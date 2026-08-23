package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonAttributesMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true, remap = false)
    private static void survivalDreams$boostHealth(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue()
                .add(Attributes.MAX_HEALTH, 250.0D);
        cir.setReturnValue(builder);
    }
}