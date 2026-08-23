package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.attachments.ZombifiedPiglinAttachment;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ZombifiedPiglinRandomAggroHandler {

    private static final double DETECTION_RADIUS = 24.0;
    private static final float CHANCE_PER_TICK = 1.0F / 600.0F;
    private static final float CHANCE_EXPLOSIVE = 0.10F;
    private static final float EXPLOSION_POWER = 4.0F;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof ZombifiedPiglin piglin)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;

            if (piglin.getAttachedOrElse(ZombifiedPiglinAttachment.IS_EXPLOSIVE, false)) return;
            if (piglin.getAttachedOrElse(ZombifiedPiglinAttachment.DECIDED, false)) return;

            piglin.setAttached(ZombifiedPiglinAttachment.DECIDED, true);

            if (serverLevel.random.nextFloat() < CHANCE_EXPLOSIVE) {
                piglin.setAttached(ZombifiedPiglinAttachment.IS_EXPLOSIVE, true);
                piglin.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TNT));
                piglin.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (Player player : level.players()) {
                if (player.isCreative() || player.isSpectator()) continue;

                AABB area = player.getBoundingBox().inflate(DETECTION_RADIUS);
                for (ZombifiedPiglin piglin : level.getEntitiesOfClass(ZombifiedPiglin.class, area)) {
                    if (piglin.getTarget() != null) continue;

                    boolean isExplosive = piglin.getAttachedOrElse(ZombifiedPiglinAttachment.IS_EXPLOSIVE, false);
                    if (isExplosive || level.random.nextFloat() < CHANCE_PER_TICK) {
                        piglin.setTarget(player);
                    }
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof Player)) return;
            if (!(damageSource.getEntity() instanceof ZombifiedPiglin piglin)) return;
            if (!piglin.getAttachedOrElse(ZombifiedPiglinAttachment.IS_EXPLOSIVE, false)) return;
            if (!(piglin.level() instanceof ServerLevel serverLevel)) return;

            serverLevel.explode(
                    piglin,
                    piglin.getX(), piglin.getY(), piglin.getZ(),
                    EXPLOSION_POWER,
                    false,
                    Level.ExplosionInteraction.MOB
            );
            piglin.discard();
        });
    }
}