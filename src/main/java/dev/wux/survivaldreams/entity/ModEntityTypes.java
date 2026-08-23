package dev.wux.survivaldreams.entity;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntityTypes {

    public static final EntityType<Wildfire> WILDFIRE = register(
            "wildfire",
            EntityType.Builder.of(Wildfire::new, MobCategory.MONSTER)
                    .sized(1.8F, 2.6F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<Watchling> WATCHLING = register(
            "watchling",
            EntityType.Builder.of(Watchling::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.4F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<Endersent> ENDERSENT = register(
            "endersent",
            EntityType.Builder.of(Endersent::new, MobCategory.MONSTER)
                    .sized(2.6F, 5.8F)
                    .clientTrackingRange(12)
    );

    public static final EntityType<EndersentDefault> ENDERSENT_DEFAULT = register(
            "endersent_default",
            EntityType.Builder.of(EndersentDefault::new, MobCategory.MONSTER)
                    .sized(2.6F, 5.8F)
                    .clientTrackingRange(12)
    );

    public static final EntityType<Firesnowgolem> FIRESNOWGOLEM = register(
            "firesnowgolem",
            EntityType.Builder.of(Firesnowgolem::new, MobCategory.MISC)
                    .sized(0.7F, 1.9F)
                    .clientTrackingRange(8)
    );

    public static final EntityType<VoidCharge> VOID_CHARGE = register(
            "void_charge",
            EntityType.Builder.<VoidCharge>of(VoidCharge::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .noSave()
    );

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
                BuiltInRegistries.ENTITY_TYPE.key(),
                Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name)
        );
        EntityType<T> entityType = builder.build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, entityType);
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(WILDFIRE, Wildfire.createAttributes());
        FabricDefaultAttributeRegistry.register(WATCHLING, Watchling.createAttributes());
        FabricDefaultAttributeRegistry.register(ENDERSENT, Endersent.createAttributes());
        FabricDefaultAttributeRegistry.register(ENDERSENT_DEFAULT, EndersentDefault.createAttributes());
        FabricDefaultAttributeRegistry.register(FIRESNOWGOLEM, Firesnowgolem.createAttributes());

        SpawnPlacements.register(
                WILDFIRE,
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules
        );

        SpawnPlacements.register(
                WATCHLING,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, spawnType, pos, random) -> {
                    if (level.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
                        return false;
                    }

                    boolean solidBelow = level.getBlockState(pos.below()).isSolid();
                    if (!solidBelow) {
                        return false;
                    }

                    boolean isEnd = level.getBiome(pos).is(net.minecraft.tags.BiomeTags.IS_END);
                    if (isEnd) {
                        return pos.getY() >= 0 && pos.getY() <= 200;
                    }

                    int lightLevel = level.getMaxLocalRawBrightness(pos);
                    return lightLevel <= 7;
                }
        );

        SpawnPlacements.register(
                ENDERSENT_DEFAULT,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
        );


        SurvivalDreams.LOGGER.info("Registrando entidades de " + SurvivalDreams.MOD_ID);
    }
}