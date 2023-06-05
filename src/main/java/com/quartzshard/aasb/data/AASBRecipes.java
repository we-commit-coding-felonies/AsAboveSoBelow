package com.quartzshard.aasb.data;

import java.util.function.Consumer;

import com.quartzshard.aasb.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public class AASBRecipes extends RecipeProvider{

    public AASBRecipes(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer){

    }
	
	


	@Override
	public String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | Recipes";
	}
}
