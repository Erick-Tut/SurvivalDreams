package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;

public class RainSlowFallingHandler {

    private static final int AMPLIFIER = 3;
    private static final int DURATION = 100;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (ServerPlayer player : level.players()) {
                if (level.isRainingAt(player.blockPosition())) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.SLOW_FALLING,
                            DURATION,
                            AMPLIFIER,
                            true,
                            false,
                            true
                    ));
                }
            }
        });
    }
}