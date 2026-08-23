package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;

public class MobRandomWeaponHandler {

    private static final float CHANCE_TO_HAVE_WEAPON = 0.70F;

    private static final float DROP_CHANCE = 0.45F;

    private static final List<WeightedMaterial> MATERIALS = List.of(
            new WeightedMaterial(40, Items.WOODEN_SWORD, Items.WOODEN_AXE),
            new WeightedMaterial(30, Items.STONE_SWORD, Items.STONE_AXE),
            new WeightedMaterial(25, Items.IRON_SWORD, Items.IRON_AXE),
            new WeightedMaterial(8, Items.DIAMOND_SWORD, Items.DIAMOND_AXE),
            new WeightedMaterial(3, Items.NETHERITE_SWORD, Items.NETHERITE_AXE)


    );

    private record WeightedMaterial(int weight, Item sword, Item axe) {}

    private record ArmorMaterial(int weight, Item head, Item chest, Item legs, Item feet) {}

    private static final List<ArmorMaterial> ARMOR_MATERIALS = List.of(
            new ArmorMaterial(35, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS),
            new ArmorMaterial(25, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS),
            new ArmorMaterial(20, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS),
            new ArmorMaterial(15, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS),
            new ArmorMaterial(8, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS),
            new ArmorMaterial(3, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
    );

    private static final float CHANCE_PER_ARMOR_SLOT = 0.35F;

    private static final List<net.minecraft.resources.ResourceKey<Enchantment>> ARMOR_ENCHANTS = List.of(
            Enchantments.UNBREAKING,
            Enchantments.PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.BLAST_PROTECTION
    );

    private static final List<net.minecraft.resources.ResourceKey<Enchantment>> WEAPON_ENCHANTS = List.of(
            Enchantments.FIRE_ASPECT,
            Enchantments.UNBREAKING,
            Enchantments.SHARPNESS,
            Enchantments.BANE_OF_ARTHROPODS
    );

    private static final float CHANCE_TO_BE_ENCHANTED = 0.12F;
    private static final int[] ENCHANT_COUNT_WEIGHTS = {70, 25, 5};

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!(entity instanceof Mob living)) return;

            boolean isTargetMob = living instanceof Zombie
                    || living instanceof ZombifiedPiglin
                    || living instanceof WitherSkeleton
                    || living instanceof Piglin
                    || living instanceof PiglinBrute
                    || isVindicator(living);

            if (!isTargetMob) return;

            RandomSource random = serverLevel.random;

            if (random.nextFloat() < CHANCE_TO_HAVE_WEAPON) {
                if (living.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                    ItemStack weapon = rollRandomWeapon(random);
                    maybeEnchant(weapon, WEAPON_ENCHANTS, serverLevel, random);
                    randomizeDurability(weapon, random);
                    living.setItemSlot(EquipmentSlot.MAINHAND, weapon);
                }
            }

            if (random.nextFloat() < 0.5F) {
                if (living.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                    ItemStack shield = new ItemStack(Items.SHIELD);
                    randomizeDurability(shield, random);
                    living.setItemSlot(EquipmentSlot.OFFHAND, shield);
                }
            }

            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack helmet = new ItemStack(mat.head());
                maybeEnchant(helmet, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(helmet, random);
                living.setItemSlot(EquipmentSlot.HEAD, helmet);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack chest = new ItemStack(mat.chest());
                maybeEnchant(chest, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(chest, random);
                living.setItemSlot(EquipmentSlot.CHEST, chest);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack legs = new ItemStack(mat.legs());
                maybeEnchant(legs, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(legs, random);
                living.setItemSlot(EquipmentSlot.LEGS, legs);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack feet = new ItemStack(mat.feet());
                maybeEnchant(feet, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(feet, random);
                living.setItemSlot(EquipmentSlot.FEET, feet);
            }

            living.setDropChance(EquipmentSlot.MAINHAND, DROP_CHANCE);
            living.setDropChance(EquipmentSlot.OFFHAND, DROP_CHANCE);
            living.setDropChance(EquipmentSlot.HEAD, DROP_CHANCE);
            living.setDropChance(EquipmentSlot.CHEST, DROP_CHANCE);
            living.setDropChance(EquipmentSlot.LEGS, DROP_CHANCE);
            living.setDropChance(EquipmentSlot.FEET, DROP_CHANCE);
        });
    }

    private static boolean isVindicator(Mob living) {
        return living.getClass().getSimpleName().equals("Vindicator");
    }

    private static ItemStack rollRandomWeapon(RandomSource random) {
        int roll = random.nextInt(100);
        int cumulative = 0;
        WeightedMaterial chosen = MATERIALS.get(0);
        for (WeightedMaterial material : MATERIALS) {
            cumulative += material.weight();
            if (roll < cumulative) {
                chosen = material;
                break;
            }
        }

        int typeRoll = random.nextInt(100);
        if (typeRoll < 10) {
            return new ItemStack(Items.TRIDENT);
        } else if (typeRoll < 25) {
            return new ItemStack(chosen.axe());
        } else {
            return new ItemStack(chosen.sword());
        }
    }

    private static ArmorMaterial rollArmorMaterial(RandomSource random) {
        int totalWeight = ARMOR_MATERIALS.stream().mapToInt(ArmorMaterial::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (ArmorMaterial mat : ARMOR_MATERIALS) {
            cumulative += mat.weight();
            if (roll < cumulative) return mat;
        }
        return ARMOR_MATERIALS.get(0);
    }

    private static void maybeEnchant(ItemStack stack, List<net.minecraft.resources.ResourceKey<Enchantment>> pool, ServerLevel level, RandomSource random) {
        if (random.nextFloat() >= CHANCE_TO_BE_ENCHANTED) return;

        int count = rollEnchantCount(random);
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        List<net.minecraft.resources.ResourceKey<Enchantment>> shuffled = new ArrayList<>(pool);
        java.util.Collections.shuffle(shuffled, new java.util.Random(random.nextLong()));

        int applied = 0;
        for (net.minecraft.resources.ResourceKey<Enchantment> key : shuffled) {
            if (applied >= count) break;
            Holder<Enchantment> holder = registry.getOrThrow(key);
            stack.enchant(holder, 1);
            applied++;
        }
    }

    private static int rollEnchantCount(RandomSource random) {
        int total = ENCHANT_COUNT_WEIGHTS[0] + ENCHANT_COUNT_WEIGHTS[1] + ENCHANT_COUNT_WEIGHTS[2];
        int roll = random.nextInt(total);
        int cumulative = 0;
        for (int i = 0; i < ENCHANT_COUNT_WEIGHTS.length; i++) {
            cumulative += ENCHANT_COUNT_WEIGHTS[i];
            if (roll < cumulative) return i + 1;
        }
        return 1;
    }

    private static void randomizeDurability(ItemStack stack, RandomSource random) {
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return;

        int remainingPercent = 1 + random.nextInt(100);
        int damage = maxDamage - Math.round(maxDamage * (remainingPercent / 100.0F));
        damage = Math.max(0, Math.min(damage, maxDamage - 1));
        stack.setDamageValue(damage);
    }
}