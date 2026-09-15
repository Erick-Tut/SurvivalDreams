package dev.wux.survivaldreams.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.wux.survivaldreams.ModEffects;
import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class GuiAmbushIconMixin {

    private static final String[] AMBUSH_SUFFIXES = {"i", "ii", "iii", "iv", "v"};

    @Redirect(
            method = "renderEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;getMobEffectSprite(Lnet/minecraft/core/Holder;)Lnet/minecraft/resources/Identifier;"
            )
    )
    private Identifier survivaldreams$resolveAmbushSprite(Holder<MobEffect> holder, @Local MobEffectInstance mobEffectInstance) {
        if (holder.value() == ModEffects.AMBUSH) {
            int level = Math.min(mobEffectInstance.getAmplifier(), AMBUSH_SUFFIXES.length - 1);
            return Identifier.fromNamespaceAndPath(
                    SurvivalDreams.MOD_ID,
                    "mob_effect/ambush_" + AMBUSH_SUFFIXES[level]
            );
        }
        return Gui.getMobEffectSprite(holder);
    }
}