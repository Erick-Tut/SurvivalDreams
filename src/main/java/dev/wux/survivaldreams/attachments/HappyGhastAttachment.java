package dev.wux.survivaldreams.attachments;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class HappyGhastAttachment {

    public static final AttachmentType<Boolean> IS_CHARGING = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "happy_ghast_charging"),
            builder -> builder
                    .initializer(() -> false)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static void initialize() {
    }
}