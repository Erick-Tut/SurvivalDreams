package dev.wux.survivaldreams.handler.enchant;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantSpec(ResourceKey<Enchantment> key, int maxLevel, int pickWeight) {

    public static EnchantSpec of(ResourceKey<Enchantment> key, int maxLevel, int pickWeight) {
        return new EnchantSpec(key, maxLevel, pickWeight);
    }
}