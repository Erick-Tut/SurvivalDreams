package dev.wux.survivaldreams.handler.enchant;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MobEnchantHelper {

    private MobEnchantHelper() {}

    public static final float CHANCE_TO_BE_ENCHANTED = 0.12F;

    public static final int[] ENCHANT_COUNT_WEIGHTS = {70, 25, 5};

    public static void maybeEnchant(ItemStack stack, List<EnchantSlot> pool, ServerLevel level, RandomSource random) {
        if (pool == null || pool.isEmpty()) return;
        if (random.nextFloat() >= CHANCE_TO_BE_ENCHANTED) return;

        int count = rollEnchantCount(random, ENCHANT_COUNT_WEIGHTS);
        Map<ResourceKey<Enchantment>, Integer> rolled = rollEnchantments(pool, count, random);
        apply(stack, rolled, level);
    }

    public static boolean isAxe(ItemStack stack) {
        var item = stack.getItem();
        return item == Items.WOODEN_AXE || item == Items.STONE_AXE || item == Items.IRON_AXE
                || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE;
    }

    public static boolean isTrident(ItemStack stack) {
        return stack.getItem() == Items.TRIDENT;
    }

    public static boolean isPickaxe(ItemStack stack) {
        var item = stack.getItem();
        return item == Items.WOODEN_PICKAXE || item == Items.STONE_PICKAXE || item == Items.IRON_PICKAXE
                || item == Items.GOLDEN_PICKAXE || item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE;
    }

    public static boolean isShovel(ItemStack stack) {
        var item = stack.getItem();
        return item == Items.WOODEN_SHOVEL || item == Items.STONE_SHOVEL || item == Items.IRON_SHOVEL
                || item == Items.GOLDEN_SHOVEL || item == Items.DIAMOND_SHOVEL || item == Items.NETHERITE_SHOVEL;
    }

    public static boolean isHoe(ItemStack stack) {
        var item = stack.getItem();
        return item == Items.WOODEN_HOE || item == Items.STONE_HOE || item == Items.IRON_HOE
                || item == Items.GOLDEN_HOE || item == Items.DIAMOND_HOE || item == Items.NETHERITE_HOE;
    }

    public static boolean isShears(ItemStack stack) {
        return stack.getItem() == Items.SHEARS;
    }

    public static boolean isFishingRod(ItemStack stack) {
        return stack.getItem() == Items.FISHING_ROD;
    }

    public static List<EnchantSlot> poolFor(ItemStack stack) {
        if (isTrident(stack)) return EnchantmentPools.TRIDENT;
        if (isAxe(stack)) return EnchantmentPools.AXE;
        if (isPickaxe(stack)) return EnchantmentPools.PICKAXE;
        if (isShovel(stack)) return EnchantmentPools.SHOVEL;
        if (isHoe(stack)) return EnchantmentPools.HOE;
        if (isShears(stack)) return EnchantmentPools.SHEARS;
        if (isFishingRod(stack)) return EnchantmentPools.FISHING_ROD;
        return EnchantmentPools.SWORD;
    }

    public static List<EnchantSlot> weaponPoolFor(ItemStack stack) {
        return poolFor(stack);
    }

    private static int rollEnchantCount(RandomSource random, int[] weights) {
        int total = 0;
        for (int w : weights) total += w;
        int roll = random.nextInt(total);
        int cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll < cumulative) return i + 1;
        }
        return 1;
    }

    private static Map<ResourceKey<Enchantment>, Integer> rollEnchantments(
            List<EnchantSlot> slots, int count, RandomSource random) {

        List<EnchantSlot> pool = new ArrayList<>(slots);
        Map<ResourceKey<Enchantment>, Integer> result = new LinkedHashMap<>();

        int picks = Math.min(count, pool.size());
        for (int i = 0; i < picks; i++) {
            if (pool.isEmpty()) break;

            EnchantSlot chosenSlot = weightedPickSlot(pool, random);
            pool.remove(chosenSlot);

            EnchantSpec chosenSpec = switch (chosenSlot) {
                case EnchantSlot.Solo s -> s.spec();
                case EnchantSlot.Group g -> weightedPickSpec(g.options(), random);
            };

            int level = rollLevel(chosenSpec.maxLevel(), random);
            result.put(chosenSpec.key(), level);
        }

        return result;
    }

    private static EnchantSlot weightedPickSlot(List<EnchantSlot> slots, RandomSource random) {
        int total = slots.stream().mapToInt(EnchantSlot::weight).sum();
        int roll = random.nextInt(Math.max(1, total));
        int cumulative = 0;
        for (EnchantSlot slot : slots) {
            cumulative += slot.weight();
            if (roll < cumulative) return slot;
        }
        return slots.get(slots.size() - 1);
    }

    private static EnchantSpec weightedPickSpec(List<EnchantSpec> specs, RandomSource random) {
        int total = specs.stream().mapToInt(EnchantSpec::pickWeight).sum();
        int roll = random.nextInt(Math.max(1, total));
        int cumulative = 0;
        for (EnchantSpec spec : specs) {
            cumulative += spec.pickWeight();
            if (roll < cumulative) return spec;
        }
        return specs.get(specs.size() - 1);
    }

    private static int rollLevel(int maxLevel, RandomSource random) {
        int[] weights = switch (maxLevel) {
            case 1 -> new int[]{100};
            case 2 -> new int[]{65, 35};
            case 3 -> new int[]{50, 30, 20};
            case 4 -> new int[]{40, 27, 20, 13};
            case 5 -> new int[]{35, 25, 20, 12, 8};
            default -> new int[]{100};
        };

        int total = 0;
        for (int w : weights) total += w;
        int roll = random.nextInt(total);
        int cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll < cumulative) return i + 1;
        }
        return 1;
    }

    private static void apply(ItemStack stack, Map<ResourceKey<Enchantment>, Integer> rolled, ServerLevel level) {
        if (rolled.isEmpty()) return;
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (var entry : rolled.entrySet()) {
            Holder<Enchantment> holder = registry.getOrThrow(entry.getKey());
            stack.enchant(holder, entry.getValue());
        }
    }
}