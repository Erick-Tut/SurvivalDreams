package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.entity.Endersent;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class EndersentRenderer extends MobRenderer<Endersent, EndersentRenderState, EndersentModel> {

    private static final Identifier ENDERSENT_TEXTURE =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/endersent/endersent.png");

    public EndersentRenderer(EntityRendererProvider.Context context) {
        super(context, new EndersentModel(context.bakeLayer(ModEntityModelLayers.ENDERSENT)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(EndersentRenderState state) {
        return ENDERSENT_TEXTURE;
    }

    @Override
    public EndersentRenderState createRenderState() {
        return new EndersentRenderState();
    }

    @Override
    public void extractRenderState(Endersent entity, EndersentRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.attackType = entity.swinging ? entity.getAttackType() : Endersent.ATTACK_NONE;
        state.attackTime = entity.getAttackAnim(partialTick);
    }
}