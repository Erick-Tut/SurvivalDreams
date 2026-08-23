package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.entity.Wildfire;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class WildfireShieldHandler {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(entity instanceof Wildfire wildfire)) return InteractionResult.PASS;

            if (wildfire.shieldsRemaining() <= 0) {
                return InteractionResult.PASS;
            }

            int shieldIndex = wildfire.findNearestShield(player.position());
            if (shieldIndex == -1) {
                return InteractionResult.PASS;
            }

            float damage = getAttackDamage(player);
            wildfire.damageShield(shieldIndex, damage);

            return InteractionResult.FAIL;
        });
    }

    private static float getAttackDamage(Player player) {
        var attr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        return attr != null ? (float) attr.getValue() : 1.0F;
    }
}