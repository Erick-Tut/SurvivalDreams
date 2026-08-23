package dev.wux.survivaldreams.client.mixin;

import dev.wux.survivaldreams.client.WuxFoxRenderStateAccessor;
import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.attachments.WuxFoxAttachment;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.fox.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxRenderer.class)
public abstract class FoxRendererMixin {

    @Unique
    private static final Identifier WUX_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/fox/wux.png");

    @Unique
    private static final Identifier WUX_SLEEP_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/fox/wux_sleep.png");

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void survivalDreams$extract(Fox fox, FoxRenderState state, float partialTick, CallbackInfo ci) {
        boolean isWux = fox.getAttachedOrElse(WuxFoxAttachment.IS_WUX, false);
        ((WuxFoxRenderStateAccessor) state).survivalDreams$setWuxFox(isWux);
    }

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void survivalDreams$texture(FoxRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (((WuxFoxRenderStateAccessor) state).survivalDreams$isWuxFox()) {
            cir.setReturnValue(state.isSleeping ? WUX_SLEEP_TEXTURE : WUX_TEXTURE);
        }
    }
}