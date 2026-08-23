package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.entity.Watchling;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class WatchlingRenderer extends MobRenderer<Watchling, WatchlingRenderState, WatchlingModel> {

    private static final Identifier WATCHLING_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/watchling/watchling.png");

    public WatchlingRenderer(EntityRendererProvider.Context context) {
        super(context, new WatchlingModel(context.bakeLayer(ModEntityModelLayers.WATCHLING)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(WatchlingRenderState state) {
        return WATCHLING_TEXTURE;
    }

    @Override
    public WatchlingRenderState createRenderState() {
        return new WatchlingRenderState();
    }

    @Override
    public void extractRenderState(Watchling entity, WatchlingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isAttacking = entity.swinging;
        state.attackTime = entity.getAttackAnim(partialTick);
    }
}