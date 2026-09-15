package dev.wux.survivaldreams.night;

import dev.wux.survivaldreams.wand.WandState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.WeakHashMap;

public final class NightCycleManager {

    public static final long CYCLE_LENGTH_TICKS = 24000L;

    private static final long NIGHT_START_TICK = 13400L;
    private static final long NIGHT_END_TICK = 22600L;
    private static final long NIGHT_RANGE = NIGHT_END_TICK - NIGHT_START_TICK;
    private static final Map<ServerLevel, Long> progressByLevel = new WeakHashMap<>();
    private static final Map<ServerLevel, Long> cycleCountByLevel = new WeakHashMap<>();

    private static final float[] PROGRESS_POINTS  = {0.00f, 0.35f, 0.55f, 0.80f, 0.92f, 1.00f};
    private static final float[] INTENSITY_POINTS = {0.20f, 0.35f, 0.55f, 0.80f, 1.00f, 0.20f};

    private NightCycleManager() {}

    public static void tick(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) return;

        if (!progressByLevel.containsKey(level)) {
            long savedDayTime = level.getDayTime();
            long initialCycleCount = Math.floorDiv(savedDayTime, CYCLE_LENGTH_TICKS);
            long remainder = Math.floorMod(savedDayTime, CYCLE_LENGTH_TICKS);
            long initialProgress;
            if (remainder >= NIGHT_START_TICK && remainder <= NIGHT_END_TICK) {
                initialProgress = (remainder - NIGHT_START_TICK) * CYCLE_LENGTH_TICKS / NIGHT_RANGE;
            } else {
                initialProgress = 0L;
            }
            progressByLevel.put(level, initialProgress);
            cycleCountByLevel.put(level, initialCycleCount);
        }

        long progress = progressByLevel.get(level);
        long cycleCount = cycleCountByLevel.get(level);

        progress++;
        if (progress >= CYCLE_LENGTH_TICKS) {
            progress = 0L;
            cycleCount++;
        }
        progressByLevel.put(level, progress);
        cycleCountByLevel.put(level, cycleCount);

        long dayTime = (cycleCount * CYCLE_LENGTH_TICKS) + NIGHT_START_TICK + (progress * NIGHT_RANGE / CYCLE_LENGTH_TICKS);
        level.setDayTime(dayTime);
    }

    public static float getProgress(ServerLevel level) {
        long progress = progressByLevel.getOrDefault(level, 0L);
        return progress / (float) CYCLE_LENGTH_TICKS;
    }

    public static NightCyclePhase getPhase(ServerLevel level) {
        return NightCyclePhase.fromProgress(getProgress(level));
    }

    public static float getIntensity(ServerLevel level) {
        float intensity = computeIntensity(getProgress(level));
        if (WandState.get(level).isActive(level.getGameTime())) {
            intensity = Math.max(0.0f, intensity - 0.05f);
        }
        return intensity;
    }

    private static float computeIntensity(float progress) {
        for (int i = 0; i < PROGRESS_POINTS.length - 1; i++) {
            float p0 = PROGRESS_POINTS[i];
            float p1 = PROGRESS_POINTS[i + 1];
            if (progress >= p0 && progress <= p1) {
                float t = (progress - p0) / (p1 - p0);
                float smooth = t * t * (3f - 2f * t);
                float i0 = INTENSITY_POINTS[i];
                float i1 = INTENSITY_POINTS[i + 1];
                return i0 + (i1 - i0) * smooth;
            }
        }
        return INTENSITY_POINTS[0];
    }
}