package dev.wux.survivaldreams.recipe;

import dev.wux.survivaldreams.component.ModDataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class WildfireReinforceRecipe implements SmithingRecipe {

    private static final Identifier TRIM_PATTERN_ID =
            Identifier.fromNamespaceAndPath("survival-dreams", "wildfire");
    private static final Identifier TRIM_MATERIAL_ID =
            Identifier.fromNamespaceAndPath("survival-dreams", "wildfire_core");

    private final Ingredient addition;
    private PlacementInfo placementInfo;

    public WildfireReinforceRecipe(Ingredient addition) {
        this.addition = addition;
    }

    public Ingredient getAddition() {
        return addition;
    }

    private static Ingredient armorIngredient() {
        return BuiltInRegistries.ITEM.get(ItemTags.TRIMMABLE_ARMOR)
                .<Ingredient>map(Ingredient::of)
                .orElseGet(() -> Ingredient.of(
                        Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE,
                        Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS
                ));
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        if (!SmithingRecipe.super.matches(input, level)) {
            return false;
        }
        return !input.base().has(ModDataComponents.WILDFIRE_REINFORCED);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = input.base().copy();
        result.set(ModDataComponents.WILDFIRE_REINFORCED, Unit.INSTANCE);
        result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        HolderLookup.RegistryLookup<TrimPattern> trimPatterns = registries.lookupOrThrow(Registries.TRIM_PATTERN);
        HolderLookup.RegistryLookup<TrimMaterial> trimMaterials = registries.lookupOrThrow(Registries.TRIM_MATERIAL);

        Optional<net.minecraft.core.Holder.Reference<TrimPattern>> pattern = trimPatterns.get(
                ResourceKey.create(Registries.TRIM_PATTERN, TRIM_PATTERN_ID)
        );
        Optional<net.minecraft.core.Holder.Reference<TrimMaterial>> material = trimMaterials.get(
                ResourceKey.create(Registries.TRIM_MATERIAL, TRIM_MATERIAL_ID)
        );

        if (pattern.isPresent() && material.isPresent()) {
            result.set(DataComponents.TRIM, new ArmorTrim(material.get(), pattern.get()));
        } else {
            System.out.println("[WildfireTrim] FALLO - pattern presente: " + pattern.isPresent() + ", material presente: " + material.isPresent());
        }

        return result;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.empty();
    }

    @Override
    public Ingredient baseIngredient() {
        return armorIngredient();
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return ModRecipeSerializers.WILDFIRE_REINFORCE_SERIALIZER;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(
                    templateIngredient(),
                    Optional.of(baseIngredient()),
                    additionIngredient()
            ));
        }
        return this.placementInfo;
    }
}