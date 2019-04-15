package com.example.adityakotalwar.lettuce_cook;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static java.security.AccessController.getContext;

public class Recipes extends AppCompatActivity {

    private ListView recipeView;
    ArrayList<String> recipes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button chooseIngreButton;
    String title;
    String ingredients;
    String procedure;
    ArrayList<String> recipe2 = new ArrayList<>();
    ArrayList<String> recipeIngr = new ArrayList<>();
    ArrayList<String> recipeProc = new ArrayList<>();

    private DrawerLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        chooseIngreButton = findViewById(R.id.buttonChooseIngredients);
        recipeView = findViewById(R.id.my_list_view2);
//        final  ArrayList<String> recipe2 = new ArrayList<>();

        coordinatorLayout =  findViewById(R.id.activity_drawer);

        coordinatorLayout.setOnTouchListener(new OnSwipeTouchListener(Recipes.this) {
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), Friends.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String house = documentSnapshot.getString("household");
                System.out.println(house);
                db.collection("Household").document(house).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.getString("recipe_list") !=null) {
                            String[] tempRecipes = documentSnapshot.get("recipe_list").toString().split(" ");
                            final int size = tempRecipes.length;
                            final ArrayList<String> temprec = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                temprec.add(tempRecipes[i]);
                            }
                        db.collection("SavedRecipe").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    recipe2 = new ArrayList<>();
                                    recipeIngr = new ArrayList<>();
                                    recipeProc = new ArrayList<>();
                                    // in this class content from api needs to be collected and displayed on the
                                    arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, recipe2);
                                    recipeView.setAdapter(arrayAdapter);

                                    //    String[] tempRecipes = documentSnapshot.get("recipe_list").toString().split(" ");
                                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                                        if (temprec.contains(ds.getId())) {
                                            recipe2.add(ds.getString("recipe_title"));
                                            recipeIngr.add(ds.getString("recipe_ingredients"));
                                            recipeProc.add(ds.getString("recipe_procedure"));
                                            title = ds.getString("recipe_title");
                                            ingredients = ds.getString("recipe_ingredients");
                                            procedure = ds.getString("recipe_procedure");
                                        }
                                    }
                                }
                            });
                        }

                        recipeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(Recipes.this, "This user is already in the household", Toast.LENGTH_LONG).show();
//                                showSavedRecipe(arrayAdapter.getItem(position))
//                                startActivity(new Intent(getApplicationContext(), RecipeInformation.class));
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recipes.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_show_recipe, null);

//                final EditText emailCurrent = (EditText) mView.findViewById(R.id.EmailCurrent);
                                final TextView recipe_title = mView.findViewById(R.id.recipe_title);
                                final TextView recipe_ingredients = mView.findViewById(R.id.recipe_ingredients);
                                final TextView recipe_procedure = mView.findViewById(R.id.recipe_procedure);
                                final Button button_back = mView.findViewById(R.id.button_back);

                                recipe_title.setText(recipe2.get(position));
                                recipe_ingredients.setText(recipeIngr.get(position));
                                recipe_procedure.setText(recipeProc.get(position));

                                getMissingIngredients();

                                mBuilder.setView(mView);
                                // Pops the dialog on the screen
                                final AlertDialog dialog = mBuilder.create();
                                dialog.show();

                                button_back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                    }
                });

            }
        });

        chooseIngreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recipes.this,SuggestedRecipe.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(Recipes.this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
            }
        });

        recipesButton.setTextColor(Color.parseColor("#5D993D"));
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this, Friends.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this,Grocery.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });

    }

    public void getMissingIngredients(){

    }




}



