package dev.wux.survivaldreams.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import dev.wux.survivaldreams.wand.ClientWandState;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMoonMixin {

    private static final Identifier CHEESE_SPRITE = Identifier.withDefaultNamespace("moon/cheese_moon");

    @Unique
    private GpuBuffer survivaldreams$cheeseBuffer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void survivaldreams$buildCheeseQuad(TextureManager textureManager, AtlasManager atlasManager, CallbackInfo ci) {
        TextureAtlas atlas = ((SkyRendererAccessor) this).survivaldreams$getCelestialsAtlas();
        TextureAtlasSprite sprite = atlas.getSprite(CHEESE_SPRITE);
        this.survivaldreams$cheeseBuffer = SkyRendererAccessor.survivaldreams$buildCelestialQuad("Cheese moon quad", sprite);
    }

    @Redirect(
            method = "renderMoon(Lnet/minecraft/world/level/MoonPhase;FLcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderPass;setVertexBuffer(ILcom/mojang/blaze3d/buffers/GpuBuffer;)V"
            )
    )
    private void survivaldreams$redirectMoonBuffer(RenderPass renderPass, int slot, GpuBuffer originalBuffer) {
        if (ClientWandState.isActive()) {
            renderPass.setVertexBuffer(slot, this.survivaldreams$cheeseBuffer);
        } else {
            renderPass.setVertexBuffer(slot, originalBuffer);
        }
    }

    @ModifyArgs(
            method = "renderMoon(Lnet/minecraft/world/level/MoonPhase;FLcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"
            )
    )
    private void survivaldreams$redirectMoonIndex(Args args) {
        if (ClientWandState.isActive()) {
            args.set(0, 0);
        }
    }
}