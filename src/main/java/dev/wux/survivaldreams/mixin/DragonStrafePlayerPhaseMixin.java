package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class DragonStrafePlayerPhaseMixin {

    @ModifyConstant(method = "doServerTick", constant = @Constant(intValue = 5))
    private int survivalDreams$fasterFireballCharge(int original) {
        return 2;
    }

    @ModifyConstant(method = "doServerTick", constant = @Constant(floatValue = 10.0F))
    private float survivalDreams$widerAimCone(float original) {
        return 25.0F;
    }
}