package dev.wux.survivaldreams.recipe;

import dev.wux.survivaldreams.SurvivalDreams;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    public static final RecipeSerializer<WildfireReinforceRecipe> WILDFIRE_REINFORCE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceKey.create(Registries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "wildfire_reinforce")),
            new WildfireReinforceRecipeSerializer()
    );

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando recipe serializers de " + SurvivalDreams.MOD_ID);
    }
}