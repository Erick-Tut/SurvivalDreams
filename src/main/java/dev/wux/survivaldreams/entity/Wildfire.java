package dev.wux.survivaldreams.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import dev.wux.survivaldreams.ModSounds;

public class Wildfire extends Blaze {

    private static final int FIRE_SECONDS_ON_HIT = 5;

    public static final int SHIELD_COUNT = 4;
    public static final float SHIELD_MAX_HEALTH = 20.0F;
    public static final float ORBIT_SPEED = 0.05F;

    private static final EntityDataAccessor<Byte> DATA_BROKEN_SHIELDS =
            SynchedEntityData.defineId(Wildfire.class, EntityDataSerializers.BYTE);

    private final float[] shieldHealth = new float[SHIELD_COUNT];
    private int summonCooldown = 0;

    private final ServerBossEvent shieldBossEvent = new ServerBossEvent(
            Component.translatable("entity.survivaldreams.wildfire.shields"),
            BossEvent.BossBarColor.WHITE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public Wildfire(EntityType<? extends Wildfire> entityType, Level level) {
        super(entityType, level);
        for (int i = 0; i < SHIELD_COUNT; i++) {
            shieldHealth[i] = SHIELD_MAX_HEALTH;
        }
        this.shieldBossEvent.setProgress(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.SCALE, 1.8D);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BROKEN_SHIELDS, (byte) 0);
    }

    public float getShieldAngle(int index) {
        return this.tickCount * ORBIT_SPEED + (float) (index * (Math.PI / 2.0));
    }

    public int findNearestShield(Vec3 attackerPos) {
        Vec3 toAttacker = attackerPos.subtract(this.position());
        double attackAngle = Math.atan2(toAttacker.z, toAttacker.x);

        int best = -1;
        double bestDiff = Double.MAX_VALUE;

        for (int i = 0; i < SHIELD_COUNT; i++) {
            if (isShieldBroken(i)) continue;

            double shieldAngle = getShieldAngle(i);
            double diff = Math.abs(Mth.wrapDegrees((float) Math.toDegrees(attackAngle - shieldAngle)));

            if (diff < bestDiff) {
                bestDiff = diff;
                best = i;
            }
        }

        return best;
    }

    public boolean damageShield(int index, float amount) {
        if (index < 0 || index >= SHIELD_COUNT) return false;
        if (isShieldBroken(index)) return false;

        shieldHealth[index] -= amount;

        if (shieldHealth[index] <= 0.0F) {
            shieldHealth[index] = 0.0F;
            markShieldBroken(index);
        } else if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), ModSounds.WILDFIRE_SHIELD_DEBRIS_IMPACT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }

        updateShieldBossBar();
        return true;
    }

    private void updateShieldBossBar() {
        if (shieldsRemaining() <= 0) {
            this.shieldBossEvent.removeAllPlayers();
            return;
        }

        float current = 0.0F;
        for (int i = 0; i < SHIELD_COUNT; i++) {
            if (!isShieldBroken(i)) {
                current += shieldHealth[i];
            }
        }
        float max = SHIELD_COUNT * SHIELD_MAX_HEALTH;
        this.shieldBossEvent.setProgress(Mth.clamp(current / max, 0.0F, 1.0F));
        for (ServerPlayer player : this.bossEvent.getPlayers()) {
            this.shieldBossEvent.addPlayer(player);
        }
    }

    private void markShieldBroken(int index) {
        byte flags = this.entityData.get(DATA_BROKEN_SHIELDS);
        flags |= (1 << index);
        this.entityData.set(DATA_BROKEN_SHIELDS, flags);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), ModSounds.WILDFIRE_SHIELD_BROKEN, SoundSource.HOSTILE, 1.0F, 1.0F);
            serverLevel.playSound(null, this.blockPosition(), ModSounds.WILDFIRE_SHIELD_BREAK_VOCAL, SoundSource.HOSTILE, 1.0F, 1.0F);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                    this.getX(), this.getY() + 1.0, this.getZ(), 12, 0.4, 0.4, 0.4, 0.02);
        }
    }

    public boolean isShieldBroken(int index) {
        byte flags = this.entityData.get(DATA_BROKEN_SHIELDS);
        return (flags & (1 << index)) != 0;
    }

    public int shieldsRemaining() {
        byte flags = this.entityData.get(DATA_BROKEN_SHIELDS);
        return SHIELD_COUNT - Integer.bitCount(flags & 0xF);
    }

    public byte getBrokenShieldsMask() {
        return this.entityData.get(DATA_BROKEN_SHIELDS);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        if (shieldsRemaining() > 0) {
            Entity attacker = damageSource.getEntity();
            Vec3 attackerPos = attacker != null ? attacker.position() : this.position();

            int shieldIndex = findNearestShield(attackerPos);
            if (shieldIndex != -1) {
                return damageShield(shieldIndex, amount);
            }
        }

        boolean result = super.hurtServer(serverLevel, damageSource, amount);
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        return result;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        boolean hit = super.doHurtTarget(serverLevel, target);
        if (hit) {
            target.igniteForSeconds(FIRE_SECONDS_ON_HIT);
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WILDFIRE_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.WILDFIRE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WILDFIRE_DEATH;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos blockPos, net.minecraft.world.level.block.state.BlockState blockState) {
        this.playSound(ModSounds.WILDFIRE_STEP, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.getTarget() instanceof net.minecraft.world.entity.player.Player targetPlayer) {
            if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                this.setTarget(null);
                return;
            }
        }

        if (this.getTarget() == null) return;

        if (summonCooldown > 0) {
            summonCooldown--;
            return;
        }

        long nearbyBlazes = serverLevel.getEntitiesOfClass(Blaze.class, this.getBoundingBox().inflate(16.0))
                .stream()
                .filter(b -> !(b instanceof Wildfire))
                .count();

        if (nearbyBlazes < 2) {
            summonBlazes(serverLevel);
            summonCooldown = 200;
        }
    }

    private void summonBlazes(ServerLevel serverLevel) {
        int amount = 1 + serverLevel.random.nextInt(2);

        for (int i = 0; i < amount; i++) {
            Blaze blaze = EntityType.BLAZE.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            if (blaze == null) continue;

            blaze.addTag(dev.wux.survivaldreams.handler.WildfireSpawnerHandler.NO_CONVERT_TAG);

            double offsetX = (serverLevel.random.nextDouble() - 0.5) * 6;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 6;

            blaze.setPos(this.getX() + offsetX, this.getY() + 1.0, this.getZ() + offsetZ);
            if (this.getTarget() != null) {
                blaze.setTarget(this.getTarget());
            }

            serverLevel.addFreshEntity(blaze);
        }

        serverLevel.levelEvent(null, 1018, this.blockPosition(), 0);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
        if (shieldsRemaining() > 0) {
            this.shieldBossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
        this.shieldBossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.bossEvent.removeAllPlayers();
        this.shieldBossEvent.removeAllPlayers();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
        this.shieldBossEvent.removeAllPlayers();
    }
}