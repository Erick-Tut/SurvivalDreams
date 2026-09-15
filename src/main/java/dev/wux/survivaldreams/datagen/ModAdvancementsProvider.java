package dev.wux.survivaldreams.datagen;

import dev.wux.survivaldreams.SurvivalDreams;
import dev.wux.survivaldreams.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementsProvider extends FabricAdvancementProvider {

    public ModAdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
        HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);

        Identifier recipesRootId = Identifier.parse("minecraft:recipes/root");

        buildRecipeUnlockAdvancement(
                consumer, items, recipesRootId,
                "cheese_capra",
                ModItems.CRIMSON_MEMBRANE, Items.EGG, Items.MILK_BUCKET
        );

        buildRecipeUnlockAdvancement(
                consumer, items, recipesRootId,
                "end_portal_anchor",
                Items.OBSIDIAN, Items.ENDER_EYE, ModItems.ENDER_KEY
        );

        buildRecipeUnlockAdvancement(
                consumer, items, recipesRootId,
                "wildfire_core",
                Items.DIAMOND, ModItems.WILDFIRE_CORE, Items.BLAZE_ROD
        );
    }

    @SuppressWarnings({"deprecation", "removal"})
    private void buildRecipeUnlockAdvancement(
            Consumer<AdvancementHolder> consumer,
            HolderLookup.RegistryLookup<Item> items,
            Identifier parentId,
            String recipeId,
            Item... triggerItems
    ) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, recipeId)
        );

        Advancement.Builder builder = Advancement.Builder.advancement()
                .parent(parentId)
                .requirements(AdvancementRequirements.Strategy.OR)
                .rewards(new AdvancementRewards.Builder().addRecipe(recipeKey));

        for (Item item : triggerItems) {
            String criterionName = "has_" + BuiltInRegistries.ITEM.getKey(item).getPath();
            builder.addCriterion(
                    criterionName,
                    InventoryChangeTrigger.TriggerInstance.hasItems(
                            ItemPredicate.Builder.item().of(items, item)
                    )
            );
        }

        builder.save(consumer, SurvivalDreams.MOD_ID + ":recipes/" + recipeId);
    }
}