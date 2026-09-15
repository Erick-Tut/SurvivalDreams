package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class ImmunityMixin {

    @Unique
    private static final Set<Holder<MobEffect>> survivalDreams$BLOCKED_EFFECTS = Set.of(
            MobEffects.SLOWNESS,
            MobEffects.MINING_FATIGUE,
            MobEffects.INSTANT_DAMAGE,
            MobEffects.NAUSEA,
            MobEffects.HUNGER,
            MobEffects.POISON,
            MobEffects.WEAKNESS,
            MobEffects.WITHER,
            MobEffects.OOZING,
            MobEffects.INFESTED,
            MobEffects.WEAVING,
            MobEffects.WIND_CHARGED,
            ModEffects.VOIDED_HOLDER,
            ModEffects.AMBUSH_HOLDER
    );

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void survivalDreams$blockImmuneEffects(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModEffects.IMMUNITY_HOLDER) && survivalDreams$BLOCKED_EFFECTS.contains(mobEffectInstance.getEffect())) {
            cir.setReturnValue(false);
        }
    }
}