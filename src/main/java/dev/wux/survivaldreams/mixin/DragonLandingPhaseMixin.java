package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DragonLandingPhase.class)
public class DragonLandingPhaseMixin {

    @ModifyConstant(method = "getFlySpeed", constant = @Constant(floatValue = 1.5F))
    private float survivalDreams$fasterDescent(float original) {
        return original * 1.25F;
    }
}