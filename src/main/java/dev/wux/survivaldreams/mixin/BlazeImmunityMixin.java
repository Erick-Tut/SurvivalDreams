package dev.wux.survivaldreams.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class BlazeImmunityMixin {

    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$cannotFreeze(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Blaze) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$noSnowballDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof Blaze)) return;

        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof Snowball) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}