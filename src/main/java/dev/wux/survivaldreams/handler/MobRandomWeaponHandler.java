package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.handler.enchant.EnchantmentPools;
import dev.wux.survivaldreams.handler.enchant.MobEnchantHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
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

    private record WeightedTool(int weight, Item pickaxe, Item shovel, Item hoe) {}

    private static final List<WeightedTool> TOOL_MATERIALS = List.of(
            new WeightedTool(40, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE),
            new WeightedTool(30, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_HOE),
            new WeightedTool(25, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_HOE),
            new WeightedTool(8, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE),
            new WeightedTool(3, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE)
    );

    private static final float CHANCE_TO_HOLD_TOOL_INSTEAD = 0.10F;

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
                    MobEnchantHelper.maybeEnchant(weapon, MobEnchantHelper.poolFor(weapon), serverLevel, random);
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
                MobEnchantHelper.maybeEnchant(helmet, EnchantmentPools.HELMET, serverLevel, random);
                randomizeDurability(helmet, random);
                living.setItemSlot(EquipmentSlot.HEAD, helmet);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack chest = new ItemStack(mat.chest());
                MobEnchantHelper.maybeEnchant(chest, EnchantmentPools.CHESTPLATE, serverLevel, random);
                randomizeDurability(chest, random);
                living.setItemSlot(EquipmentSlot.CHEST, chest);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack legs = new ItemStack(mat.legs());
                MobEnchantHelper.maybeEnchant(legs, EnchantmentPools.LEGGINGS, serverLevel, random);
                randomizeDurability(legs, random);
                living.setItemSlot(EquipmentSlot.LEGS, legs);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack feet = new ItemStack(mat.feet());
                MobEnchantHelper.maybeEnchant(feet, EnchantmentPools.BOOTS, serverLevel, random);
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
        if (random.nextFloat() < CHANCE_TO_HOLD_TOOL_INSTEAD) {
            return rollRandomTool(random);
        }

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

    private static ItemStack rollRandomTool(RandomSource random) {
        int typeRoll = random.nextInt(100);

        if (typeRoll < 20) {
            return new ItemStack(Items.SHEARS);
        } else if (typeRoll < 40) {
            return new ItemStack(Items.FISHING_ROD);
        }

        int roll = random.nextInt(100);
        int cumulative = 0;
        WeightedTool chosen = TOOL_MATERIALS.get(0);
        for (WeightedTool tool : TOOL_MATERIALS) {
            cumulative += tool.weight();
            if (roll < cumulative) {
                chosen = tool;
                break;
            }
        }

        int toolTypeRoll = random.nextInt(100);
        if (toolTypeRoll < 34) {
            return new ItemStack(chosen.pickaxe());
        } else if (toolTypeRoll < 67) {
            return new ItemStack(chosen.shovel());
        } else {
            return new ItemStack(chosen.hoe());
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

    private static void randomizeDurability(ItemStack stack, RandomSource random) {
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return;

        int remainingPercent = 1 + random.nextInt(100);
        int damage = maxDamage - Math.round(maxDamage * (remainingPercent / 100.0F));
        damage = Math.max(0, Math.min(damage, maxDamage - 1));
        stack.setDamageValue(damage);
    }
}