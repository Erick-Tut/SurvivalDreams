package dev.wux.survivaldreams.attachments;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public class ZombifiedPiglinAttachment {

    public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<Boolean> IS_EXPLOSIVE =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "piglin_explosive"),
                    builder -> builder.initializer(() -> false).persistent(Codec.BOOL)
            );

    public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<Boolean> DECIDED =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "piglin_decided"),
                    builder -> builder.initializer(() -> false).persistent(Codec.BOOL)
            );

    public static void initialize() {
    }
}