package dev.wux.survivaldreams;

import dev.wux.survivaldreams.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {

    public static final MobEffect VOIDED = register(
            "voided",
            new VoidedEffect(MobEffectCategory.HARMFUL, 0x0D0019)
    );

    public static final Holder<MobEffect> VOIDED_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(VOIDED);

    public static final MobEffect AMBUSH = register(
            "ambush",
            new AmbushEffect(MobEffectCategory.HARMFUL, 0x8B0000)
    );

    public static final MobEffect IMMUNITY = register(
            "immunity",
            new ImmunityEffect(MobEffectCategory.BENEFICIAL, 0xCA79D6)
    );

    public static final Holder<MobEffect> IMMUNITY_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IMMUNITY);

    public static final Holder<MobEffect> AMBUSH_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMBUSH);

    private static MobEffect register(String name, MobEffect effect) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        return Registry.register(BuiltInRegistries.MOB_EFFECT, id, effect);
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando efectos de " + SurvivalDreams.MOD_ID);
    }
}