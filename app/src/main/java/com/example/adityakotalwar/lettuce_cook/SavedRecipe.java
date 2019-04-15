package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private Button savedRecipeButton;

    private DrawerLayout coordinatorLayout;

    ListView sharedRecipeList;
    ArrayList<RecipeListViewItem> recipeSet = new ArrayList<>();
    private static CustomAdapterRecipe adapterRecipe;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    ArrayList<String[]> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipe);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        savedRecipeButton = findViewById(R.id.SavedRecipeButton);

        sharedRecipeList = findViewById(R.id.shared_recipe_list);

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
        });

        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String household = documentSnapshot.getString("household");
                recipeSet.clear();
                db.collection("Household").document(household).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                adapterRecipe = new CustomAdapterRecipe(recipeSet, getApplicationContext());
                                sharedRecipeList.setAdapter(adapterRecipe);
                            }
                        });
                    }
                });

            }
        });

        sharedRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        recipesButton.setTextColor(Color.parseColor("#5D993D"));
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

        savedRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipe.this, Recipes.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });


    }
}
