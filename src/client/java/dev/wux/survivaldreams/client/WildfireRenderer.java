package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.entity.Wildfire;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class WildfireRenderer extends MobRenderer<Wildfire, WildfireRenderState, WildfireModel> {

    private static final Identifier WILDFIRE_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/wildfire/wildfire.png");

    public WildfireRenderer(EntityRendererProvider.Context context) {
        super(context, new WildfireModel(context.bakeLayer(ModEntityModelLayers.WILDFIRE)), 0.5F);
    }

    @Override
    protected int getBlockLightLevel(Wildfire wildfire, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTextureLocation(WildfireRenderState state) {
        return WILDFIRE_TEXTURE;
    }

    @Override
    public WildfireRenderState createRenderState() {
        return new WildfireRenderState();
    }

    @Override
    public void extractRenderState(Wildfire wildfire, WildfireRenderState state, float partialTick) {
        super.extractRenderState(wildfire, state, partialTick);
        state.brokenShieldsMask = wildfire.getBrokenShieldsMask();
    }
}