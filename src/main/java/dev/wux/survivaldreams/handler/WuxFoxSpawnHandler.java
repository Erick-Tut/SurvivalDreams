package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.attachments.WuxFoxAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class WuxFoxSpawnHandler {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Fox fox)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (fox.getAttachedOrElse(WuxFoxAttachment.IS_WUX, false)) return;

            Holder<Biome> biome = serverLevel.getBiome(fox.blockPosition());
            boolean isArcticBiome = biome.is(Biomes.FROZEN_PEAKS)
                    || biome.is(Biomes.JAGGED_PEAKS)
                    || biome.is(Biomes.SNOWY_PLAINS)
                    || biome.is(Biomes.ICE_SPIKES)
                    || biome.is(Biomes.SNOWY_SLOPES);

            if (isArcticBiome) {
                fox.setAttached(WuxFoxAttachment.IS_WUX, true);
            }
        });
    }
}