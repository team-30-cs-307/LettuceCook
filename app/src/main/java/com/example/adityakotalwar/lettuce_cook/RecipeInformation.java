package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

public class RecipeInformation extends AppCompatActivity {
    private Button groceryButton;
    private Button stockButton;
    private Button friendsButton;
    private Button recipesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);

        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);

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
}