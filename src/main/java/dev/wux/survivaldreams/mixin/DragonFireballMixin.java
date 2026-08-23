package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin {

    @Unique
    private static final int VOIDED_DURATION_TICKS = 100;

    @Unique
    private static final int VOIDED_AMPLIFIER = 0;

    @ModifyVariable(method = "onHit", at = @At("STORE"), ordinal = 0)
    private AreaEffectCloud survivalDreams$addVoidedEffect(AreaEffectCloud areaEffectCloud) {
        areaEffectCloud.addEffect(new MobEffectInstance(ModEffects.VOIDED_HOLDER, VOIDED_DURATION_TICKS, VOIDED_AMPLIFIER));
        return areaEffectCloud;
    }
}