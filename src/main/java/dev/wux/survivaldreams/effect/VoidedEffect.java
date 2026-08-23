package dev.wux.survivaldreams.effect;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class VoidedEffect extends MobEffect {

    public static final float DAMAGE_MULTIPLIER = 1.5F;
    private static final double SLOW_AMOUNT = -0.2D;

    private static final float TICK_DAMAGE = 1.0F;
    private static final int TICK_INTERVAL = 25;

    private static final Identifier SLOW_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "effect.voided_slow");

    public VoidedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        AttributeInstance instance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.getModifier(SLOW_MODIFIER_ID) == null) {
            instance.addTransientModifier(new AttributeModifier(
                    SLOW_MODIFIER_ID,
                    SLOW_AMOUNT,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            instance.removeModifier(SLOW_MODIFIER_ID);
        }
        super.onEffectRemoved(effectInstance, entity);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % TICK_INTERVAL == 0;
    }

    @Override
    public boolean applyEffectTick(net.minecraft.server.level.ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        entity.hurtServer(serverLevel, entity.damageSources().magic(), TICK_DAMAGE);
        return true;
    }
}