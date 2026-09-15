package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.resources.Identifier;

public class MigoGlassesLayer extends RenderLayer<LlamaRenderState, LlamaModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            SurvivalDreams.MOD_ID,
            "textures/entity/llama/migo_glasses.png"
    );

    public MigoGlassesLayer(RenderLayerParent<LlamaRenderState, LlamaModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, LlamaRenderState llamaRenderState, float f, float g) {
        if (llamaRenderState instanceof MigoLlamaRenderState migoState && migoState.isMigo) {
            coloredCutoutModelCopyLayerRender(
                    this.getParentModel(),
                    TEXTURE,
                    poseStack,
                    submitNodeCollector,
                    i,
                    llamaRenderState,
                    -1,
                    0
            );
        }
    }
}