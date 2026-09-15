package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.night.NightCycleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Inject(
            method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void survivaldreams$throttleNaturalSpawn(
            MobCategory mobCategory,
            ServerLevel serverLevel,
            ChunkAccess chunkAccess,
            BlockPos blockPos,
            NaturalSpawner.SpawnPredicate spawnPredicate,
            NaturalSpawner.AfterSpawnCallback afterSpawnCallback,
            CallbackInfo ci) {

        if (mobCategory != MobCategory.MONSTER) return;
        if (serverLevel.dimension() != Level.OVERWORLD) return;
        if (!serverLevel.canSeeSky(blockPos)) return;
        if (!serverLevel.structureManager().getAllStructuresAt(blockPos).isEmpty()) return;

        float intensity = NightCycleManager.getIntensity(serverLevel);
        if (serverLevel.getRandom().nextFloat() >= intensity) {
            ci.cancel();
        }
    }
}