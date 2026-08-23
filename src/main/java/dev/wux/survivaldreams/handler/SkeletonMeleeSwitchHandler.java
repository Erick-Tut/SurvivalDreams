package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
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

    private static final List<ResourceKey<Enchantment>> WEAPON_ENCHANTS = List.of(
            Enchantments.FIRE_ASPECT,
            Enchantments.UNBREAKING,
            Enchantments.SHARPNESS,
            Enchantments.BANE_OF_ARTHROPODS
    );

    private static final float CHANCE_TO_BE_ENCHANTED = 0.12F;
    private static final int[] ENCHANT_COUNT_WEIGHTS = {70, 25, 5};

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

    private static final List<ResourceKey<Enchantment>> ARMOR_ENCHANTS = List.of(
            Enchantments.UNBREAKING,
            Enchantments.PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.BLAST_PROTECTION
    );

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!(entity instanceof AbstractSkeleton skeleton)) return;

            RandomSource random = serverLevel.random;

            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack helmet = new ItemStack(mat.head());
                maybeEnchant(helmet, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(helmet, random);
                skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
                skeleton.setDropChance(EquipmentSlot.HEAD, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack chest = new ItemStack(mat.chest());
                maybeEnchant(chest, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(chest, random);
                skeleton.setItemSlot(EquipmentSlot.CHEST, chest);
                skeleton.setDropChance(EquipmentSlot.CHEST, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack legs = new ItemStack(mat.legs());
                maybeEnchant(legs, ARMOR_ENCHANTS, serverLevel, random);
                randomizeDurability(legs, random);
                skeleton.setItemSlot(EquipmentSlot.LEGS, legs);
                skeleton.setDropChance(EquipmentSlot.LEGS, DROP_CHANCE);
            }
            if (random.nextFloat() < CHANCE_PER_ARMOR_SLOT) {
                ArmorMaterial mat = rollArmorMaterial(random);
                ItemStack feet = new ItemStack(mat.feet());
                maybeEnchant(feet, ARMOR_ENCHANTS, serverLevel, random);
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
            maybeEnchant(weapon, WEAPON_ENCHANTS, level, random);
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

    private static void maybeEnchant(ItemStack stack, List<ResourceKey<Enchantment>> pool, ServerLevel level, RandomSource random) {
        if (random.nextFloat() >= CHANCE_TO_BE_ENCHANTED) return;

        int count = rollEnchantCount(random);
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        List<ResourceKey<Enchantment>> shuffled = new ArrayList<>(pool);
        java.util.Collections.shuffle(shuffled, new java.util.Random(random.nextLong()));

        int applied = 0;
        for (ResourceKey<Enchantment> key : shuffled) {
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