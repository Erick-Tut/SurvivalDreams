package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.entity.ModEntityTypes;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class ModSpawnEggs {

    public static final Item WATCHLING_SPAWN_EGG =
            registerSpawnEgg("watchling_spawn_egg", ModEntityTypes.WATCHLING);

    public static final Item ENDERSENT_SPAWN_EGG =
            registerSpawnEgg("endersent_spawn_egg", ModEntityTypes.ENDERSENT_DEFAULT);

    public static final Item WILDFIRE_SPAWN_EGG =
            registerSpawnEgg("wildfire_spawn_egg", ModEntityTypes.WILDFIRE);

    public static final Item ILLUSIONER_SPAWN_EGG =
            registerSpawnEgg("illusioner_spawn_egg", EntityType.ILLUSIONER);

    private static Item registerSpawnEgg(
            String name,
            net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> entityType) {

        ResourceKey<Item> key = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name)
        );

        Item spawnEgg = new SpawnEggItem(
                new Item.Properties()
                        .setId(key)
                        .spawnEgg(entityType)
        );

        return Registry.register(BuiltInRegistries.ITEM, key, spawnEgg);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(entries -> {
                    entries.accept(WATCHLING_SPAWN_EGG);
                    entries.accept(ENDERSENT_SPAWN_EGG);
                    entries.accept(WILDFIRE_SPAWN_EGG);
                    entries.accept(ILLUSIONER_SPAWN_EGG);
                });

        SurvivalDreams.LOGGER.info("[SurvivalDreams] Spawn eggs registrados");
    }
}