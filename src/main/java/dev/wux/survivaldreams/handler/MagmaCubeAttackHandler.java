package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;

public class MagmaCubeAttackHandler {

    private static final int POISON_DURATION = 100;
    private static final int POISON_AMPLIFIER = 1;

    private static final int STRENGTH_DURATION = 100;
    private static final int STRENGTH_AMPLIFIER = 1;

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof Player player)) return;
            if (!(damageSource.getEntity() instanceof MagmaCube magmaCube)) return;

            player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
            magmaCube.addEffect(new MobEffectInstance(MobEffects.STRENGTH, STRENGTH_DURATION, STRENGTH_AMPLIFIER));
        });
    }
}