package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.entity.Firesnowgolem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class GolemGroupAlertHandler {

    private static final double GROUP_ALERT_RADIUS = 16.0;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof Mob hitMob)) return;
            if (!(hitMob instanceof IronGolem) && !(hitMob instanceof Firesnowgolem)) return;
            if (!(damageSource.getEntity() instanceof Player player)) return;
            if (!(hitMob.level() instanceof ServerLevel serverLevel)) return;

            alertGolem(hitMob, player);

            AABB groupArea = hitMob.getBoundingBox().inflate(GROUP_ALERT_RADIUS);
            for (Mob nearby : serverLevel.getEntitiesOfClass(Mob.class, groupArea)) {
                if (nearby.getType() == hitMob.getType()) {
                    alertGolem(nearby, player);
                }
            }
        });
    }

    private static void alertGolem(Mob golem, Player player) {
        if (golem.getTarget() == null) {
            golem.setTarget(player);
        }
    }
}