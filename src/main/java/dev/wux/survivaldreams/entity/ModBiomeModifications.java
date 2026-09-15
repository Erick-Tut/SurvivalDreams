package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;

public class ModBiomeModifications {

    public static void initialize() {

        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld()
                        .or(BiomeSelectors.foundInTheNether()),
                MobCategory.MONSTER,
                ModEntityTypes.WATCHLING,
                13,
                1, 1
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld()
                        .or(BiomeSelectors.foundInTheNether()),
                MobCategory.MONSTER,
                ModEntityTypes.ENDERSENT_DEFAULT,
                5,
                1, 1
        );


        SurvivalDreams.LOGGER.info(
                "[SurvivalDreams] BiomeModifications aplicadas (Overworld + Nether + End)"
        );
    }
}