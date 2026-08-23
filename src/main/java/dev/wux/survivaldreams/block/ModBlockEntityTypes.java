package dev.wux.survivaldreams.block;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypes {

    public static final BlockEntityType<EndersentSpawnerBlockEntity> ENDERSENT_SPAWNER = register(
            "endersent_spawner",
            FabricBlockEntityTypeBuilder.create(EndersentSpawnerBlockEntity::new, ModBlocks.ENDERSENT_SPAWNER_BLOCK).build()
    );

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando block entities de " + SurvivalDreams.MOD_ID);
    }
}