package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.attachments.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CheartItem extends Item {

    public CheartItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            int blocked = player.getAttachedOrElse(ModAttachments.BLOCKED_SLOTS, 0);

            if (blocked > 0) {
                player.setAttached(ModAttachments.BLOCKED_SLOTS, blocked - 1);

                AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    double newMax = Math.min(20.0, maxHealthAttr.getBaseValue() + 2.0);
                    maxHealthAttr.setBaseValue(newMax);
                }

                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
            }
        }

        return result;
    }
}