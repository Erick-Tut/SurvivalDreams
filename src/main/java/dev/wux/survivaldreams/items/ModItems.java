package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.SurvivalDreams;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import dev.wux.survivaldreams.ModEffects;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.function.Function;

public class ModItems {

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name)
        );
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static final Item POTEM = register(
            "potem",
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .component(
                            DataComponents.DEATH_PROTECTION,
                            new DeathProtection(List.of(
                                    new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.REGENERATION, 900, 1),
                                            1.0f
                                    ),
                                    new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.ABSORPTION, 100, 1),
                                            1.0f
                                    ),
                                    new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0),
                                            1.0f
                                    )
                            ))
                    )
    );

    public static final Item CHEART = register(
            "cheart",
            CheartItem::new,
            new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.6f)
                            .alwaysEdible()
                            .build())
    );

    public static final Item ENDER_KEY = register(
            "ender_key",
            EnderKeyItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()
    );

    public static final Item WILDFIRE_CORE = register(
            "wildfire_core",
            Item::new,
            new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .component(
                            DataComponents.LORE,
                            new net.minecraft.world.item.component.ItemLore(List.of(
                                    net.minecraft.network.chat.Component.translatable("item.survival-dreams.wildfire_core.applies_to"),
                                    net.minecraft.network.chat.Component.translatable("item.survival-dreams.wildfire_core.applies_to.armor")
                            ))
                    )
    );

    public static final Item LUNAR_WAND = register(
            "lunar_wand",
            LunarWand::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
    );

    public static final Item CRIMSON_MEMBRANE = register(
            "crimson_membrane",
            Item::new,
            new Item.Properties()
                    .rarity(Rarity.RARE)
    );

    public static final Item CAPRA_CHEESE = register(
            "cheese_capra",
            Item::new,
            new Item.Properties()
                    .rarity(Rarity.RARE)
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(1.4f)
                            .alwaysEdible()
                            .build())
                    .component(
                            DataComponents.CONSUMABLE,
                            net.minecraft.world.item.component.Consumable.builder()
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.REGENERATION, 260, 2),
                                            1.0f
                                    ))
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1),
                                            1.0f
                                    ))
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(ModEffects.IMMUNITY_HOLDER, 3000, 0),
                                            1.0f
                                    ))
                                    .build()
                    )
    );

    public static final Item MYSTERY_BOTTLE = register(
            "mystery_bottle",
            MysteryBottleItem::new,
            new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)
                    .usingConvertsTo(Items.GLASS_BOTTLE)
                    .component(
                            DataComponents.CONSUMABLE,
                            net.minecraft.world.item.component.Consumable.builder()
                                    .animation(net.minecraft.world.item.ItemUseAnimation.DRINK)
                                    .sound(net.minecraft.sounds.SoundEvents.GENERIC_DRINK)
                                    .hasConsumeParticles(false)
                                    .consumeSeconds(1.6f)
                                    .build()
                    )
    );

    public static final Item MYSTERY_DUST = register(
            "mystery_dust",
            Item::new,
            new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
    );

    public static final Item SEALED_END_PORTAL_FRAME_ITEM = register(
            "sealed_end_portal_frame",
            props -> new BlockItem(dev.wux.survivaldreams.block.ModBlocks.SEALED_END_PORTAL_FRAME, props),
            new Item.Properties()
    );

    public static final Item KEYHOLE_ITEM = register(
            "keyhole",
            props -> new BlockItem(dev.wux.survivaldreams.block.ModBlocks.KEYHOLE, props),
            new Item.Properties()
    );

    public static final Item END_PORTAL_ANCHOR = register(
            "end_portal_anchor",
            props -> new BlockItem(dev.wux.survivaldreams.block.ModBlocks.END_PORTAL_ANCHOR, props),
            new Item.Properties()
    );

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando items de " + SurvivalDreams.MOD_ID);
    }
}