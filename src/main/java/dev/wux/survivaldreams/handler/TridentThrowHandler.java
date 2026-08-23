package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TridentThrowHandler {

    private static final double SEARCH_RADIUS = 20.0;
    private static final double MIN_THROW_DISTANCE = 3.0;
    private static final double MAX_THROW_DISTANCE = 12.0;
    private static final int THROW_COOLDOWN_TICKS = 40;

    private static final Map<UUID, Integer> THROW_COOLDOWN = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            java.util.Set<Mob> nearbyMobs = new java.util.HashSet<>();

            for (Player player : level.players()) {
                if (player.isCreative() || player.isSpectator()) continue;
                AABB searchBox = player.getBoundingBox().inflate(SEARCH_RADIUS);
                nearbyMobs.addAll(level.getEntitiesOfClass(Mob.class, searchBox));
            }

            for (Mob mob : nearbyMobs) {

                ItemStack mainHand = mob.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!mainHand.is(Items.TRIDENT)) continue;

                LivingEntity target = mob.getTarget();
                if (target == null) continue;

                double distSqr = mob.distanceToSqr(target);
                if (distSqr < MIN_THROW_DISTANCE * MIN_THROW_DISTANCE
                        || distSqr > MAX_THROW_DISTANCE * MAX_THROW_DISTANCE) continue;

                UUID uuid = mob.getUUID();
                int cooldown = THROW_COOLDOWN.getOrDefault(uuid, 0);
                if (cooldown > 0) {
                    THROW_COOLDOWN.put(uuid, cooldown - 1);
                    continue;
                }

                throwTrident(mob, target, mainHand, level);
                THROW_COOLDOWN.put(uuid, THROW_COOLDOWN_TICKS);
            }
        });
    }

    private static void throwTrident(Mob mob, LivingEntity target, ItemStack tridentStack, ServerLevel level) {
        ThrownTrident thrown = new ThrownTrident(level, mob, tridentStack.copy());

        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.3333333333333333) - thrown.getY();
        double dz = target.getZ() - mob.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        thrown.shoot(dx, dy + horizontalDist * 0.2, dz, 1.6F, 1.0F);
        mob.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
        level.addFreshEntity(thrown);
    }
}