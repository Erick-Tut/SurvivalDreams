package dev.wux.survivaldreams.attachments;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class EndFoxAttachment {

    public static final AttachmentType<Boolean> IS_END = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "is_end_fox"),
            builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<Boolean> END_ROLLED = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "end_fox_rolled"),
            builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .copyOnDeath()
    );

    public static void initialize() {
    }
}