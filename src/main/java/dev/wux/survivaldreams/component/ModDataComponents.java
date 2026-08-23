package dev.wux.survivaldreams.component;

import dev.wux.survivaldreams.SurvivalDreams;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;

public class ModDataComponents {

    public static final DataComponentType<Unit> WILDFIRE_REINFORCED = register(
            "wildfire_reinforced",
            DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC)
                    .build()
    );

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        ResourceKey<DataComponentType<?>> key = ResourceKey.create(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name)
        );
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, type);
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando data components de " + SurvivalDreams.MOD_ID);
    }
}