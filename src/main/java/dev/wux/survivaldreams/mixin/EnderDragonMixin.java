package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {

    @Unique
    private boolean survivalDreams$hasSpawned80 = false;

    @Unique
    private boolean survivalDreams$hasSpawned50 = false;

    @Unique
    private boolean survivalDreams$hasSpawned20 = false;
    @Unique
    private static final int[] SEARCH_ANGLES = {0, 45, 90, 135, 180, 225, 270, 315};

    @Unique
    private static final int[] SEARCH_RADII = {6, 10, 14};

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void survivalDreams$checkReinforcements(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        Level level = self.level();

        if (!(level instanceof ServerLevel serverLevel)) return;

        float healthPct = self.getHealth() / self.getMaxHealth();

        if (!survivalDreams$hasSpawned80 && healthPct <= 0.8F) {
            survivalDreams$hasSpawned80 = true;
            survivalDreams$spawnReinforcements(serverLevel, self, ModEntityTypes.WATCHLING);
        }

        if (!survivalDreams$hasSpawned50 && healthPct <= 0.5F) {
            survivalDreams$hasSpawned50 = true;
            survivalDreams$spawnReinforcements(serverLevel, self, ModEntityTypes.ENDERSENT_DEFAULT);
        }

        if (!survivalDreams$hasSpawned20 && healthPct <= 0.2F) {
            survivalDreams$hasSpawned20 = true;
            survivalDreams$spawnReinforcements(serverLevel, self, ModEntityTypes.WATCHLING);
        }
    }

    @Unique
    private void survivalDreams$spawnReinforcements(ServerLevel level, EnderDragon dragon, EntityType<? extends Mob> entityType) {
        BlockPos center = dragon.blockPosition();

        int spawned = 0;
        for (int angleDeg : SEARCH_ANGLES) {
            if (spawned >= 2) break;

            BlockPos spawnPos = survivalDreams$findGroundPos(level, center, angleDeg);
            if (spawnPos == null) continue;

            Mob mob = entityType.create(level, EntitySpawnReason.EVENT);
            if (mob == null) continue;

            level.sendParticles(ParticleTypes.EXPLOSION,
                    spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                    10, 0.6, 0.4, 0.6, 0.02);

            mob.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5);
            level.addFreshEntity(mob);
            spawned++;
        }
    }

    @Unique
    private BlockPos survivalDreams$findGroundPos(ServerLevel level, BlockPos center, int angleDeg) {
        double rad = Math.toRadians(angleDeg);

        for (int radius : SEARCH_RADII) {
            int x = center.getX() + (int) (Math.cos(rad) * radius);
            int z = center.getZ() + (int) (Math.sin(rad) * radius);

            BlockPos found = survivalDreams$scanColumn(level, x, center.getY(), z);
            if (found != null) return found;
        }
        return null;
    }

    @Unique
    private BlockPos survivalDreams$scanColumn(ServerLevel level, int x, int startY, int z) {
        int minY = level.getMinY();
        int maxScanHeight = startY + 6;

        for (int y = maxScanHeight; y > minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);

            if (!level.isLoaded(pos)) continue;

            BlockState state = level.getBlockState(pos);
            if (!survivalDreams$isValidGround(state)) continue;

            BlockPos above = pos.above();
            BlockPos aboveTwo = pos.above(2);

            boolean clearAbove = level.getBlockState(above).isAir()
                    && level.getBlockState(aboveTwo).isAir();

            if (clearAbove) return above;
        }
        return null;
    }

    @Unique
    private boolean survivalDreams$isValidGround(BlockState state) {
        return !state.isAir()
                && state.isSolid()
                && !state.liquid();
    }
}