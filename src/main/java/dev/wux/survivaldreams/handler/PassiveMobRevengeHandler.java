package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.entity.Firesnowgolem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PassiveMobRevengeHandler {

    private static final double GROUP_ALERT_RADIUS = 16.0;
    private static final double DETECTION_RADIUS = 32.0;
    private static final double BITE_RANGE = 2.2;
    private static final int BITE_COOLDOWN_TICKS = 20;
    private static final int POISON_DURATION = 100;
    private static final int POISON_AMPLIFIER = 1;
    private static final int FORGET_TICKS = 100;
    private static final double CHASE_SPEED = 1.6;

    private static final Set<UUID> ENGAGED = new HashSet<>();
    private static final Map<UUID, Integer> BITE_COOLDOWN = new HashMap<>();
    private static final Map<UUID, Integer> LOST_SIGHT_TICKS = new HashMap<>();

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof Mob mob)) return;
            if (!(damageSource.getEntity() instanceof Player)) return;
            if (mob instanceof Wolf) return;
            if (!isEligible(mob)) return;
            if (!(mob.level() instanceof ServerLevel serverLevel)) return;

            engage(mob);

            AABB groupArea = mob.getBoundingBox().inflate(GROUP_ALERT_RADIUS);
            for (Mob nearby : serverLevel.getEntitiesOfClass(Mob.class, groupArea)) {
                if (nearby.getType() == mob.getType() && isEligible(nearby)) {
                    engage(nearby);
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (Player player : level.players()) {
                if (player.isCreative() || player.isSpectator()) continue;

                AABB area = player.getBoundingBox().inflate(DETECTION_RADIUS);
                for (Mob mob : level.getEntitiesOfClass(Mob.class, area)) {
                    UUID uuid = mob.getUUID();
                    if (!ENGAGED.contains(uuid)) continue;
                    if (mob instanceof Wolf) continue;

                    boolean canSee = mob.hasLineOfSight(player);

                    if (canSee) {
                        LOST_SIGHT_TICKS.remove(uuid);
                    } else {
                        int lostTicks = LOST_SIGHT_TICKS.getOrDefault(uuid, 0) + 1;
                        if (lostTicks >= FORGET_TICKS) {
                            disengage(mob);
                            continue;
                        }
                        LOST_SIGHT_TICKS.put(uuid, lostTicks);
                    }

                    mob.setTarget(player);
                    mob.setSprinting(true);

                    if (mob.tickCount % 5 == 0) {
                        mob.getNavigation().moveTo(player, CHASE_SPEED);
                    }

                    double distSqr = mob.distanceToSqr(player);
                    if (distSqr <= BITE_RANGE * BITE_RANGE) {
                        int cooldown = BITE_COOLDOWN.getOrDefault(uuid, 0);
                        if (cooldown <= 0) {
                            biteWithPoison(mob, player);
                            BITE_COOLDOWN.put(uuid, BITE_COOLDOWN_TICKS);
                        }
                    }

                    BITE_COOLDOWN.merge(uuid, -1, (old, dec) -> Math.max(0, old + dec));
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof Player player)) return;
            if (!(damageSource.getEntity() instanceof Wolf)) return;
            player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
        });
    }

    private static void engage(Mob mob) {
        ENGAGED.add(mob.getUUID());
        LOST_SIGHT_TICKS.remove(mob.getUUID());
    }

    private static void disengage(Mob mob) {
        UUID uuid = mob.getUUID();
        ENGAGED.remove(uuid);
        BITE_COOLDOWN.remove(uuid);
        LOST_SIGHT_TICKS.remove(uuid);
        mob.setTarget(null);
        mob.setSprinting(false);
    }

    private static void biteWithPoison(Mob mob, Player player) {
        var attackDamageAttr = mob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float damage = attackDamageAttr != null ? (float) attackDamageAttr.getValue() : 2.0F;

        player.hurt(mob.damageSources().mobAttack(mob), damage);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
    }

    private static boolean isEligible(Mob mob) {
        if (mob instanceof IronGolem) return false;
        if (mob instanceof Firesnowgolem) return false;

        MobCategory category = mob.getType().getCategory();
        return category == MobCategory.CREATURE
                || category == MobCategory.AMBIENT
                || category == MobCategory.WATER_CREATURE
                || category == MobCategory.WATER_AMBIENT
                || category == MobCategory.UNDERGROUND_WATER_CREATURE
                || category == MobCategory.AXOLOTLS
                || category == MobCategory.MISC;
    }
}