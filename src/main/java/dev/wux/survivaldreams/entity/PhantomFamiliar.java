package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PhantomFamiliar extends Phantom {

    public PhantomFamiliar(EntityType<? extends Phantom> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new PhantomFamiliarMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.6D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PhantomFamiliarAttackGoal(this));
        this.goalSelector.addGoal(2, new PhantomFamiliarIdleGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void applyAmbush(LivingEntity target) {
        MobEffectInstance current = target.getEffect(ModEffects.AMBUSH_HOLDER);
        int newAmplifier;
        int remainingDuration;
        if (current != null) {
            newAmplifier = Math.min(current.getAmplifier() + 1, 4);
            remainingDuration = current.getDuration();
        } else {
            newAmplifier = 0;
            remainingDuration = 200;
        }
        target.removeEffect(ModEffects.AMBUSH_HOLDER);
        target.addEffect(new MobEffectInstance(ModEffects.AMBUSH_HOLDER, remainingDuration, newAmplifier, false, true, true));
    }
}