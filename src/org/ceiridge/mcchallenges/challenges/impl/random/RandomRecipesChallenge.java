package org.ceiridge.mcchallenges.challenges.impl.random;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.ceiridge.mcchallenges.challenges.Challenge;

public class RandomRecipesChallenge extends Challenge {
	private long seed;
	private ArrayList<Recipe> backupRecipes = new ArrayList<>();

	public RandomRecipesChallenge() {
		super("RandomRecipes",
				new String[] {"All craftable recipes and furnace recipes are randomized. Every wood type yields different results.", "The workbench, wood and sticks recipes haven't been changed.", "You can use the recipe book to quickly test all results."});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void reset() {
		if (this.enabled) {
			this.seed = System.currentTimeMillis();
			Random rnd = new Random(this.seed);

			ArrayList<ItemStack> resultStack = new ArrayList<>();
			ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<>();
			ArrayList<ShapelessRecipe> shapelessRecipes = new ArrayList<>();
			ArrayList<ShapedRecipe> shapeRecipes = new ArrayList<>();
			ArrayList<Recipe> ignoreRecipes = new ArrayList<>();

			Iterator<Recipe> recipes = Bukkit.recipeIterator();
			this.backupRecipes.clear();
			while (recipes.hasNext()) {
				Recipe rec = recipes.next();
				ItemStack stack = rec.getResult();
				Material result = stack.getType();

				this.backupRecipes.add(rec);

				if (result == Material.CRAFTING_TABLE || result == Material.STICK || result == Material.ACACIA_PLANKS
						|| result == Material.BIRCH_PLANKS || result == Material.DARK_OAK_PLANKS || result == Material.JUNGLE_PLANKS
						|| result == Material.OAK_PLANKS || result == Material.SPRUCE_PLANKS) {
					ignoreRecipes.add(rec);
				} else {
					if (rec instanceof FurnaceRecipe) {
						furnaceRecipes.add((FurnaceRecipe) rec);
					}

					if (rec instanceof ShapelessRecipe) {
						shapelessRecipes.add((ShapelessRecipe) rec);
					}

					if (rec instanceof ShapedRecipe) {
						shapeRecipes.add((ShapedRecipe) rec);
					}

					if (stack.getType() != Material.AIR && !resultStack.contains(stack))
						resultStack.add(stack);
				}
			}

			Bukkit.clearRecipes();

			for (Recipe rec : ignoreRecipes)
				Bukkit.addRecipe(rec);

			for (FurnaceRecipe fur : furnaceRecipes) {
				ItemStack rndStack = resultStack.get(rnd.nextInt(resultStack.size()));
				resultStack.remove(rndStack);

				FurnaceRecipe replacement = new FurnaceRecipe(rndStack, rndStack.getType());
				replacement.setInput(fur.getInput().getType());
				Bukkit.addRecipe(replacement);
			}

			for (ShapelessRecipe sl : shapelessRecipes) {
				ItemStack rndStack = resultStack.get(rnd.nextInt(resultStack.size()));
				resultStack.remove(rndStack);

				ShapelessRecipe replacement = new ShapelessRecipe(sl.getKey(), rndStack);
				for (ItemStack ingredient : sl.getIngredientList()) {
					replacement.addIngredient(ingredient.getType());
				}

				Bukkit.addRecipe(replacement);
			}

			try {
				Field ingredientsField = ShapedRecipe.class.getDeclaredField("ingredients");
				ingredientsField.setAccessible(true);

				for (ShapedRecipe sr : shapeRecipes) {
					ItemStack rndStack = resultStack.get(rnd.nextInt(resultStack.size()));
					resultStack.remove(rndStack);

					ShapedRecipe replacement = new ShapedRecipe(sr.getKey(), rndStack);
					replacement.shape(sr.getShape());
					ingredientsField.set(replacement, ingredientsField.get(sr));

					Bukkit.addRecipe(replacement);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "recipe give @a *");
		} else if (this.backupRecipes.size() > 0) {
			Bukkit.clearRecipes();

			for (Recipe rec : this.backupRecipes) {
				Bukkit.addRecipe(rec);
			}
		}
	}
}
