package com.example.adityakotalwar.lettuce_cook;

public class SaveRecipeCollection {

//    String recipe_id;
    String recipe_title;
    String recipe_ingredients;
    String recipe_procedure;

    SaveRecipeCollection(String recipe_title, String recipe_ingredients, String recipe_procedure){
        this.recipe_title = recipe_title;
        this.recipe_ingredients = recipe_ingredients;
        this.recipe_procedure = recipe_procedure;
    }

}
