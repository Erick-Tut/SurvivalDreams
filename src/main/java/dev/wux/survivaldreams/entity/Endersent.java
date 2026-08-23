package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.ModEffects;
import dev.wux.survivaldreams.ModSounds;
import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.block.EndersentSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Endersent extends Monster {

    private static final int VOIDED_DURATION_TICKS = 100;

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_BASIC = 1;
    public static final int ATTACK_BASIC_VARIANT = 2;
    public static final int ATTACK_DEADLY_ESCAPE = 3;
    public static final int ATTACK_TELEPORT_SMASH = 4;
    public static final int ATTACK_VOID_ERUPTION = 5;
    public static final int ATTACK_VOID_CHARGE = 6;

    private static final float BASIC_ATTACK_RANGE = 3.5F;
    private static final float BASIC_ATTACK_DAMAGE = 12.0F;
    private static final int BASIC_ATTACK_COOLDOWN = 40;

    private static final int SHORT_TELEPORT_COOLDOWN = 100;
    private static final double SHORT_TELEPORT_MIN_DISTANCE = 8.0D;
    private static final double SHORT_TELEPORT_MAX_DISTANCE = 24.0D;
    private static final double SHORT_TELEPORT_LANDING_OFFSET = 3.0D;
    private static final int SHORT_TELEPORT_MAX_ATTEMPTS = 10;
    private static final int SHORT_TELEPORT_GROUND_SEARCH_RANGE = 6;

    private static final float DEADLY_ESCAPE_DAMAGE = 40.0F;
    private static final float DEADLY_ESCAPE_RADIUS = 5.0F;
    private static final int DEADLY_ESCAPE_INTERVAL = 600;
    private static final int DEADLY_ESCAPE_RETURN_DELAY = 100;
    private static final double DEADLY_ESCAPE_FLEE_DISTANCE = 10.0D;

    private static final int GROUND_SEARCH_RANGE = 8;
    private static final int GROUND_SEARCH_ATTEMPTS = 10;

    private static final int FALL_SAFETY_MAX_FALL_TICKS = 20;
    private static final int FALL_SAFETY_CHECK_RADIUS = 3;

    private static final int VOID_ERUPTION_INTERVAL = 220;
    private static final float[] VOID_ERUPTION_RADII = {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F};
    private static final int VOID_ERUPTION_TELEGRAPH_TICKS = 8;
    private static final int VOID_ERUPTION_STAGE_DELAY = 6;
    private static final float VOID_ERUPTION_DAMAGE = 10.0F;
    private static final double VOID_ERUPTION_HEIGHT_BELOW = 1.0D;
    private static final double VOID_ERUPTION_HEIGHT_ABOVE = 6.0D;
    private static final int VOID_ERUPTION_PARTICLE_POINTS = 36;

    private static final int VOID_PURPLE = 0x9429DB;
    private static final DustParticleOptions VOID_ERUPTION_RING_DUST = new DustParticleOptions(VOID_PURPLE, 1.6F);
    private static final DustParticleOptions VOID_ERUPTION_PULSE_DUST = new DustParticleOptions(VOID_PURPLE, 2.2F);

    private boolean erupting = false;
    private int eruptionStageIndex = 0;
    private int eruptionStageTicks = 0;
    private BlockPos eruptionCenter = null;
    private final Set<UUID> eruptionHitEntities = new HashSet<>();

    private static final int VOID_CHARGE_INTERVAL = 240;
    private static final int VOID_CHARGE_MIN_COUNT = 2;
    private static final int VOID_CHARGE_MAX_COUNT = 2;
    private static final int VOID_CHARGE_SHOT_DELAY = 8;
    private static final float VOID_CHARGE_VELOCITY = 1.3F;
    private static final float VOID_CHARGE_INACCURACY = 1.0F;

    private boolean chargingVoidCharges = false;
    private int voidChargeShotsRemaining = 0;
    private int voidChargeShotTimer = 0;

    private int fallTicksWithoutGround = 0;
    private BlockPos lastSafeGroundPos = null;

    private static final double ARENA_HALF_WIDTH = 23.0D;
    private static final double ARENA_HEIGHT_BELOW = 10.0D;
    private static final double ARENA_HEIGHT_ABOVE = 9.0D;
    private static final int NO_TARGET_RETURN_DELAY = 200;
    private int noTargetTicks = 0;

    private BlockPos spawnerPos;

    private static final EntityDataAccessor<Integer> DATA_ATTACK_TYPE =
            SynchedEntityData.defineId(Endersent.class, EntityDataSerializers.INT);

    private int attackCooldown = 0;
    private int shortTeleportCooldown = 0;
    private int deadlyEscapeCooldown = DEADLY_ESCAPE_INTERVAL;
    private int voidEruptionCooldown = VOID_ERUPTION_INTERVAL;
    private int voidChargeCooldown = VOID_CHARGE_INTERVAL;
    private int returnTimer = -1;
    private BlockPos spawnPos;

    protected final ServerBossEvent bossEvent =
            new ServerBossEvent(this.getDisplayName(),
                    BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public Endersent(EntityType<? extends Endersent> entityType, Level level) {
        super(entityType, level);
        this.spawnPos = this.blockPosition();
        SurvivalDreams.LOGGER.info("[SpawnDebug] Endersent CONSTRUCTOR llamado pos={}", this.blockPosition());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.ATTACK_DAMAGE, BASIC_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    protected boolean hasBossBar() {
        return true;
    }

    protected boolean canDeadlyEscape() {
        return true;
    }

    protected boolean canVoidEruption() {
        return true;
    }

    protected boolean canVoidCharge() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void checkDespawn() {
    }

    public void setSpawnerPos(BlockPos pos) {
        this.spawnerPos = pos;
        this.spawnPos = pos;
    }

    private AABB getArenaBox() {
        double cx = spawnPos.getX() + 0.5D;
        double cy = spawnPos.getY();
        double cz = spawnPos.getZ() + 0.5D;

        return new AABB(
                cx - ARENA_HALF_WIDTH, cy - ARENA_HEIGHT_BELOW, cz - ARENA_HALF_WIDTH,
                cx + ARENA_HALF_WIDTH, cy + ARENA_HEIGHT_ABOVE, cz + ARENA_HALF_WIDTH
        );
    }

    private boolean isWithinArena(Entity entity) {
        return getArenaBox().contains(entity.position());
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        if (this.spawnerPos != null && this.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getBlockEntity(this.spawnerPos) instanceof EndersentSpawnerBlockEntity spawner) {
                spawner.markDefeated();
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ATTACK_TYPE, ATTACK_NONE);
    }

    public int getAttackType() {
        return this.entityData.get(DATA_ATTACK_TYPE);
    }

    private void setAttackType(int type) {
        this.entityData.set(DATA_ATTACK_TYPE, type);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason spawnReason, SpawnGroupData spawnGroupData) {
        this.spawnPos = this.blockPosition();
        SurvivalDreams.LOGGER.info("[SpawnDebug] Endersent FINALIZESPAWN llamado pos={}", this.blockPosition());
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENDERSENT_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ENDERSENT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENDERSENT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(ModSounds.ENDERSENT_STEP, 1.0F, 1.0F);
    }

    public void playStunSound() {
        this.playSound(ModSounds.ENDERSENT_STUN, 1.0F, 1.0F);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (hasBossBar()) {
            this.bossEvent.addPlayer(serverPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (hasBossBar()) {
            this.bossEvent.removePlayer(serverPlayer);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) attackCooldown--;
        if (shortTeleportCooldown > 0) shortTeleportCooldown--;

        if (this.level() instanceof ServerLevel serverLevel) {
            handleFallSafety(serverLevel);
            VoidParticles.spawnAmbient(serverLevel, this, 0.1F);

            if (hasBossBar()) {
                this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            }

            if (returnTimer > 0) {
                returnTimer--;
                if (returnTimer == 0) {
                    this.teleportTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                }
                return;
            }

            LivingEntity target = this.getTarget();

            boolean anyPlayerInArena = !serverLevel.getEntitiesOfClass(
                    Player.class, getArenaBox()
            ).isEmpty();

            if (anyPlayerInArena) {
                this.noTargetTicks = 0;
            } else {
                this.noTargetTicks++;
                if (this.noTargetTicks >= NO_TARGET_RETURN_DELAY) {
                    returnToSpawn();
                    this.noTargetTicks = 0;
                    return;
                }
            }

            if (target != null && !isWithinArena(target)) {
                returnToSpawn();
                return;
            }

            if (target != null && shortTeleportCooldown == 0) {
                double distSqr = this.distanceToSqr(target);
                if (distSqr > SHORT_TELEPORT_MIN_DISTANCE * SHORT_TELEPORT_MIN_DISTANCE
                        && distSqr < SHORT_TELEPORT_MAX_DISTANCE * SHORT_TELEPORT_MAX_DISTANCE) {
                    if (tryShortTeleportTowards(serverLevel, target)) {
                        shortTeleportCooldown = SHORT_TELEPORT_COOLDOWN;
                    }
                }
            }

            if (erupting) {
                updateVoidEruption(serverLevel);
            } else {
                if (canVoidEruption() && voidEruptionCooldown > 0) {
                    voidEruptionCooldown--;
                }
                if (canVoidEruption() && voidEruptionCooldown <= 0 && target != null) {
                    startVoidEruption(serverLevel, target);
                }
            }

            if (chargingVoidCharges) {
                updateVoidChargeBurst(serverLevel);
            } else {
                if (canVoidCharge() && voidChargeCooldown > 0) {
                    voidChargeCooldown--;
                }
                if (canVoidCharge() && voidChargeCooldown <= 0 && target != null) {
                    startVoidChargeBurst();
                }
            }

            if (!erupting && !chargingVoidCharges) {
                if (canDeadlyEscape() && deadlyEscapeCooldown > 0) {
                    deadlyEscapeCooldown--;
                }
                if (canDeadlyEscape() && deadlyEscapeCooldown <= 0 && target != null) {
                    performDeadlyEscape(serverLevel);
                    deadlyEscapeCooldown = DEADLY_ESCAPE_INTERVAL;
                }
            }
        }
    }

    private void returnToSpawn() {
        this.setTarget(null);
        this.getNavigation().stop();
        this.teleportTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        if (attackCooldown > 0) {
            return false;
        }
        attackCooldown = BASIC_ATTACK_COOLDOWN;
        groundSmash(serverLevel, BASIC_ATTACK_DAMAGE, BASIC_ATTACK_RANGE);
        return true;
    }

    private void groundSmash(ServerLevel serverLevel, float damage, float radius) {
        this.setAttackType(ATTACK_BASIC_VARIANT);
        this.swing(InteractionHand.MAIN_HAND);

        this.playSound(ModSounds.ENDERSENT_ATTACK, 1.0F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);

        dealAreaDamage(serverLevel, damage, radius, true, true);
    }

    private boolean tryShortTeleportTowards(ServerLevel serverLevel, LivingEntity target) {
        RandomSource random = this.random;

        for (int attempt = 0; attempt < SHORT_TELEPORT_MAX_ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double offsetX = Math.cos(angle) * SHORT_TELEPORT_LANDING_OFFSET;
            double offsetZ = Math.sin(angle) * SHORT_TELEPORT_LANDING_OFFSET;

            double targetX = target.getX() + offsetX;
            double targetZ = target.getZ() + offsetZ;
            double targetY = target.getY();

            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(
                    Mth.floor(targetX), Mth.floor(targetY), Mth.floor(targetZ));

            boolean foundGround = false;
            for (int dy = 0; dy < SHORT_TELEPORT_GROUND_SEARCH_RANGE; dy++) {
                BlockPos below = checkPos.below(dy);
                BlockState belowState = serverLevel.getBlockState(below);
                if (belowState.isFaceSturdy(serverLevel, below, Direction.UP)) {
                    checkPos.set(below.getX(), below.getY() + 1, below.getZ());
                    foundGround = true;
                    break;
                }
            }

            if (!foundGround) continue;

            AABB landingBox = this.getDimensions(this.getPose())
                    .makeBoundingBox(checkPos.getX() + 0.5, checkPos.getY(), checkPos.getZ() + 0.5);

            if (!serverLevel.noCollision(this, landingBox)) continue;

            BlockState landingState = serverLevel.getBlockState(checkPos);
            if (!landingState.getFluidState().isEmpty()) continue;

            this.teleportTo(checkPos.getX() + 0.5D, checkPos.getY(), checkPos.getZ() + 0.5D);
            this.setAttackType(ATTACK_BASIC_VARIANT);
            this.playSound(ModSounds.ENDERSENT_TELEPORT_SMASH, 1.2F, 1.0F);
            return true;
        }

        return false;
    }

    private void performDeadlyEscape(ServerLevel serverLevel) {
        this.setAttackType(ATTACK_BASIC_VARIANT);
        this.swing(InteractionHand.MAIN_HAND);

        this.playSound(ModSounds.ENDERSENT_DEADLY_ESCAPE, 1.2F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);

        dealAreaDamage(serverLevel, DEADLY_ESCAPE_DAMAGE, DEADLY_ESCAPE_RADIUS, false, true);

        LivingEntity target = this.getTarget();
        if (target != null) {
            Vec3 fleeDir = this.position().subtract(target.position()).normalize();
            BlockPos fleeTarget = this.blockPosition().offset(
                    (int) (fleeDir.x * DEADLY_ESCAPE_FLEE_DISTANCE),
                    0,
                    (int) (fleeDir.z * DEADLY_ESCAPE_FLEE_DISTANCE));

            BlockPos safeFleePos = findSafeGroundNear(serverLevel, fleeTarget.getX(), fleeTarget.getY(), fleeTarget.getZ());
            if (safeFleePos != null) {
                this.teleportTo(safeFleePos.getX() + 0.5D, safeFleePos.getY(), safeFleePos.getZ() + 0.5D);
            }
        }

        int watchlingCount = 1 + this.random.nextInt(4);
        for (int i = 0; i < watchlingCount; i++) {
            double rawX = this.getX() + (this.random.nextDouble() - 0.5D) * 4.0D;
            double rawZ = this.getZ() + (this.random.nextDouble() - 0.5D) * 4.0D;

            BlockPos safeSpawnPos = findSafeGroundNear(serverLevel, rawX, this.getY(), rawZ);
            if (safeSpawnPos == null) continue;

            Watchling watchling = ModEntityTypes.WATCHLING.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            if (watchling == null) continue;

            watchling.setPos(safeSpawnPos.getX() + 0.5D, safeSpawnPos.getY(), safeSpawnPos.getZ() + 0.5D);
            serverLevel.addFreshEntity(watchling);
        }

        returnTimer = DEADLY_ESCAPE_RETURN_DELAY;
    }

    private void startVoidEruption(ServerLevel serverLevel, LivingEntity target) {
        this.erupting = true;
        this.eruptionCenter = target.blockPosition();
        this.eruptionStageIndex = 0;
        this.eruptionStageTicks = VOID_ERUPTION_TELEGRAPH_TICKS;
        this.eruptionHitEntities.clear();

        this.setAttackType(ATTACK_VOID_ERUPTION);
        this.playSound(ModSounds.ENDERSENT_DEADLY_ESCAPE, 1.0F, 0.7F);

        spawnRingParticles(serverLevel, this.eruptionCenter, VOID_ERUPTION_RADII[0]);
    }

    private void updateVoidEruption(ServerLevel serverLevel) {
        if (this.eruptionCenter == null) {
            this.erupting = false;
            return;
        }

        this.eruptionStageTicks--;

        if (this.eruptionStageTicks % 4 == 0) {
            spawnRingParticles(serverLevel, this.eruptionCenter, VOID_ERUPTION_RADII[this.eruptionStageIndex]);
        }

        if (this.eruptionStageTicks <= 0) {
            triggerEruptionPulse(serverLevel, VOID_ERUPTION_RADII[this.eruptionStageIndex]);
            this.eruptionStageIndex++;

            if (this.eruptionStageIndex >= VOID_ERUPTION_RADII.length) {
                this.erupting = false;
                this.eruptionCenter = null;
                this.voidEruptionCooldown = VOID_ERUPTION_INTERVAL;
            } else {
                this.eruptionStageTicks = VOID_ERUPTION_STAGE_DELAY;
            }
        }
    }

    private void triggerEruptionPulse(ServerLevel serverLevel, float radius) {
        double cx = this.eruptionCenter.getX() + 0.5D;
        double cy = this.eruptionCenter.getY();
        double cz = this.eruptionCenter.getZ() + 0.5D;

        this.playSound(ModSounds.ENDERSENT_ATTACK, 1.1F, 0.8F);

        serverLevel.sendParticles(VOID_ERUPTION_PULSE_DUST,
                cx, cy + 1.0, cz, 40, radius * 0.4, 0.6, radius * 0.4, 0.02);

        AABB damageBox = new AABB(
                cx - radius, cy - VOID_ERUPTION_HEIGHT_BELOW, cz - radius,
                cx + radius, cy + VOID_ERUPTION_HEIGHT_ABOVE, cz + radius);

        for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class, damageBox)) {
            if (nearby == this) continue;
            if (nearby instanceof Monster) continue;

            double dx = nearby.getX() - cx;
            double dz = nearby.getZ() - cz;
            if (Math.sqrt(dx * dx + dz * dz) > radius + 0.5D) continue;

            boolean hit = nearby.hurtServer(serverLevel, this.damageSources().mobAttack(this), VOID_ERUPTION_DAMAGE);
            if (hit) {
                this.eruptionHitEntities.add(nearby.getUUID());
                nearby.addEffect(new MobEffectInstance(
                        ModEffects.VOIDED_HOLDER,
                        VOIDED_DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                ));
            }
        }
    }

    private void spawnRingParticles(ServerLevel serverLevel, BlockPos center, float radius) {
        double cx = center.getX() + 0.5D;
        double cy = center.getY() + 0.1D;
        double cz = center.getZ() + 0.5D;

        for (int i = 0; i < VOID_ERUPTION_PARTICLE_POINTS; i++) {
            double angle = (Math.PI * 2 * i) / VOID_ERUPTION_PARTICLE_POINTS;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            serverLevel.sendParticles(VOID_ERUPTION_RING_DUST, px, cy, pz, 1, 0.0, 0.2, 0.0, 0.0);
        }
    }

    private void startVoidChargeBurst() {
        this.chargingVoidCharges = true;
        this.voidChargeShotsRemaining = VOID_CHARGE_MIN_COUNT
                + this.random.nextInt(VOID_CHARGE_MAX_COUNT - VOID_CHARGE_MIN_COUNT + 1);
        this.voidChargeShotTimer = 0;
    }

    private void updateVoidChargeBurst(ServerLevel serverLevel) {
        LivingEntity target = this.getTarget();
        if (target == null) {
            this.chargingVoidCharges = false;
            this.voidChargeCooldown = VOID_CHARGE_INTERVAL;
            return;
        }

        if (this.voidChargeShotTimer > 0) {
            this.voidChargeShotTimer--;
            return;
        }

        fireVoidCharge(serverLevel, target);
        this.voidChargeShotsRemaining--;

        if (this.voidChargeShotsRemaining <= 0) {
            this.chargingVoidCharges = false;
            this.voidChargeCooldown = VOID_CHARGE_INTERVAL;
        } else {
            this.voidChargeShotTimer = VOID_CHARGE_SHOT_DELAY;
        }
    }

    private void fireVoidCharge(ServerLevel serverLevel, LivingEntity target) {
        this.setAttackType(ATTACK_VOID_CHARGE);
        this.swing(InteractionHand.OFF_HAND);
        this.playSound(ModSounds.ENDERSENT_ATTACK, 1.0F, 1.5F);

        double spawnX = this.getX();
        double spawnY = this.getEyeY() - 0.1D;
        double spawnZ = this.getZ();

        VoidCharge charge = new VoidCharge(serverLevel, this, spawnX, spawnY, spawnZ);

        double dx = target.getX() - spawnX;
        double dy = (target.getY() + target.getBbHeight() * 0.5D) - spawnY;
        double dz = target.getZ() - spawnZ;

        charge.shoot(dx, dy, dz, VOID_CHARGE_VELOCITY, VOID_CHARGE_INACCURACY);
        serverLevel.addFreshEntity(charge);
    }

    private BlockPos findSafeGroundNear(ServerLevel serverLevel, double x, double y, double z) {
        RandomSource random = this.random;

        for (int attempt = 0; attempt < GROUND_SEARCH_ATTEMPTS; attempt++) {
            double sampleX = x + (attempt == 0 ? 0.0 : (random.nextDouble() - 0.5) * 6.0);
            double sampleZ = z + (attempt == 0 ? 0.0 : (random.nextDouble() - 0.5) * 6.0);

            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(
                    Mth.floor(sampleX), Mth.floor(y), Mth.floor(sampleZ));

            for (int dy = 0; dy < GROUND_SEARCH_RANGE; dy++) {
                BlockPos below = checkPos.below(dy);
                if (below.getY() < serverLevel.getMinY()) break;
                BlockState belowState = serverLevel.getBlockState(below);
                if (belowState.isFaceSturdy(serverLevel, below, Direction.UP)) {
                    BlockPos landing = below.above();
                    BlockState fluidCheck = serverLevel.getBlockState(landing);
                    if (fluidCheck.getFluidState().isEmpty()) {
                        return landing;
                    }
                }
            }

            for (int dy = 1; dy < GROUND_SEARCH_RANGE; dy++) {
                BlockPos below = checkPos.above(dy).below(1);
                if (below.getY() < serverLevel.getMinY()) break;
                BlockState belowState = serverLevel.getBlockState(below);
                if (belowState.isFaceSturdy(serverLevel, below, Direction.UP)) {
                    BlockPos landing = below.above();
                    BlockState fluidCheck = serverLevel.getBlockState(landing);
                    if (fluidCheck.getFluidState().isEmpty()) {
                        return landing;
                    }
                }
            }
        }

        return null;
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

    private void dealAreaDamage(ServerLevel serverLevel, float damage, float radius, boolean applyKnockback, boolean applyVoidEffect) {
        AABB box = this.getBoundingBox().inflate(radius);
        for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
            if (nearby == this) continue;
            if (nearby instanceof Monster) continue;

            boolean hit = nearby.hurtServer(serverLevel, this.damageSources().mobAttack(this), damage);

            if (hit && applyVoidEffect) {
                nearby.addEffect(new MobEffectInstance(
                        ModEffects.VOIDED_HOLDER,
                        VOIDED_DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                ));
            }

            if (applyKnockback) {
                double dx = nearby.getX() - this.getX();
                double dz = nearby.getZ() - this.getZ();
                double dist = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
                nearby.setDeltaMovement(nearby.getDeltaMovement().add(
                        (dx / dist) * 0.8, 0.4, (dz / dist) * 0.8
                ));
            }
        }
    }
}