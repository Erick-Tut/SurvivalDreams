package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.attachments.ModAttachments;
import dev.wux.survivaldreams.items.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private boolean survivalDreams$hadPotem = false;

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"))
    private void survivalDreams$beforeCheckTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        survivalDreams$hadPotem = false;

        if (!(self instanceof Player player)) {
            return;
        }

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == ModItems.POTEM) {
                survivalDreams$hadPotem = true;
                break;
            }
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private void survivalDreams$afterCheckTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !survivalDreams$hadPotem) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) {
            return;
        }

        int current = player.getAttachedOrElse(ModAttachments.BLOCKED_SLOTS, 0);
        player.setAttached(ModAttachments.BLOCKED_SLOTS, current + 1);

        var maxHealthAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            double newMax = Math.max(2.0, maxHealthAttr.getBaseValue() - 2.0);
            maxHealthAttr.setBaseValue(newMax);

            if (player.getHealth() > (float) newMax) {
                player.setHealth((float) newMax);
            }
        }
    }
}