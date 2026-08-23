package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.ModEffects;
import dev.wux.survivaldreams.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class VoidCharge extends AbstractWindCharge {

    private static final int VOIDED_DURATION_TICKS = 100;

    private static final float DIRECT_HIT_DAMAGE = 6.0F;

    private static final float MINI_RING_DAMAGE = 6.0F;
    private static final float MINI_RING_RADIUS = 2.2F;
    private static final double MINI_RING_HEIGHT_BELOW = 1.0D;
    private static final double MINI_RING_HEIGHT_ABOVE = 3.0D;

    private static final float CLOUD_RADIUS = 2.2F;
    private static final int CLOUD_DURATION_TICKS = 100;
    private static final int CLOUD_EFFECT_INTERVAL_TICKS = 20;

    private static final String VOID_TEAM_NAME = "survivaldreams_void_charge";

    private static final int VOID_PURPLE = 0x9429DB;
    private static final DustParticleOptions VOID_DUST = new DustParticleOptions(VOID_PURPLE, 1.4F);
    private static final DustParticleOptions VOID_DUST_TRAIL = new DustParticleOptions(VOID_PURPLE, 0.9F);

    private static final int TRAIL_INTERVAL_TICKS = 2;

    private boolean voidTintApplied = false;

    public VoidCharge(EntityType<? extends AbstractWindCharge> entityType, Level level) {
        super(entityType, level);
    }

    public VoidCharge(Level level, LivingEntity owner, double x, double y, double z) {
        super(ModEntityTypes.VOID_CHARGE, level, owner, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.voidTintApplied && this.level() instanceof ServerLevel serverLevel) {
            applyVoidTint(serverLevel);
            this.voidTintApplied = true;
        }

        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % TRAIL_INTERVAL_TICKS == 0) {
            serverLevel.sendParticles(VOID_DUST_TRAIL,
                    this.getX(), this.getY(), this.getZ(), 2, 0.05, 0.05, 0.05, 0.0);
        }
    }

    private void applyVoidTint(ServerLevel serverLevel) {
        Scoreboard scoreboard = serverLevel.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(VOID_TEAM_NAME);

        if (team == null) {
            team = scoreboard.addPlayerTeam(VOID_TEAM_NAME);
            team.setColor(ChatFormatting.DARK_PURPLE);
            team.setNameTagVisibility(Team.Visibility.NEVER);
            team.setCollisionRule(Team.CollisionRule.NEVER);
        }

        scoreboard.addPlayerToTeam(this.getStringUUID(), team);
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    protected void explode(Vec3 pos) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(ParticleTypes.PORTAL,
                pos.x, pos.y, pos.z, 30, 0.4, 0.4, 0.4, 0.1);

        serverLevel.sendParticles(VOID_DUST,
                pos.x, pos.y, pos.z, 25, 0.5, 0.5, 0.5, 0.0);

        serverLevel.playSound(null, pos.x, pos.y, pos.z,
                ModSounds.ENDERSENT_ATTACK, SoundSource.HOSTILE, 1.0F, 1.3F);

        dealMiniRingDamage(serverLevel, pos);
        spawnVoidedCloud(serverLevel, pos);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity ownerEntity = this.getOwner();
        LivingEntity owner = ownerEntity instanceof LivingEntity l ? l : null;
        Entity target = result.getEntity();

        target.hurtServer(serverLevel, this.damageSources().mobProjectile(this, owner), DIRECT_HIT_DAMAGE);

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

    private void dealMiniRingDamage(ServerLevel serverLevel, Vec3 pos) {
        AABB box = new AABB(
                pos.x - MINI_RING_RADIUS, pos.y - MINI_RING_HEIGHT_BELOW, pos.z - MINI_RING_RADIUS,
                pos.x + MINI_RING_RADIUS, pos.y + MINI_RING_HEIGHT_ABOVE, pos.z + MINI_RING_RADIUS);

        Entity ownerEntity = this.getOwner();
        LivingEntity owner = ownerEntity instanceof LivingEntity l ? l : null;

        for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
            if (nearby instanceof Monster) continue;

            double dx = nearby.getX() - pos.x;
            double dz = nearby.getZ() - pos.z;
            if (Math.sqrt(dx * dx + dz * dz) > MINI_RING_RADIUS + 0.5D) continue;

            boolean hit = nearby.hurtServer(serverLevel, this.damageSources().mobProjectile(this, owner), MINI_RING_DAMAGE);
            if (hit) {
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

    private void spawnVoidedCloud(ServerLevel serverLevel, Vec3 pos) {
        AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, pos.x, pos.y, pos.z);
        cloud.setOwner(this.getOwner() instanceof LivingEntity l ? l : null);

        cloud.setRadius(CLOUD_RADIUS);
        cloud.setDuration(CLOUD_DURATION_TICKS);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(0.0F);
        cloud.setRadiusOnUse(0.0F);
        cloud.setDurationOnUse(0);
        cloud.addEffect(new MobEffectInstance(ModEffects.VOIDED_HOLDER, CLOUD_EFFECT_INTERVAL_TICKS + 1, 0, false, true, true));

        serverLevel.addFreshEntity(cloud);
    }
}