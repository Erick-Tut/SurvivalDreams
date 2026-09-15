package dev.wux.survivaldreams.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class PhantomFamiliarAttackGoal extends Goal {

    private static final int ATTACK_COOLDOWN_TICKS = 40;
    private static final int RETREAT_TICKS = 20;
    private static final float DIVE_MAX_SPEED = 2.0F;
    private static final float RETREAT_MAX_SPEED = 1.0F;
    private static final float SEEK_MAX_SPEED = 0.9F;
    private static final double RETREAT_HEIGHT = 3.0D;
    private static final double ATTACK_REACH_SQR = 4.0D;

    private static final double BRAKE_START_DISTANCE = 6.0D;
    private static final float BRAKE_MIN_SPEED_FRACTION = 0.35F;

    private static final double SEEK_MAX_HEIGHT_ABOVE_TARGET = 8.0D;
    private static final double SEEK_CLIMB_STEP = 1.5D;

    private final PhantomFamiliar phantomFamiliar;
    private int cooldown;
    private int retreatTicksLeft;

    public PhantomFamiliarAttackGoal(PhantomFamiliar phantomFamiliar) {
        this.phantomFamiliar = phantomFamiliar;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private PhantomFamiliarMoveControl moveControl() {
        return (PhantomFamiliarMoveControl) this.phantomFamiliar.getMoveControl();
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.phantomFamiliar.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.phantomFamiliar.getTarget();
        if (target == null) return;

        this.phantomFamiliar.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.cooldown > 0) {
            this.cooldown--;
        }

        if (this.retreatTicksLeft > 0) {
            this.retreatTicksLeft--;
            PhantomFamiliarMoveControl moveControl = this.moveControl();
            moveControl.setMaxSpeed(RETREAT_MAX_SPEED);
            moveControl.setWantedPosition(
                    this.phantomFamiliar.getX(),
                    target.getY() + 1.0D + RETREAT_HEIGHT,
                    this.phantomFamiliar.getZ(),
                    1.0D);
            return;
        }

        if (!this.phantomFamiliar.getSensing().hasLineOfSight(target)) {
            double climbTargetY = Math.min(
                    this.phantomFamiliar.getY() + SEEK_CLIMB_STEP,
                    target.getY() + SEEK_MAX_HEIGHT_ABOVE_TARGET);

            PhantomFamiliarMoveControl moveControl = this.moveControl();
            moveControl.setMaxSpeed(SEEK_MAX_SPEED);
            moveControl.setWantedPosition(
                    this.phantomFamiliar.getX(),
                    climbTargetY,
                    this.phantomFamiliar.getZ(),
                    1.0D);
            return;
        }

        double distSqr = this.phantomFamiliar.distanceToSqr(target);

        if (distSqr <= ATTACK_REACH_SQR) {
            if (this.cooldown <= 0 && this.phantomFamiliar.level() instanceof ServerLevel serverLevel) {
                this.phantomFamiliar.doHurtTarget(serverLevel, target);
                this.phantomFamiliar.applyAmbush(target);
                this.cooldown = ATTACK_COOLDOWN_TICKS;
                this.retreatTicksLeft = RETREAT_TICKS;
            }
        } else {
            double dist = Math.sqrt(distSqr);
            float speedFraction = (float) Mth.clamp(dist / BRAKE_START_DISTANCE, BRAKE_MIN_SPEED_FRACTION, 1.0D);
            float cappedMaxSpeed = DIVE_MAX_SPEED * speedFraction;

            PhantomFamiliarMoveControl moveControl = this.moveControl();
            moveControl.setMaxSpeed(cappedMaxSpeed);
            if (dist > BRAKE_START_DISTANCE) {
                moveControl.boostSpeed(cappedMaxSpeed * 0.55F);
            }
            moveControl.setWantedPosition(target.getX(), target.getY() + 1.0D, target.getZ(), 1.0D);
        }
    }
}