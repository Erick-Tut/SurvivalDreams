package dev.wux.survivaldreams.wand;

import dev.wux.survivaldreams.network.WandSyncPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class WandSync {

    private static boolean lastKnownActive = false;
    private static final List<UUID> pendingJoinSync = new ArrayList<>();

    private WandSync() {}

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(WandSync::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                pendingJoinSync.add(handler.player.getUUID()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.level() instanceof ServerLevel serverLevel) {
                sendTo(serverLevel.getServer(), newPlayer);
            }
        });
    }

    private static void onServerTick(MinecraftServer server) {
        if (!pendingJoinSync.isEmpty()) {
            for (UUID uuid : pendingJoinSync) {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    sendTo(server, player);
                }
            }
            pendingJoinSync.clear();
        }

        ServerLevel overworld = server.overworld();
        long now = overworld.getGameTime();
        WandState state = WandState.get(overworld);

        boolean active = state.isActive(now);
        if (active != lastKnownActive) {
            lastKnownActive = active;
            broadcast(server, state, now);
        }
    }

    private static void broadcast(MinecraftServer server, WandState state, long now) {
        WandSyncPayload payload = buildPayload(state, now);
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendTo(MinecraftServer server, ServerPlayer player) {
        ServerLevel overworld = server.overworld();
        long now = overworld.getGameTime();
        WandState state = WandState.get(overworld);
        ServerPlayNetworking.send(player, buildPayload(state, now));
    }

    private static WandSyncPayload buildPayload(WandState state, long now) {
        boolean active = state.isActive(now);
        int remaining = (int) state.ticksUntilReusable(now);
        return new WandSyncPayload(active, remaining, (int) WandState.COOLDOWN_TICKS);
    }
}