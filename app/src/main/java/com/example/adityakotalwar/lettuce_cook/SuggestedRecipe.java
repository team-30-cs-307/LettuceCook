package com.example.adityakotalwar.lettuce_cook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SuggestedRecipe extends AppCompatActivity {

    ListView myList;
    ListView suggRecipeList;
    Button getChoice;
    ArrayList<String> recipes = new ArrayList<String>();


    String[] listContent = {

            "January",

            "February",

            "March",

            "April",

            "May",

            "June",

            "July",

            "August",

            "September",

            "October",

            "November",

            "December"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggrecipe);
        myList = (ListView) findViewById(R.id.listViewStock);

        getChoice = (Button) findViewById(R.id.getChoiceButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                listContent);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(adapter);
        getChoice.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = "";
                int cntChoice = myList.getCount();
                SparseBooleanArray sparseBooleanArray = myList.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
                        selected += myList.getItemAtPosition(i).toString() + "\n";
                    }

                }
                System.out.println(selected+"SHSHSHSHS");
                Toast.makeText(SuggestedRecipe.this, selected, Toast.LENGTH_LONG).show();
            }
        });
        suggRecipeList = findViewById(R.id.listViewSuggestedRecipes);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes);
        suggRecipeList.setAdapter(adapter);
        recipes.add("SUGGESTED RECIPE 1");
        recipes.add("SUGGESTED RECIPE 2");


    }

}
