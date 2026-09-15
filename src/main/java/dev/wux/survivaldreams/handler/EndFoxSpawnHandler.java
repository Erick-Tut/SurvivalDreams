package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.attachments.EndFoxAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class EndFoxSpawnHandler {

    private static final float END_CHANCE = 0.10F;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Fox fox)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (fox.getAttachedOrElse(EndFoxAttachment.END_ROLLED, false)) return;

            Holder<Biome> biome = serverLevel.getBiome(fox.blockPosition());
            boolean isCherryGrove = biome.is(Biomes.CHERRY_GROVE);

            if (isCherryGrove) {
                fox.setAttached(EndFoxAttachment.END_ROLLED, true);
                if (fox.getRandom().nextFloat() < END_CHANCE) {
                    fox.setAttached(EndFoxAttachment.IS_END, true);
                }
            }
        });
    }
}