package dev.wux.survivaldreams;

import dev.wux.survivaldreams.effect.VoidedEffect;
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

    private static MobEffect register(String name, MobEffect effect) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        return Registry.register(BuiltInRegistries.MOB_EFFECT, id, effect);
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando efectos de " + SurvivalDreams.MOD_ID);
    }
}