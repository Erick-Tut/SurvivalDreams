package dev.wux.survivaldreams.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class WildfireReinforceRecipeSerializer implements RecipeSerializer<WildfireReinforceRecipe> {

    public static final MapCodec<WildfireReinforceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("addition").forGetter(WildfireReinforceRecipe::getAddition)
            ).apply(instance, WildfireReinforceRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, WildfireReinforceRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, WildfireReinforceRecipe::getAddition,
            WildfireReinforceRecipe::new
    );

    @Override
    public MapCodec<WildfireReinforceRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WildfireReinforceRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}