package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityVoidedMixin {

    @ModifyArg(
            method = "hurtServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V")
    )
    private float survivaldreams$applyVoidedMultiplier(float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModEffects.VOIDED_HOLDER)) {
            return amount * dev.wux.survivaldreams.effect.VoidedEffect.DAMAGE_MULTIPLIER;
        }
        return amount;
    }
}