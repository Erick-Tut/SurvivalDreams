package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.night.NightCycleManager;
import dev.wux.survivaldreams.night.NightCyclePhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;

public final class PhantomFamiliarSpawner {

    private static final int CHECK_INTERVAL_TICKS = 100;
    private static final float SPAWN_CHANCE = 0.10F;
    private static final double SPAWN_RADIUS = 24.0D;
    private static final double MIN_SPAWN_DISTANCE = 15.0D;

    private static final double MIN_SPAWN_HEIGHT_ABOVE = 3.0D;
    private static final int SPAWN_HEIGHT_RANDOM_RANGE = 5;

    private PhantomFamiliarSpawner() {}

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(PhantomFamiliarSpawner::tick);
    }

    private static void tick(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) return;
        if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0) return;
        if (NightCycleManager.getPhase(level) != NightCyclePhase.NOCHE_PROFUNDA) return;

        for (ServerPlayer player : level.players()) {
            if (level.random.nextFloat() >= SPAWN_CHANCE) continue;

            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = MIN_SPAWN_DISTANCE + level.random.nextDouble() * (SPAWN_RADIUS - MIN_SPAWN_DISTANCE);
            double x = player.getX() + Math.cos(angle) * dist;
            double z = player.getZ() + Math.sin(angle) * dist;
            double y = player.getY() + MIN_SPAWN_HEIGHT_ABOVE + level.random.nextInt(SPAWN_HEIGHT_RANDOM_RANGE);

            BlockPos pos = BlockPos.containing(x, y, z);
            if (!level.canSeeSky(pos)) continue;

            PhantomFamiliar familiar = ModEntityTypes.PHANTOM_FAMILIAR.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (familiar == null) continue;

            familiar.snapTo(x, y, z, level.random.nextFloat() * 360F, 0F);
            level.addFreshEntity(familiar);
        }
    }
}