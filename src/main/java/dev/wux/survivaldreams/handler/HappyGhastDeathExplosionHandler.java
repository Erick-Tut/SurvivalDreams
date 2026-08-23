package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public class HappyGhastDeathExplosionHandler {

    private static final float EXPLOSION_POWER = 8.0F;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof HappyGhast ghast)) return;
            if (!(ghast.level() instanceof ServerLevel serverLevel)) return;

            serverLevel.explode(
                    ghast,
                    ghast.getX(), ghast.getY(0.5), ghast.getZ(),
                    EXPLOSION_POWER,
                    Level.ExplosionInteraction.MOB
            );

            AreaEffectCloud cloud = new AreaEffectCloud(
                    serverLevel, ghast.getX(), ghast.getY(0.5), ghast.getZ()
            );
            cloud.setRadius(4.0F);
            cloud.setDuration(100);
            cloud.setRadiusPerTick((7.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1));

            serverLevel.addFreshEntity(cloud);
            serverLevel.levelEvent(null, 1016, ghast.blockPosition(), 0);
        });
    }
}