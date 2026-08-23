package dev.wux.survivaldreams.attachments;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class ModAttachments {

    public static final AttachmentType<Integer> BLOCKED_SLOTS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "blocked_slots"),
            builder -> builder
                    .initializer(() -> 0)
                    .persistent(Codec.INT)
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly())
    );

    public static final AttachmentType<Long> LOW_HEALTH_REGEN_COOLDOWN = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "low_health_regen_cooldown"),
            builder -> builder
                    .initializer(() -> 0L)
                    .persistent(Codec.LONG)
    );

    public static void initialize() {
    }
}