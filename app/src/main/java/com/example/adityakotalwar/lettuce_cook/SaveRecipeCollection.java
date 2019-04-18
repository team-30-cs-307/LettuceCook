package com.example.adityakotalwar.lettuce_cook;

public class SaveRecipeCollection {

//    String recipe_id;
    String recipe_title;
    String recipe_ingredients;
    String recipe_procedure;
    String recipe_owner;
    String timestamp;

    SaveRecipeCollection(String recipe_title, String recipe_ingredients, String recipe_procedure){
        this.recipe_title = recipe_title;
        this.recipe_ingredients = recipe_ingredients;
        this.recipe_procedure = recipe_procedure;
    }

    SaveRecipeCollection(String recipe_title, String recipe_ingredients, String recipe_procedure, String recipe_owner, String time_stamp){
        this.recipe_title = recipe_title;
        this.recipe_ingredients = recipe_ingredients;
        this.recipe_procedure = recipe_procedure;
        this.recipe_owner = recipe_owner;
        this.timestamp = time_stamp;
    }

}
