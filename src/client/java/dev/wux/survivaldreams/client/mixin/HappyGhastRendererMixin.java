package dev.wux.survivaldreams.client.mixin;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.attachments.HappyGhastAttachment;
import dev.wux.survivaldreams.client.HappyGhastRenderStateAccessor;
import net.minecraft.client.renderer.entity.HappyGhastRenderer;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhastRenderer.class)
public abstract class HappyGhastRendererMixin {

    @Unique
    private static final Identifier ANGRY_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/ghast/happy_ghast_angry.png");

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void survivalDreams$extract(HappyGhast ghast, HappyGhastRenderState state, float partialTick, CallbackInfo ci) {
        boolean isCharging = ghast.getAttachedOrElse(HappyGhastAttachment.IS_CHARGING, false);
        ((HappyGhastRenderStateAccessor) state).survivalDreams$setAngry(isCharging);
    }

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void survivalDreams$texture(HappyGhastRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (((HappyGhastRenderStateAccessor) state).survivalDreams$isAngry()) {
            cir.setReturnValue(ANGRY_TEXTURE);
        }
    }
}