package dev.wux.survivaldreams.handler;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class BlazeBreezeDeathHandler {

    private static final int MIN_SPAWNS = 1;
    private static final int MAX_SPAWNS = 3;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof Blaze) && !(entity instanceof Breeze)) return;
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;

            Registry<Enchantment> enchantmentRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> knockback = enchantmentRegistry.getOrThrow(Enchantments.KNOCKBACK);

            int spawnCount = MIN_SPAWNS + serverLevel.random.nextInt(MAX_SPAWNS - MIN_SPAWNS + 1);

            for (int i = 0; i < spawnCount; i++) {
                Pufferfish pufferfish = EntityType.PUFFERFISH.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
                if (pufferfish == null) continue;

                pufferfish.setPos(
                        entity.getX() + (serverLevel.random.nextDouble() - 0.5) * 2.0,
                        entity.getY(),
                        entity.getZ() + (serverLevel.random.nextDouble() - 0.5) * 2.0
                );
                serverLevel.addFreshEntity(pufferfish);

                Bogged bogged = EntityType.BOGGED.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
                if (bogged == null) continue;

                bogged.setPos(pufferfish.getX(), pufferfish.getY() + 1.0, pufferfish.getZ());

                ItemStack poisonPotato = new ItemStack(Items.POISONOUS_POTATO);
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                enchantments.set(knockback, 6);
                poisonPotato.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

                bogged.setItemSlot(EquipmentSlot.MAINHAND, poisonPotato);
                bogged.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                FixedWeaponMobs.IDS.add(bogged.getUUID());

                serverLevel.addFreshEntity(bogged);
                bogged.startRiding(pufferfish);
            }
        });
    }
}