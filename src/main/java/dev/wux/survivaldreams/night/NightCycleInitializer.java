package dev.wux.survivaldreams.night;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

public final class NightCycleInitializer {

    private NightCycleInitializer() {}

    public static void initialize() {

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.dimension() == Level.OVERWORLD) {
                world.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                NightCycleManager.tick(world);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(NightCycleManager::tick);

        EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> false);
    }
}