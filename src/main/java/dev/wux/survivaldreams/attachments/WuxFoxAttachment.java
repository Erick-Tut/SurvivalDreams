package dev.wux.survivaldreams.attachments;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class WuxFoxAttachment {

    public static final AttachmentType<Boolean> IS_WUX = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "is_wux_fox"),
            builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static void initialize() {
    }
}