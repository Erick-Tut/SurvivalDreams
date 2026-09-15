package dev.wux.survivaldreams.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WandSyncPayload(boolean active, int remainingCooldownTicks, int totalCooldownTicks)
        implements CustomPacketPayload {

    public static final Type<WandSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("survival-dreams", "wand_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WandSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, WandSyncPayload::active,
                    ByteBufCodecs.VAR_INT, WandSyncPayload::remainingCooldownTicks,
                    ByteBufCodecs.VAR_INT, WandSyncPayload::totalCooldownTicks,
                    WandSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}