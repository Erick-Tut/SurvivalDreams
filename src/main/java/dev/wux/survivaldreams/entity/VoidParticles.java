package dev.wux.survivaldreams.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;

public final class VoidParticles {

    private VoidParticles() {
    }

    public static void spawnAmbient(ServerLevel serverLevel, LivingEntity entity, float chance) {
        if (entity.getRandom().nextFloat() >= chance) return;

        double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
        double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
        double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();

        serverLevel.sendParticles(ParticleTypes.PORTAL,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
    }
}