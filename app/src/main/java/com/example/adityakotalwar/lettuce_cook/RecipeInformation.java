package com.example.adityakotalwar.lettuce_cook;
//package com.mashape.p.spoonacularrecipefoodnutritionv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

////import com.mashape.p.spoonacularrecipefoodnutritionv1.controllers.*;
////import com.mashape.p.spoonacularrecipefoodnutritionv1.http.client.HttpClient;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.JsonNode;
//import com.mashape.unirest.http.Unirest;
//import com.mashape.unirest.http.exceptions.UnirestException;


public class RecipeInformation extends AppCompatActivity {
    private Button groceryButton;
    private Button stockButton;
    private Button friendsButton;
    private Button recipesButton;
    private Button buttonRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);

        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        buttonRecipe = findViewById(R.id.buttonRecipe);

        buttonRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getRecipe();
            }
        });

        recipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeInformation.this,Recipes.class);
                startActivity(intent);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeInformation.this,MainActivity.class);
                startActivity(intent);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeInformation.this,Grocery.class);
                startActivity(intent);
            }
        });
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeInformation.this,Grocery.class);
                startActivity(intent);
            }
        });

    }
//    public void getRecipe(){
//        try {
//            HttpResponse<JsonNode> response = Unirest.get("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How+much+vitamin+c+is+in+2+apples%3F")
//                    .header("X-RapidAPI-Key", "489f0a43bbmshdbcadc67d147cfap1af9eajsnb1f4a4f4f5f9")
//                    .asJson();
//            System.out.println(response);
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}