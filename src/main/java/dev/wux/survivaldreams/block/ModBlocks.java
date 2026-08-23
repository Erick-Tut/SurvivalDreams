package dev.wux.survivaldreams.block;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

    public static final Block ENDERSENT_SPAWNER_BLOCK = register(
            "endersent_spawner_block",
            EndersentSpawnerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .noCollision()
                    .noLootTable()
    );

    public static final SealedEndPortalFrameBlock SEALED_END_PORTAL_FRAME = register(
            "sealed_end_portal_frame",
            SealedEndPortalFrameBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()
                    .lightLevel(state -> 1)
                    .sound(SoundType.GLASS)
                    .pushReaction(PushReaction.BLOCK)
    );

    public static final KeyholeBlock KEYHOLE = register(
            "keyhole",
            KeyholeBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(KeyholeBlock.UNLOCKED) ? 7 : 3)
                    .pushReaction(PushReaction.BLOCK)
    );

    public static final EndPortalAnchorBlock END_PORTAL_ANCHOR = register(
            "end_portal_anchor",
            EndPortalAnchorBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(50.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)
    );

    private static <T extends Block> T register(String name, java.util.function.Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        T block = factory.apply(properties.setId(net.minecraft.resources.ResourceKey.create(BuiltInRegistries.BLOCK.key(), id)));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando bloques de " + SurvivalDreams.MOD_ID);
    }
}