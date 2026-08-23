package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.handler.RaidGolemTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.raid.Raid;
import dev.wux.survivaldreams.entity.Firesnowgolem;
import dev.wux.survivaldreams.entity.ModEntityTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class RaidGolemMixin {

    @Shadow private int groupsSpawned;
    @Shadow private float totalHealth;
    @Shadow private Optional<BlockPos> waveSpawnPos;
    @Shadow private long ticksActive;

    @Shadow public abstract void updateBossbar();

    @Shadow public abstract BlockPos getCenter();

    @Unique
    private static final int BASE_COUNT = 3;
    @Unique
    private static final int PER_WAVE_INCREMENT = 1;
    @Unique
    private static final int MAX_COUNT = 9;

    @Unique
    private static final double SPAWN_RADIUS = 7.0;

    @Unique
    private static final int FIRESNOWGOLEM_RATIO = 4;

    @Unique
    private static final String GOLEM_TEAM_NAME = "survivaldreams_raid_golems";

    @Unique
    private static final Identifier WAVE_SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("survivaldreams", "raid_golem_wave_speed");

    @Unique
    private static final int MAX_SCALING_WAVE = 7;

    @Unique
    private static final double MAX_SPEED_BONUS = 0.15D;

    @Unique
    private RaidGolemTracker.Group survivaldreams$group;

    @Unique
    private ServerLevel survivaldreams$lastLevel;

    @Unique
    private int survivaldreams$lastWaveSpawned = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void survivaldreams$preventTimeout(CallbackInfo ci) {
        this.ticksActive = 0L;
    }

    @Inject(method = "spawnGroup", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$spawnGolemsInstead(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci) {
        this.survivaldreams$lastLevel = serverLevel;

        BlockPos villageCenter = this.getCenter();

        if (this.survivaldreams$group == null) {
            this.survivaldreams$group = RaidGolemTracker.findOrCreateNear(villageCenter, 64.0);
        }
        this.survivaldreams$group.center = villageCenter;

        int wave = this.groupsSpawned + 1;

        if (wave != this.survivaldreams$lastWaveSpawned) {
            survivaldreams$purgeGroup();
        }

        int count = golemCountForWave(wave);

        SurvivalDreams.LOGGER.info(
                "[RaidGolemMixin] Oleada=" + wave + ", count=" + count + ", center=" + villageCenter);

        this.totalHealth = 0.0F;

        PlayerTeam golemTeam = survivaldreams$getOrCreateGolemTeam(serverLevel);

        for (int i = 0; i < count; i++) {
            boolean spawnFire = (i % FIRESNOWGOLEM_RATIO) == (FIRESNOWGOLEM_RATIO - 1);

            Mob golem;
            if (spawnFire) {
                golem = ModEntityTypes.FIRESNOWGOLEM.create(serverLevel, EntitySpawnReason.EVENT);
                if (golem == null) {
                    SurvivalDreams.LOGGER.warn("[RaidGolemMixin] ModEntityTypes.FIRESNOWGOLEM.create devolvio null!");
                    continue;
                }
            } else {
                golem = EntityType.IRON_GOLEM.create(serverLevel, EntitySpawnReason.EVENT);
                if (golem == null) {
                    SurvivalDreams.LOGGER.warn("[RaidGolemMixin] EntityType.IRON_GOLEM.create devolvio null!");
                    continue;
                }
            }

            double angle = (Math.PI * 2 / count) * i;
            double jitter = (serverLevel.getRandom().nextDouble() - 0.5) * 2.0;
            double radius = SPAWN_RADIUS + jitter;
            int offsetBlockX = (int) Math.round(Math.cos(angle) * radius);
            int offsetBlockZ = (int) Math.round(Math.sin(angle) * radius);
            BlockPos desiredColumn = villageCenter.offset(offsetBlockX, 0, offsetBlockZ);

            float golemWidth = 1.4F;
            float golemHeight = 2.9F;

            BlockPos safePos = survivaldreams$findSafeSpawnPos(serverLevel, desiredColumn, golemWidth, golemHeight);
            if (safePos == null) {
                safePos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, desiredColumn);
                SurvivalDreams.LOGGER.warn(
                        "[RaidGolemMixin] No se encontro posicion libre en 3x3 alrededor de " + desiredColumn
                                + ", usando fallback " + safePos);
            }

            golem.snapTo(
                    safePos.getX() + 0.5,
                    safePos.getY(),
                    safePos.getZ() + 0.5,
                    0.0F, 0.0F);

            survivaldreams$applyGradualSpeed(golem, wave);

            if (golem instanceof IronGolem ironGolem) {
                applyWaveBuffs(ironGolem, wave);
            }

            serverLevel.getScoreboard().addPlayerToTeam(golem.getScoreboardName(), golemTeam);

            net.minecraft.world.entity.player.Player nearestPlayer = serverLevel.getNearestPlayer(
                    golem.getX(), golem.getY(), golem.getZ(), 64.0, false);
            if (nearestPlayer != null) {
                golem.setTarget(nearestPlayer);
            }

            boolean added = serverLevel.addFreshEntity(golem);
            SurvivalDreams.LOGGER.info(
                    "[RaidGolemMixin] Golem #" + i + " (" + golem.getType() + ") addFreshEntity=" + added
                            + " pos=" + golem.blockPosition());

            if (added) {
                this.totalHealth += golem.getHealth();
                RaidGolemTracker.addGolem(this.survivaldreams$group, golem.getUUID());
            }
        }

        this.survivaldreams$lastWaveSpawned = wave;
        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;

        cleanupDead();
        this.updateBossbar();

        ci.cancel();
    }

    @Unique
    private static PlayerTeam survivaldreams$getOrCreateGolemTeam(ServerLevel serverLevel) {
        Scoreboard scoreboard = serverLevel.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(GOLEM_TEAM_NAME);
        if (team == null) {
            team = scoreboard.addPlayerTeam(GOLEM_TEAM_NAME);
            team.setColor(net.minecraft.ChatFormatting.GRAY);
            team.setAllowFriendlyFire(false);
            team.setSeeFriendlyInvisibles(false);
            team.setNameTagVisibility(Team.Visibility.NEVER);
        }
        return team;
    }

    @Unique
    private static BlockPos survivaldreams$findSafeSpawnPos(ServerLevel level, BlockPos desiredColumn, float width, float height) {
        int[][] searchOffsets = {
                {0, 0},
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] offset : searchOffsets) {
            BlockPos columnBase = desiredColumn.offset(offset[0], 0, offset[1]);

            BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, columnBase);

            double half = width / 2.0;
            AABB spawnBox = new AABB(
                    surfacePos.getX() + 0.5 - half, surfacePos.getY(), surfacePos.getZ() + 0.5 - half,
                    surfacePos.getX() + 0.5 + half, surfacePos.getY() + height, surfacePos.getZ() + 0.5 + half
            );

            if (level.noCollision(spawnBox)) {
                return surfacePos;
            }
        }

        return null;
    }

    @Unique
    private void survivaldreams$purgeGroup() {
        if (this.survivaldreams$group == null || this.survivaldreams$lastLevel == null) return;

        for (UUID uuid : this.survivaldreams$group.golemIds) {
            Entity e = this.survivaldreams$lastLevel.getEntity(uuid);
            if (e != null && e.isAlive() && (e instanceof IronGolem || e instanceof Firesnowgolem)) {
                e.discard();
            }
        }
        this.survivaldreams$group.golemIds.clear();
    }

    @Unique
    private static void survivaldreams$applyGradualSpeed(Mob golem, int wave) {
        AttributeInstance speedAttribute = golem.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) return;

        double progress = Mth.clamp((wave - 1) / (double) (MAX_SCALING_WAVE - 1), 0.0D, 1.0D);
        double bonus = progress * MAX_SPEED_BONUS;

        if (bonus > 0.0D) {
            speedAttribute.addPermanentModifier(new AttributeModifier(
                    WAVE_SPEED_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @Unique
    private static void applyWaveBuffs(IronGolem golem, int wave) {
        final int longDuration = 24000;

        if (wave >= 4) {
            golem.addEffect(new MobEffectInstance(MobEffects.STRENGTH, longDuration, 0, true, false));
        }
        if (wave >= 5) {
            golem.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, longDuration, 0, true, false));
        }
        if (wave >= 6) {
            golem.addEffect(new MobEffectInstance(MobEffects.STRENGTH, longDuration, 1, true, false));
        }
    }

    @Inject(method = "getTotalRaidersAlive", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$countAliveGolems(CallbackInfoReturnable<Integer> cir) {
        if (this.survivaldreams$group == null) return;
        cleanupDead();
        cir.setReturnValue(this.survivaldreams$group.golemIds.size());
        cir.cancel();
    }

    @Inject(method = "getHealthOfLivingRaiders", at = @At("HEAD"), cancellable = true)
    private void survivaldreams$sumGolemHealth(CallbackInfoReturnable<Float> cir) {
        if (this.survivaldreams$group == null) return;
        cleanupDead();

        float sum = 0.0F;
        for (UUID uuid : this.survivaldreams$group.golemIds) {
            Entity e = this.survivaldreams$lastLevel.getEntity(uuid);
            if (e instanceof LivingEntity living) {
                sum += living.getHealth();
            }
        }
        cir.setReturnValue(sum);
        cir.cancel();
    }

    @Unique
    private void cleanupDead() {
        if (this.survivaldreams$lastLevel == null || this.survivaldreams$group == null) return;
        int before = this.survivaldreams$group.golemIds.size();

        this.survivaldreams$group.golemIds.removeIf(uuid -> {
            Entity e = this.survivaldreams$lastLevel.getEntity(uuid);
            return e != null && !e.isAlive();
        });
        if (this.survivaldreams$group.golemIds.size() != before) {
            this.updateBossbar();
        }
    }

    @Unique
    private static int golemCountForWave(int wave) {
        int count = BASE_COUNT + (wave - 1) * PER_WAVE_INCREMENT;
        return Math.min(count, MAX_COUNT);
    }
}