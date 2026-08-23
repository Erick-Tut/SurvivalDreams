package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.mixin.CatVariantAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;

import java.util.Optional;

public class CatVariantHandler {

    private static final float DESKI_CHANCE = 0.08F;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Cat cat)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;

            if (serverLevel.random.nextFloat() >= DESKI_CHANCE) return;

            try {
                Registry<CatVariant> variantRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.CAT_VARIANT);
                ResourceKey<CatVariant> deskiKey = ResourceKey.create(Registries.CAT_VARIANT,
                        Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "deski"));

                Optional<Holder.Reference<CatVariant>> deskiOptional = variantRegistry.get(deskiKey);
                if (deskiOptional.isEmpty()) {
                    SurvivalDreams.LOGGER.warn("Variante 'deski' no encontrada en el registro, saltando");
                    return;
                }

                ((CatVariantAccessor) cat).survivalDreams$setVariant(deskiOptional.get());
            } catch (Exception e) {
                SurvivalDreams.LOGGER.error("Error al forzar variante de gato deski", e);
            }
        });
    }
}