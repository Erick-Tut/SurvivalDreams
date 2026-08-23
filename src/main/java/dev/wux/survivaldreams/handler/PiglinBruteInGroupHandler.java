package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;

public class PiglinBruteInGroupHandler {

    private static final float CHANCE_BRUTE_IN_GROUP = 0.20F;
    private static final double GROUP_CHECK_RADIUS = 16.0;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Piglin piglin)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;

            boolean alreadyHasBrute = !serverLevel.getEntitiesOfClass(
                    PiglinBrute.class,
                    piglin.getBoundingBox().inflate(GROUP_CHECK_RADIUS)
            ).isEmpty();
            if (alreadyHasBrute) return;

            if (serverLevel.random.nextFloat() < CHANCE_BRUTE_IN_GROUP) {
                PiglinBrute brute = EntityType.PIGLIN_BRUTE.create(serverLevel, EntitySpawnReason.NATURAL);
                if (brute == null) return;

                brute.snapTo(piglin.getX(), piglin.getY(), piglin.getZ(), piglin.getYRot(), 0.0F);
                brute.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(piglin.blockPosition()), EntitySpawnReason.NATURAL, null);
                serverLevel.addFreshEntity(brute);
            }
        });
    }
}