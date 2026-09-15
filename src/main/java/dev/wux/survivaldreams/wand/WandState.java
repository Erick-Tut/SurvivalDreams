package dev.wux.survivaldreams.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class WandState extends SavedData {

    public static final long DURATION_TICKS = 24000L;
    public static final long COOLDOWN_TICKS = 48000L;

    public static final SavedDataType<WandState> TYPE = new SavedDataType<>(
            "survivaldreams_wand_state",
            WandState::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.fieldOf("active_until").forGetter(s -> s.activeUntilGameTime),
                    Codec.LONG.fieldOf("cooldown_until").forGetter(s -> s.cooldownUntilGameTime)
            ).apply(instance, WandState::new)),
            DataFixTypes.LEVEL
    );

    private long activeUntilGameTime;
    private long cooldownUntilGameTime;

    public WandState() {
        this(0L, 0L);
    }

    private WandState(long activeUntilGameTime, long cooldownUntilGameTime) {
        this.activeUntilGameTime = activeUntilGameTime;
        this.cooldownUntilGameTime = cooldownUntilGameTime;
    }

    public static WandState get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean isActive(long currentGameTime) {
        return currentGameTime < activeUntilGameTime;
    }

    public boolean isOnCooldown(long currentGameTime) {
        return currentGameTime < cooldownUntilGameTime;
    }

    public long ticksUntilInactive(long currentGameTime) {
        return Math.max(0L, activeUntilGameTime - currentGameTime);
    }

    public long ticksUntilReusable(long currentGameTime) {
        return Math.max(0L, cooldownUntilGameTime - currentGameTime);
    }

    public boolean tryActivate(long currentGameTime) {
        if (isOnCooldown(currentGameTime)) {
            return false;
        }
        this.activeUntilGameTime = currentGameTime + DURATION_TICKS;
        this.cooldownUntilGameTime = currentGameTime + COOLDOWN_TICKS;
        this.setDirty();
        return true;
    }
}