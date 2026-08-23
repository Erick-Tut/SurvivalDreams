package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(DragonChargePlayerPhase.class)
public class DragonChargePlayerPhaseMixin {

    @ModifyConstant(method = "doServerTick", constant = @Constant(intValue = 10))
    private int survivalDreams$fasterAim(int original) {
        return (int) (original * 0.75F);
    }
}