package com.example.adityakotalwar.lettuce_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Recipes extends AppCompatActivity {

    private ListView recipeView;
    ArrayList<String> recipes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recipeView = (ListView) findViewById(R.id.my_list_view2);
        // in this class content from api needs to be collected and displayed on the
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes);
        recipeView.setAdapter(arrayAdapter);

        recipes.add("API CALLS WE NEED TO ADD");
        recipes.add("GANG GANG");


    }
}
