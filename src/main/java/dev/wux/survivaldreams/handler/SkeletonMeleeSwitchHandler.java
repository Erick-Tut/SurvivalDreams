package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.handler.enchant.EnchantmentPools;
import dev.wux.survivaldreams.handler.enchant.MobEnchantHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkeletonMeleeSwitchHandler {

    private static final double SWITCH_DISTANCE = 7.0;
    private static final double MAX_CHASE_DISTANCE = 10.0;
    private static final double SEARCH_RADIUS = 16.0;

    private static final float DROP_CHANCE = 0.45F;

    private static final Map<UUID, ItemStack> ORIGINAL_BOWS = new HashMap<>();
    private static final Map<UUID, Boolean> IS_IN_MELEE_MODE = new HashMap<>();
    private static final Map<UUID, ItemStack> ASSIGNED_WEAPON = new HashMap<>();
    private static final Map<UUID, ItemStack> ASSIGNED_OFFHAND = new HashMap<>();

    private record WeightedWeaponSet(int weight, Item sword, Item axe) {}

    private static final List<WeightedWeaponSet> WEAPON_MATERIALS = List.of(
            new WeightedWeaponSet(40, Items.WOODEN_SWORD, Items.WOODEN_AXE),
            new WeightedWeaponSet(30, Items.STONE_SWORD, Items.STONE_AXE),
            new WeightedWeaponSet(25, Items.IRON_SWORD, Items.IRON_AXE),
            new WeightedWeaponSet(15, Items.GOLDEN_SWORD, Items.GOLDEN_AXE),
            new WeightedWeaponSet(8, Items.DIAMOND_SWORD, Items.DIAMOND_AXE),
            new WeightedWeaponSet(3, Items.NETHERITE_SWORD, Items.NETHERITE_AXE)
    );

    private record WeightedTool(int weight, Item pickaxe, Item shovel, Item hoe) {}

    private static final List<WeightedTool> TOOL_MATERIALS = List.of(
            new WeightedTool(40, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE),
            new WeightedTool(30, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_HOE),
            new WeightedTool(25, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_HOE),
            new WeightedTool(15, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE),
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
            if (!(entity instanceof AbstractSkeleton skeleton)) return;

            RandomSource random = serverLevel.random;

            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack helmet = new ItemStack(mat.head());
                MobEnchantHelper.maybeEnchant(helmet, EnchantmentPools.HELMET, serverLevel, random);
                randomizeDurability(helmet, random);
                skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
                skeleton.setDropChance(EquipmentSlot.HEAD, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack chest = new ItemStack(mat.chest());
                MobEnchantHelper.maybeEnchant(chest, EnchantmentPools.CHESTPLATE, serverLevel, random);
                randomizeDurability(chest, random);
                skeleton.setItemSlot(EquipmentSlot.CHEST, chest);
                skeleton.setDropChance(EquipmentSlot.CHEST, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack legs = new ItemStack(mat.legs());
                MobEnchantHelper.maybeEnchant(legs, EnchantmentPools.LEGGINGS, serverLevel, random);
                randomizeDurability(legs, random);
                skeleton.setItemSlot(EquipmentSlot.LEGS, legs);
                skeleton.setDropChance(EquipmentSlot.LEGS, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack feet = new ItemStack(mat.feet());
                MobEnchantHelper.maybeEnchant(feet, EnchantmentPools.BOOTS, serverLevel, random);
                randomizeDurability(feet, random);
                skeleton.setItemSlot(EquipmentSlot.FEET, feet);
                skeleton.setDropChance(EquipmentSlot.FEET, DROP_CHANCE);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            java.util.Set<UUID> processedThisTick = new java.util.HashSet<>();
            java.util.Set<Mob> nearbyMobs = new java.util.HashSet<>();

            for (Player player : level.players()) {
                if (player.isCreative() || player.isSpectator()) continue;

                AABB searchBox = player.getBoundingBox().inflate(SEARCH_RADIUS);
                nearbyMobs.addAll(level.getEntitiesOfClass(Mob.class, searchBox));
            }

            for (Mob mob : nearbyMobs) {
                if (!isTargetMob(mob)) continue;
                if (FixedWeaponMobs.IDS.contains(mob.getUUID())) continue;

                Player nearestValidPlayer = null;
                double nearestDist = Double.MAX_VALUE;
                for (Player player : level.players()) {
                    if (player.isCreative() || player.isSpectator()) continue;
                    double d = mob.distanceTo(player);
                    if (d < nearestDist) {
                        nearestDist = d;
                        nearestValidPlayer = player;
                    }
                }

                if (nearestValidPlayer == null) continue;

                processedThisTick.add(mob.getUUID());
                boolean currentlyMelee = IS_IN_MELEE_MODE.getOrDefault(mob.getUUID(), false);
                boolean shouldBeMelee = nearestDist <= SWITCH_DISTANCE;

                if (shouldBeMelee && !currentlyMelee) {
                    switchToMelee(mob, level);
                    IS_IN_MELEE_MODE.put(mob.getUUID(), true);
                } else if (!shouldBeMelee && currentlyMelee) {
                    switchToBow(mob);
                    IS_IN_MELEE_MODE.put(mob.getUUID(), false);
                }

                if (shouldBeMelee && mob.getTarget() == null) {
                    mob.setTarget(nearestValidPlayer);
                }

                var target = mob.getTarget();
                if (target != null && mob.distanceTo(target) > MAX_CHASE_DISTANCE) {
                    mob.setTarget(null);
                }
            }
        });
    }

    private static boolean isTargetMob(Mob mob) {
        return mob instanceof AbstractSkeleton || mob instanceof Drowned;
    }

    private static void switchToMelee(Mob mob, ServerLevel level) {
        ItemStack currentMainHand = mob.getItemBySlot(EquipmentSlot.MAINHAND);
        ORIGINAL_BOWS.putIfAbsent(mob.getUUID(), currentMainHand.copy());

        RandomSource random = level.random;

        ItemStack weapon;
        ItemStack offhand;

        if (ASSIGNED_WEAPON.containsKey(mob.getUUID())) {
            weapon = ASSIGNED_WEAPON.get(mob.getUUID()).copy();
            offhand = ASSIGNED_OFFHAND.getOrDefault(mob.getUUID(), ItemStack.EMPTY).copy();
        } else {
            weapon = rollRandomWeapon(random);
            MobEnchantHelper.maybeEnchant(weapon, MobEnchantHelper.poolFor(weapon), level, random);
            randomizeDurability(weapon, random);

            if (random.nextFloat() < 0.3F) {
                offhand = new ItemStack(Items.SHIELD);
                randomizeDurability(offhand, random);
            } else {
                offhand = ItemStack.EMPTY;
            }

            ASSIGNED_WEAPON.put(mob.getUUID(), weapon.copy());
            ASSIGNED_OFFHAND.put(mob.getUUID(), offhand.copy());
        }

        mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        mob.setItemSlot(EquipmentSlot.OFFHAND, offhand);

        mob.setDropChance(EquipmentSlot.MAINHAND, DROP_CHANCE);
        mob.setDropChance(EquipmentSlot.OFFHAND, DROP_CHANCE);
    }

    private static void switchToBow(Mob mob) {
        ItemStack originalBow = ORIGINAL_BOWS.getOrDefault(mob.getUUID(), new ItemStack(Items.BOW));
        mob.setItemSlot(EquipmentSlot.MAINHAND, originalBow);
        mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        ORIGINAL_BOWS.remove(mob.getUUID());
    }

    private static ItemStack rollRandomWeapon(RandomSource random) {
        if (random.nextFloat() < CHANCE_TO_HOLD_TOOL_INSTEAD) {
            return rollRandomTool(random);
        }

        int totalWeight = WEAPON_MATERIALS.stream().mapToInt(WeightedWeaponSet::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        WeightedWeaponSet chosen = WEAPON_MATERIALS.get(0);
        for (WeightedWeaponSet w : WEAPON_MATERIALS) {
            cumulative += w.weight();
            if (roll < cumulative) {
                chosen = w;
                break;
            }
        }

        int typeRoll = random.nextInt(100);
        if (typeRoll < 10) {
            return new ItemStack(Items.TRIDENT);
        } else if (typeRoll < 30) {
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

        int totalWeight = TOOL_MATERIALS.stream().mapToInt(WeightedTool::weight).sum();
        int roll = random.nextInt(totalWeight);
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