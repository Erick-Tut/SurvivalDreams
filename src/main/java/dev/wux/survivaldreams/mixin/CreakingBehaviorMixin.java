package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.monster.creaking.Creaking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creaking.class)
public abstract class CreakingBehaviorMixin {

    @Inject(method = "checkCanMove", at = @At("RETURN"), cancellable = true)
    private void survivalDreams$alwaysCanMove(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}