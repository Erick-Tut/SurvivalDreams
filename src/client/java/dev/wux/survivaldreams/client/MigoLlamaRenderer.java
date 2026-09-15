package dev.wux.survivaldreams.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.world.entity.animal.equine.Llama;
import org.jspecify.annotations.NonNull;

public class MigoLlamaRenderer extends LlamaRenderer {

    public MigoLlamaRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY);
        this.addLayer(new MigoGlassesLayer(this));
    }

    @Override
    public MigoLlamaRenderState createRenderState() {
        return new MigoLlamaRenderState();
    }

    @Override
    public void extractRenderState(@NonNull Llama llama, @NonNull LlamaRenderState state, float partialTick) {
        super.extractRenderState(llama, state, partialTick);
        ((MigoLlamaRenderState) state).isMigo = llama.hasCustomName()
                && llama.getCustomName() != null
                && llama.getCustomName().getString().equals("Migo");
    }
}