package dev.wux.survivaldreams.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AmbushEffect extends MobEffect {

    private static final float[] DAMAGE_PER_LEVEL = {1f, 2f, 3f, 4f, 5f};

    public AmbushEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        int level = Math.min(amplifier, DAMAGE_PER_LEVEL.length - 1);
        entity.hurtServer(serverLevel, entity.damageSources().magic(), DAMAGE_PER_LEVEL[level]);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}