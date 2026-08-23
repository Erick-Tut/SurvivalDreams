package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.ModEffects;
import dev.wux.survivaldreams.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Watchling extends Monster {

    private static final float BASIC_ATTACK_DAMAGE = 6.0F;
    private static final float HEAVY_ATTACK_DAMAGE = 12.0F;
    private static final float HEAVY_ATTACK_CHANCE = 0.3F;

    private static final int TELEPORT_ATTEMPTS = 16;
    private static final double TELEPORT_RANGE = 12.0;

    private static final int VOIDED_DURATION_TICKS = 100;

    private static final int FALL_SAFETY_MAX_FALL_TICKS = 20;
    private static final int FALL_SAFETY_CHECK_RADIUS = 3;

    private int teleportCooldown = 0;
    private int fallTicksWithoutGround = 0;
    private BlockPos lastSafeGroundPos = null;

    public Watchling(EntityType<? extends Watchling> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, BASIC_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Endersent.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == ModEffects.VOIDED) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WATCHLING_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.WATCHLING_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WATCHLING_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(ModSounds.WATCHLING_STEP, 0.15F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (teleportCooldown > 0) teleportCooldown--;

        if (this.level() instanceof ServerLevel serverLevel) {
            handleFallSafety(serverLevel);
            VoidParticles.spawnAmbient(serverLevel, this, 0.1F);

            if (this.getTarget() != null && teleportCooldown <= 0) {
                double distSqr = this.distanceToSqr(this.getTarget());
                if (distSqr > 9.0) {
                    teleportTowards(serverLevel, this.getTarget().position());
                    teleportCooldown = 60;
                }
            }
        }
    }

    private void handleFallSafety(ServerLevel serverLevel) {
        if (this.onGround()) {
            fallTicksWithoutGround = 0;
            lastSafeGroundPos = this.blockPosition();
            return;
        }

        if (hasNearbyGround(serverLevel, FALL_SAFETY_CHECK_RADIUS)) {
            fallTicksWithoutGround = 0;
            return;
        }

        fallTicksWithoutGround++;

        if (fallTicksWithoutGround >= FALL_SAFETY_MAX_FALL_TICKS) {
            if (lastSafeGroundPos != null) {
                this.setDeltaMovement(Vec3.ZERO);
                this.teleportTo(
                        lastSafeGroundPos.getX() + 0.5D,
                        lastSafeGroundPos.getY(),
                        lastSafeGroundPos.getZ() + 0.5D
                );
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        this.getX(), this.getY() + 1.0, this.getZ(), 24, 0.5, 1.0, 0.5, 0.3);
            }
            fallTicksWithoutGround = 0;
        }
    }

    private boolean hasNearbyGround(ServerLevel serverLevel, int radius) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        int baseX = Mth.floor(this.getX());
        int baseY = Mth.floor(this.getY());
        int baseZ = Mth.floor(this.getZ());

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -radius; dy <= 1; dy++) {
                    checkPos.set(baseX + dx, baseY + dy, baseZ + dz);
                    if (serverLevel.getBlockState(checkPos).isFaceSturdy(serverLevel, checkPos, Direction.UP)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        boolean heavy = serverLevel.random.nextFloat() < HEAVY_ATTACK_CHANCE;
        float damage = heavy ? HEAVY_ATTACK_DAMAGE : BASIC_ATTACK_DAMAGE;

        boolean hit = target.hurtServer(serverLevel, this.damageSources().mobAttack(this), damage);

        if (hit) {
            SoundEvent sound = heavy ? ModSounds.WATCHLING_HEAVY_ATTACK : ModSounds.WATCHLING_ATTACK;
            serverLevel.playSound(null, this.blockPosition(), sound, SoundSource.HOSTILE, 1.0F, 1.0F);

            if (target instanceof LivingEntity livingTarget) {
                livingTarget.addEffect(new MobEffectInstance(
                        ModEffects.VOIDED_HOLDER,
                        VOIDED_DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                ));
            }
        }

        return hit;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        boolean isProjectile = damageSource.getDirectEntity() instanceof Projectile;

        boolean hurt = super.hurtServer(serverLevel, damageSource, amount);

        if (hurt && teleportCooldown <= 0) {
            Entity attacker = damageSource.getEntity();
            Vec3 fleeTarget = attacker != null
                    ? this.position().add(this.position().subtract(attacker.position()).normalize().scale(8.0))
                    : this.position();

            teleportTowards(serverLevel, fleeTarget);
            teleportCooldown = isProjectile ? 0 : 20;
        }

        return hurt;
    }

    private void teleportTowards(ServerLevel serverLevel, Vec3 nearPos) {
        RandomSource random = serverLevel.random;

        for (int i = 0; i < TELEPORT_ATTEMPTS; i++) {
            double x = nearPos.x + (random.nextDouble() - 0.5) * TELEPORT_RANGE;
            double y = nearPos.y + random.nextInt(3) - 1;
            double z = nearPos.z + (random.nextDouble() - 0.5) * TELEPORT_RANGE;

            if (tryTeleportTo(serverLevel, x, y, z)) {
                return;
            }
        }

        for (int i = 0; i < TELEPORT_ATTEMPTS; i++) {
            double x = this.getX() + (random.nextDouble() - 0.5) * TELEPORT_RANGE;
            double y = this.getY() + random.nextInt(5) - 2;
            double z = this.getZ() + (random.nextDouble() - 0.5) * TELEPORT_RANGE;

            if (tryTeleportTo(serverLevel, x, y, z)) {
                return;
            }
        }

    }

    private boolean tryTeleportTo(ServerLevel serverLevel, double x, double y, double z) {
        BlockPos.MutableBlockPos mutablePos =
                new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));

        int minY = serverLevel.getMinY();

        while (mutablePos.getY() > minY
                && !serverLevel.getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }

        if (mutablePos.getY() <= minY
                && !serverLevel.getBlockState(mutablePos).blocksMotion()) {
            return false;
        }

        if (!serverLevel.hasChunkAt(mutablePos)) return false;

        BlockState blockState = serverLevel.getBlockState(mutablePos);
        boolean solidGround = blockState.blocksMotion();
        boolean isWater = blockState.getFluidState().is(FluidTags.WATER);
        if (!solidGround || isWater) return false;

        double targetX = x;
        double targetY = mutablePos.getY() + 1.0;
        double targetZ = z;

        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();

        boolean success = this.randomTeleport(targetX, targetY, targetZ, true);

        if (success) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    oldX, oldY + 1.0, oldZ, 32, 0.5, 1.0, 0.5, 0.3);
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(), 32, 0.5, 1.0, 0.5, 0.3);
        }

        return success;
    }
}