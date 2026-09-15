package dev.wux.survivaldreams.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PhantomFamiliarIdleGoal extends Goal {

    private static final float IDLE_MAX_SPEED = 0.6F;

    private static final float MIN_DISTANCE = 4.0F;
    private static final float MAX_DISTANCE = 10.0F;
    private static final float MIN_HEIGHT_OFFSET = -2.0F;
    private static final float MAX_HEIGHT_OFFSET = 3.0F;

    private static final int DISTANCE_CHANGE_INTERVAL = 250;
    private static final int HEIGHT_CHANGE_INTERVAL = 350;
    private static final int REROLL_ANGLE_INTERVAL = 450;

    private final PhantomFamiliar phantomFamiliar;

    private Vec3 anchor;
    private float angle;
    private float distance;
    private float heightOffset;
    private float clockwise;

    public PhantomFamiliarIdleGoal(PhantomFamiliar phantomFamiliar) {
        this.phantomFamiliar = phantomFamiliar;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    private PhantomFamiliarMoveControl moveControl() {
        return (PhantomFamiliarMoveControl) this.phantomFamiliar.getMoveControl();
    }

    @Override
    public boolean canUse() {
        return this.phantomFamiliar.getTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.phantomFamiliar.getTarget() == null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.anchor = this.phantomFamiliar.position();
        this.distance = MIN_DISTANCE + this.phantomFamiliar.getRandom().nextFloat() * (MAX_DISTANCE - MIN_DISTANCE);
        this.heightOffset = MIN_HEIGHT_OFFSET + this.phantomFamiliar.getRandom().nextFloat() * (MAX_HEIGHT_OFFSET - MIN_HEIGHT_OFFSET);
        this.clockwise = this.phantomFamiliar.getRandom().nextBoolean() ? 1.0F : -1.0F;
        this.angle = this.phantomFamiliar.getRandom().nextFloat() * 2.0F * (float) Math.PI;
    }

    @Override
    public void tick() {
        var random = this.phantomFamiliar.getRandom();

        if (random.nextInt(HEIGHT_CHANGE_INTERVAL) == 0) {
            this.heightOffset = MIN_HEIGHT_OFFSET + random.nextFloat() * (MAX_HEIGHT_OFFSET - MIN_HEIGHT_OFFSET);
        }

        if (random.nextInt(DISTANCE_CHANGE_INTERVAL) == 0) {
            this.distance += 1.0F;
            if (this.distance > MAX_DISTANCE) {
                this.distance = MIN_DISTANCE;
                this.clockwise = -this.clockwise;
            }
        }

        if (random.nextInt(REROLL_ANGLE_INTERVAL) == 0) {
            this.angle = random.nextFloat() * 2.0F * (float) Math.PI;
        }

        this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);

        double targetX = this.anchor.x + this.distance * Mth.cos(this.angle);
        double targetY = this.anchor.y + this.heightOffset;
        double targetZ = this.anchor.z + this.distance * Mth.sin(this.angle);

        if (targetY < this.phantomFamiliar.getY()
                && !this.phantomFamiliar.level().isEmptyBlock(this.phantomFamiliar.blockPosition().below(1))) {
            this.heightOffset = Math.max(1.0F, this.heightOffset);
        }

        PhantomFamiliarMoveControl moveControl = this.moveControl();
        moveControl.setMaxSpeed(IDLE_MAX_SPEED);
        moveControl.setWantedPosition(targetX, targetY, targetZ, 1.0D);
    }
}