package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlazeShieldDisableHandler {

    private static final int DISABLE_TICKS = 100;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            if (!blocked) return;

            Entity attacker = source.getEntity();
            Entity directAttacker = source.getDirectEntity();
            boolean isBlazeAttack = attacker instanceof Blaze || directAttacker instanceof SmallFireball;

            if (isBlazeAttack) {
                player.getCooldowns().addCooldown(new ItemStack(Items.SHIELD), DISABLE_TICKS);
                player.stopUsingItem();
            }
        });
    }
}