package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;


public class WitherSkeletonLootingDropHandler {

    private static final float VANILLA_HEAD_DROP_CHANCE = 1F / 40F;

    private static final float LOOTING_BOOK_DROP_CHANCE = VANILLA_HEAD_DROP_CHANCE / 2F;

    private static final int LOOTING_BOOK_LEVEL = 4;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof WitherSkeleton)) return;
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;

            RandomSource random = serverLevel.random;
            if (random.nextFloat() >= LOOTING_BOOK_DROP_CHANCE) return;

            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            var registry = serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> looting = registry.getOrThrow(Enchantments.LOOTING);
            book.enchant(looting, LOOTING_BOOK_LEVEL);

            ItemEntity itemEntity = new ItemEntity(
                    serverLevel,
                    entity.getX(), entity.getY(), entity.getZ(),
                    book
            );
            serverLevel.addFreshEntity(itemEntity);
        });
    }
}