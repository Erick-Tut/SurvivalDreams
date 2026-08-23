package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final ResourceKey<CreativeModeTab> SURVIVAL_DREAMS_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "survival_dreams_tab")
    );

    public static final CreativeModeTab SURVIVAL_DREAMS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            SURVIVAL_DREAMS_TAB_KEY,
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.survival-dreams.survival_dreams_tab"))
                    .icon(() -> new ItemStack(ModItems.KEYHOLE_ITEM))
                    .displayItems((parameters, output) -> {

                        output.accept(ModItems.POTEM);
                        output.accept(ModItems.CHEART);
                        output.accept(ModItems.ENDER_KEY);
                        output.accept(ModItems.WILDFIRE_CORE);

                        output.accept(ModItems.SEALED_END_PORTAL_FRAME_ITEM);
                        output.accept(ModItems.KEYHOLE_ITEM);
                        output.accept(ModItems.END_PORTAL_ANCHOR);

                        output.accept(ModSpawnEggs.WATCHLING_SPAWN_EGG);
                        output.accept(ModSpawnEggs.ENDERSENT_SPAWN_EGG);
                        output.accept(ModSpawnEggs.WILDFIRE_SPAWN_EGG);
                        output.accept(ModSpawnEggs.ILLUSIONER_SPAWN_EGG);
                    })
                    .build()
    );

    public static void initialize() {
        SurvivalDreams.LOGGER.info("[SurvivalDreams] Item group registrado: {}", "survival_dreams_tab");
    }
}