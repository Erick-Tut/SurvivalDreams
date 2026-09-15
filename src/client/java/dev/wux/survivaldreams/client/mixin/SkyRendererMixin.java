package dev.wux.survivaldreams.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    @ModifyVariable(method = "renderSkyDisc(I)V", at = @At("HEAD"), argsOnly = true)
    private int survivaldreams$forceBlackSkyDisc(int original) {
        return 0xFF000000;
    }

    @Inject(
            method = "renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;FI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void survivaldreams$cancelSunriseGlow(PoseStack poseStack, float f, int i, CallbackInfo ci) {
        ci.cancel();
    }
}