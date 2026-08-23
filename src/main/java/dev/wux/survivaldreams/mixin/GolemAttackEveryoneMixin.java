package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class GolemAttackEveryoneMixin {

    @Inject(method = "canAttackType", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$attackEveryone(EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }
}