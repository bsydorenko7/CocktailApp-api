package com.netcracker.coctail.dao;


import com.netcracker.coctail.model.CreateRecipe;
import com.netcracker.coctail.model.Ingredient;
import com.netcracker.coctail.model.Kitchenware;
import com.netcracker.coctail.model.Recipe;
import com.netcracker.coctail.model.UserToRecipe;

import java.util.List;

public interface RecipeDao {

    List<Recipe> findRecipeByName(String name);

    void createRecipe(CreateRecipe recipe);

    List<Recipe> findRecipeById(Integer id);

    void addIngredientToRecipe(Integer recipeId, Long ingredientId);

    void addKitchenwareToRecipe(Integer recipeId, Long kitchenwareId);

    void removeIngredientFromRecipe(Integer recipeId, Long ingredientId);

    void removeKitchenwareFromRecipe(Integer recipeId, Long kitchenwareId);

    List<Recipe> findAllRecipesByName(String name);

    List<Recipe> findAllRecipesFiltered(String sugarless, String alcohol);

    boolean ingredientInRecipe(Integer recipeId, Long ingredientId);

    boolean kitchenwareInRecipe(Integer recipeId, Long kitchenwareId);

    void editRecipe(Recipe recipe);

    void removeRecipe(int id);

    void addToFavourites(long ownerId, int recipeId);

    void likeRecipe(int recipeId);

    void likedLock(long userId, int recipeId, boolean liked);

    void favouriteLock(long userId, int recipeId, boolean liked);

    List<UserToRecipe> checkLike(long userId, int recipeId);

    void withdrawLike(int recipeId);

    List<Ingredient> containsIngredients(int recipeId);

    List<Kitchenware> containsKitchenware(int recipeId);

    List<Recipe> getSuggestion(Long id);

    List<Recipe> favList(Long id);
}
