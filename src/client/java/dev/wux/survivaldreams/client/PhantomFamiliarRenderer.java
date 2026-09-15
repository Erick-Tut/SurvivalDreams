package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.resources.Identifier;

public class PhantomFamiliarRenderer extends PhantomRenderer {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            SurvivalDreams.MOD_ID,
            "textures/entity/phantom_familiar/phantom_familiar.png"
    );

    public PhantomFamiliarRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.removeIf(layer -> layer instanceof PhantomEyesLayer);
        this.addLayer(new PhantomFamiliarEyesLayer(this));
    }

    @Override
    public Identifier getTextureLocation(PhantomRenderState state) {
        return TEXTURE;
    }
}