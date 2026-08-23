package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.entity.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.world.level.material.Fluids;

public class SurvivalDreamsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WILDFIRE, WildfireModel::createBodyLayer);
		EntityRenderers.register(ModEntityTypes.WILDFIRE, WildfireRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WATCHLING, WatchlingModel::createBodyLayer);
		EntityRenderers.register(ModEntityTypes.WATCHLING, WatchlingRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ENDERSENT, EndersentModel::createBodyLayer);
		EntityRenderers.register(ModEntityTypes.ENDERSENT, EndersentRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ENDERSENT_DEFAULT, EndersentModel::createBodyLayer);
		EntityRenderers.register(ModEntityTypes.ENDERSENT_DEFAULT, EndersentDefaultRenderer::new);

		EntityRenderers.register(ModEntityTypes.FIRESNOWGOLEM, SnowGolemRenderer::new);

		EntityRenderers.register(ModEntityTypes.VOID_CHARGE, VoidChargeRenderer::new);

		EndersentSpawnerHighlightRenderer.register();

		FluidRenderHandlerRegistry.INSTANCE.register(
				Fluids.WATER,
				Fluids.FLOWING_WATER,
				SimpleFluidRenderHandler.coloredWater(0x9900CC)
		);
	}
}