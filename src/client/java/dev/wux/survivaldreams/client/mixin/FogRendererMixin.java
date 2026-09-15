package dev.wux.survivaldreams.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(
            method = "computeFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)Lorg/joml/Vector4f;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void survivaldreams$forceBlackFog(
            Camera camera,
            float f,
            ClientLevel clientLevel,
            int i,
            float g,
            CallbackInfoReturnable<Vector4f> cir) {
        cir.setReturnValue(new Vector4f(0.0F, 0.0F, 0.0F, 1.0F));
    }
}