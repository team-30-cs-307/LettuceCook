package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nullable;

public class SavedRecipe extends AppCompatActivity {

    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button mapsButton;

    private Button savedRecipeButton;

    private DrawerLayout coordinatorLayout;

    ListView sharedRecipeList;
    ListView recipeShared;

    ArrayList<RecipeListViewItem> recipeSet = new ArrayList<>();
    ArrayList<RecipeListViewItem> sharedRecipeSet = new ArrayList<>();

    private static CustomAdapterSharedRecipe adapterRecipe;
    private static CustomAdapterRecipeShared sharedAdapterRecipe;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    ArrayList<String[]> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipe);

        Intent intent = getIntent();
        final String house = intent.getStringExtra("house");

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        mapsButton = findViewById(R.id.buttonMaps);
        savedRecipeButton = findViewById(R.id.SavedRecipeButton);

        sharedRecipeList = findViewById(R.id.shared_recipe_list);
        recipeShared = findViewById(R.id.recipe_shared_with_friends);

        coordinatorLayout =  findViewById(R.id.activity_drawer);

        coordinatorLayout.setOnTouchListener(new OnSwipeTouchListener(SavedRecipe.this) {
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), Friends.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
            public void onSwipeBottom(){
                fillListView(house);
            }
        });

        sharedRecipeList.setOnTouchListener(new OnSwipeTouchListener(SavedRecipe.this){
            public void onSwipeBottom(){
                recipeSet.clear();
                sharedRecipeSet.clear();
                fillListView(house);
                fillList2View(house);
            }
        });
        fillListView(house);
        fillList2View(house);

        recipesButton.setTextColor(Color.parseColor("#5D993D"));
        recipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this, Recipes.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this, Friends.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this,Grocery.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this, MapsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        savedRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this, Recipes.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


    }

    void fillListView(final String house){
        recipeSet.clear();
        db.collection("Household").document(house).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final String[] temp_recipe = documentSnapshot.getString("shared_recipe_list").split(" ");
                final ArrayList<String> shared_recipe = new ArrayList<>(Arrays.asList(temp_recipe));
                db.collection("SavedRecipe").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot ds: queryDocumentSnapshots){
                            if(shared_recipe.contains(ds.getId())){
                                String id = ds.getId();
                                String title = ds.getString("recipe_title");
                                String owner = ds.getString("recipe_owner");
                                String desc = ds.getString("recipe_procedure");
                                String timestamp = ds.getString("timestamp");
                                recipeSet.add(new RecipeListViewItem(id, owner, timestamp, title, desc));
                            }
                        }
                        adapterRecipe = new CustomAdapterSharedRecipe(recipeSet, getApplicationContext(), house);
                        sharedRecipeList.setAdapter(adapterRecipe);
                    }
                });
            }
        });
    }

    void fillList2View(final String house){
        sharedRecipeSet.clear();
        db.collection("Household").document(house).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final String[] temp_recipe = documentSnapshot.getString("recipe_shared_with_friends").split(" ");
                final ArrayList<String> shared_recipe = new ArrayList<>(Arrays.asList(temp_recipe));
                db.collection("SavedRecipe").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot ds: queryDocumentSnapshots){
                            if(shared_recipe.contains(ds.getId())){
                                String id = ds.getId();
                                String title = ds.getString("recipe_title");
                                String owner = ds.getString("recipe_owner");
                                String desc = ds.getString("recipe_procedure");
                                String timestamp = ds.getString("timestamp");
                                sharedRecipeSet.add(new RecipeListViewItem(id, owner, timestamp, title, desc));
                            }
                        }
                        sharedAdapterRecipe = new CustomAdapterRecipeShared(sharedRecipeSet, getApplicationContext(), house);
                        recipeShared.setAdapter(sharedAdapterRecipe);
                    }
                });
            }
        });
    }


}
