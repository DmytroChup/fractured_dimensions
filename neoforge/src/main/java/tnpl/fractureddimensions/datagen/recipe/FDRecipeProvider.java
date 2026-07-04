package tnpl.fractureddimensions.datagen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.CookingBookCategory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class FDRecipeProvider extends RecipeProvider {

    public FDRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.RAW_AZURITE.get()),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        ModItems.AZURITE_INGOT.get(),
                        0.7f,
                        400
                ).unlockedBy("has_raw_azurite", has(ModItems.RAW_AZURITE.get()))
                .save(this.output);

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(ModItems.RAW_AZURITE.get()),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        ModItems.AZURITE_INGOT.get(),
                        0.7f,
                        200
                ).unlockedBy("has_raw_azurite", has(ModItems.RAW_AZURITE.get()))
                .save(this.output, Constants.MOD_ID + ":azurite_ingot_from_blasting_raw_azurite");
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new FDRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "FD Recipes";
        }
    }
}
