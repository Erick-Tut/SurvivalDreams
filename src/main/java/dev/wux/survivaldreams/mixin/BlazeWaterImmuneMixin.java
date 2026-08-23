package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.monster.Blaze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blaze.class)
public abstract class BlazeWaterImmuneMixin {

    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$notSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}