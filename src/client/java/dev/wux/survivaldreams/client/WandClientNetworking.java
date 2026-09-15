package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.items.ModItems;
import dev.wux.survivaldreams.mixin.ItemCooldownsAccessor;
import dev.wux.survivaldreams.network.WandSyncPayload;
import dev.wux.survivaldreams.wand.ClientWandState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemCooldowns;

import java.util.Map;

public final class WandClientNetworking {

    private WandClientNetworking() {}

    public static void initializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WandSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> handleSync(payload)));
    }

    @SuppressWarnings("unchecked")
    private static void handleSync(WandSyncPayload payload) {
        ClientWandState.setActive(payload.active());

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        Identifier wandId = BuiltInRegistries.ITEM.getKey(ModItems.LUNAR_WAND);
        ItemCooldowns cooldowns = client.player.getCooldowns();
        ItemCooldownsAccessor accessor = (ItemCooldownsAccessor) cooldowns;
        Map<Identifier, Object> cooldownsMap = accessor.survivalDreams$getCooldownsMap();

        int remaining = payload.remainingCooldownTicks();
        if (remaining <= 0) {
            cooldownsMap.remove(wandId);
            return;
        }

        int elapsed = payload.totalCooldownTicks() - remaining;
        int tickCount = accessor.survivalDreams$getTickCount();
        int startTime = tickCount - elapsed;
        int endTime = tickCount + remaining;

        cooldownsMap.put(wandId, CooldownInstanceFactory.create(startTime, endTime));
    }
}