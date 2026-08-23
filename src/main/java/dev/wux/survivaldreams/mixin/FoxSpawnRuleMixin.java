package dev.wux.survivaldreams.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fox.class)
public abstract class FoxSpawnRuleMixin {

    @Inject(method = "checkFoxSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void survivalDreams$allowSpawnAtNight(
            EntityType<Fox> entityType,
            LevelAccessor levelAccessor,
            EntitySpawnReason entitySpawnReason,
            BlockPos blockPos,
            RandomSource randomSource,
            CallbackInfoReturnable<Boolean> cir) {

        boolean validBlock = levelAccessor.getBlockState(blockPos.below()).is(BlockTags.FOXES_SPAWNABLE_ON);
        cir.setReturnValue(validBlock);
    }
}