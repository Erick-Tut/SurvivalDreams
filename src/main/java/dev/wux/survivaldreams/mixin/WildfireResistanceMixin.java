package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.component.ModDataComponents;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class WildfireResistanceMixin {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static final float GENERAL_REDUCTION_PER_PIECE = 0.05f;
    private static final float FIRE_REDUCTION_PER_PIECE = 0.10f;

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float survivalDreams$applyWildfireResistance(float amount, ServerLevel serverLevel, DamageSource damageSource) {
        LivingEntity self = (LivingEntity) (Object) this;

        int reinforcedCount = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorStack = self.getItemBySlot(slot);
            if (armorStack.has(ModDataComponents.WILDFIRE_REINFORCED)) {
                reinforcedCount++;
            }
        }

        if (reinforcedCount == 0) {
            return amount;
        }

        float multiplier = 1f - (reinforcedCount * GENERAL_REDUCTION_PER_PIECE);
        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            multiplier -= reinforcedCount * FIRE_REDUCTION_PER_PIECE;
        }

        return Math.max(0f, amount * multiplier);
    }
}