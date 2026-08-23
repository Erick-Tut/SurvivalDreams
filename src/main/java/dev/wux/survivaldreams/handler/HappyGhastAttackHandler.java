package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.attachments.HappyGhastAttachment;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HappyGhastAttackHandler {

    private static final double DETECTION_RADIUS = 32.0;

    private static final int PRIME_TICKS = 10;
    private static final int SHOOT_COOLDOWN_TICKS = 60;
    private static final int FORGET_TICKS = 100;

    private static final Set<UUID> ENGAGED = new HashSet<>();
    private static final Map<UUID, Integer> CHARGE_TIME = new HashMap<>();
    private static final Map<UUID, Integer> LOST_SIGHT_TICKS = new HashMap<>();

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof HappyGhast ghast)) return;
            if (ghast.isBaby()) return;
            if (!(damageSource.getEntity() instanceof Player)) return;

            ENGAGED.add(ghast.getUUID());
            LOST_SIGHT_TICKS.remove(ghast.getUUID());
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (Player player : level.players()) {
                if (player.isCreative() || player.isSpectator()) continue;

                AABB area = player.getBoundingBox().inflate(DETECTION_RADIUS);
                for (HappyGhast ghast : level.getEntitiesOfClass(HappyGhast.class, area)) {
                    if (ghast.isBaby()) continue;

                    UUID uuid = ghast.getUUID();
                    if (!ENGAGED.contains(uuid)) continue;

                    boolean canSee = ghast.hasLineOfSight(player);

                    if (!canSee) {
                        int lostTicks = LOST_SIGHT_TICKS.getOrDefault(uuid, 0) + 1;

                        if (lostTicks >= FORGET_TICKS) {
                            ENGAGED.remove(uuid);
                            CHARGE_TIME.remove(uuid);
                            LOST_SIGHT_TICKS.remove(uuid);
                            ghast.setAttached(HappyGhastAttachment.IS_CHARGING, false);
                            ghast.setSilent(false);
                        } else {
                            LOST_SIGHT_TICKS.put(uuid, lostTicks);
                        }
                        continue;
                    }

                    LOST_SIGHT_TICKS.remove(uuid);
                    ghast.setSilent(true);

                    int chargeTime = CHARGE_TIME.getOrDefault(uuid, 0);
                    chargeTime++;

                    if (chargeTime == PRIME_TICKS) {
                        level.levelEvent(null, 1015, ghast.blockPosition(), 0);
                        ghast.setAttached(HappyGhastAttachment.IS_CHARGING, true);
                    }

                    if (chargeTime >= PRIME_TICKS * 2) {
                        shootDragonFireball(ghast, player, level);
                        ghast.setAttached(HappyGhastAttachment.IS_CHARGING, false);
                        chargeTime = -SHOOT_COOLDOWN_TICKS;
                    }

                    CHARGE_TIME.put(uuid, chargeTime);
                }
            }
        });
    }

    private static void shootDragonFireball(HappyGhast ghast, Player target, ServerLevel level) {
        Vec3 direction = new Vec3(
                target.getX() - ghast.getX(),
                target.getY(0.5) - ghast.getY(0.5),
                target.getZ() - ghast.getZ()
        ).normalize();

        DragonFireball fireball = new DragonFireball(level, ghast, direction);
        fireball.setPos(ghast.getX(), ghast.getY(0.5), ghast.getZ());

        level.addFreshEntity(fireball);
        level.levelEvent(null, 1016, ghast.blockPosition(), 0);
    }
}