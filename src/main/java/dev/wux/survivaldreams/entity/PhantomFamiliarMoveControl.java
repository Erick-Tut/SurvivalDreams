package dev.wux.survivaldreams.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class PhantomFamiliarMoveControl extends MoveControl {

    private static final float DEFAULT_MAX_SPEED = 2.2F;
    private static final float MIN_SPEED = 0.2F;
    private static final float TURN_ACCEL_STEP = 0.02F;
    private static final float BRAKE_STEP = 0.025F;

    private float speed = 0.1F;
    private float maxSpeed = DEFAULT_MAX_SPEED;

    public PhantomFamiliarMoveControl(Mob mob) {
        super(mob);
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        if (this.speed > this.maxSpeed) {
            this.speed = this.maxSpeed;
        }
    }

    public void boostSpeed(float speed) {
        this.speed = Math.max(this.speed, Math.min(speed, this.maxSpeed));
    }

    @Override
    public void tick() {
        if (this.mob.horizontalCollision) {
            this.mob.setYRot(this.mob.getYRot() + 180.0F);
            this.speed = 0.1F;
        }

        double d = this.wantedX - this.mob.getX();
        double e = this.wantedY - this.mob.getY();
        double f = this.wantedZ - this.mob.getZ();
        double g = Math.sqrt(d * d + f * f);

        if (Math.abs(g) > (double) 1.0E-5F) {
            double h = (double) 1.0F - Math.abs(e * (double) 0.7F) / g;
            d *= h;
            f *= h;
            g = Math.sqrt(d * d + f * f);
            double i = Math.sqrt(d * d + f * f + e * e);

            float j = this.mob.getYRot();
            float k = (float) Mth.atan2(f, d);
            float l = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
            float m = Mth.wrapDegrees(k * (180F / (float) Math.PI));
            this.mob.setYRot(Mth.approachDegrees(l, m, 4.0F) - 90.0F);
            this.mob.yBodyRot = this.mob.getYRot();

            if (Mth.degreesDifferenceAbs(j, this.mob.getYRot()) < 3.0F) {
                this.speed = Mth.approach(this.speed, this.maxSpeed, TURN_ACCEL_STEP * (this.maxSpeed / this.speed));
            } else {
                this.speed = Mth.approach(this.speed, MIN_SPEED, BRAKE_STEP);
            }

            float n = (float) (-(Mth.atan2(-e, g) * (double) (180F / (float) Math.PI)));
            this.mob.setXRot(n);

            float o = this.mob.getYRot() + 90.0F;
            double p = (double) (this.speed * Mth.cos((double) (o * ((float) Math.PI / 180F)))) * Math.abs(d / i);
            double q = (double) (this.speed * Mth.sin((double) (o * ((float) Math.PI / 180F)))) * Math.abs(f / i);
            double r = (double) (this.speed * Mth.sin((double) (n * ((float) Math.PI / 180F)))) * Math.abs(e / i);

            Vec3 vec3 = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(vec3.add(new Vec3(p, r, q).subtract(vec3).scale(0.2)));
        }
    }
}