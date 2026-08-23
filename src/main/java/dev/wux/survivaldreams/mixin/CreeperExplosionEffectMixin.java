package dev.wux.survivaldreams.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperExplosionEffectMixin {

    private static final double RADIUS = 5.0;

    @Inject(method = "explodeCreeper", at = @At("TAIL"))
    private void survivaldreams$explosionEffect(CallbackInfo ci) {
        Creeper self = (Creeper) (Object) this;
        if (!(self.level() instanceof ServerLevel level)) return;

        BlockPos pos = self.blockPosition();
        AABB area = new AABB(pos).inflate(RADIUS);

        boolean charged = self.isPowered();

        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (living == self) continue;
            if (charged) {
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            } else {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
            }
        }
    }
}