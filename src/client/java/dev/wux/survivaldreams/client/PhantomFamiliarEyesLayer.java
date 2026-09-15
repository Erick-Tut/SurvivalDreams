package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.client.model.monster.phantom.PhantomModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class PhantomFamiliarEyesLayer extends EyesLayer<PhantomRenderState, PhantomModel> {

    private static final RenderType PHANTOM_FAMILIAR_EYES = RenderTypes.eyes(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "textures/entity/phantom_familiar/phantom_familiar_eyes.png")
    );

    public PhantomFamiliarEyesLayer(RenderLayerParent<PhantomRenderState, PhantomModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public RenderType renderType() {
        return PHANTOM_FAMILIAR_EYES;
    }
}