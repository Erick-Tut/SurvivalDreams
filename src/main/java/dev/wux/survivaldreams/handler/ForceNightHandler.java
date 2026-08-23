package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ForceNightHandler {

    private static final long NIGHT_TIME = 18000L;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var stack = server.createCommandSourceStack();
            server.getCommands().performPrefixedCommand(stack, "gamerule doDaylightCycle false");
            server.getCommands().performPrefixedCommand(stack, "gamerule players_sleeping_percentage 101");

            server.overworld().setDayTime(NIGHT_TIME);
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            long dayTime = level.getDayTime() % 24000L;
            if (dayTime != NIGHT_TIME) {
                level.setDayTime(NIGHT_TIME);
            }
        });
    }
}