package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

public class PaleGardenBlindnessHandler {

    private static final int BLINDNESS_DURATION = 220;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            if (level.getGameTime() % 100 != 0) return;

            ResourceKey<Biome> paleGardenKey = ResourceKey.create(Registries.BIOME, Identifier.withDefaultNamespace("pale_garden"));

            for (Player player : level.players()) {
                Holder<Biome> biome = level.getBiome(player.blockPosition());
                if (biome.is(paleGardenKey)) {
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_DURATION, 0));
                }
            }
        });
    }
}