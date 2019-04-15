package com.example.adityakotalwar.lettuce_cook;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.firestore.DocumentReference;
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
import com.google.firebase.firestore.Query;
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
    ArrayList<RecipeListViewItem> recipeSet = new ArrayList<>();
    private static CustomAdapterRecipe adapterRecipe;

    ArrayList<String> recipes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button chooseIngreButton;

    private Button sharedRecipeButton;
    public ProgressDialog progressDialog;

    final FirebaseFirestore db =  FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DrawerLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        sharedRecipeButton = findViewById(R.id.SharedRecipeButton);
        chooseIngreButton = findViewById(R.id.buttonChooseIngredients);
        recipeView = findViewById(R.id.my_list_view2);

        progressDialog = new ProgressDialog(this);
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


        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String house = documentSnapshot.getString("household");
                System.out.println(house);
                db.collection("Household").document(house).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.getString("recipe_list") != null) {
                            String[] tempRecipes = documentSnapshot.get("recipe_list").toString().split(" ");
                            final int size = tempRecipes.length;
                            final ArrayList<String> temprec = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                temprec.add(tempRecipes[i]);
                            }

                            db.collection("SavedRecipe").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    recipeSet.clear();
                                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                                        if (temprec.contains(ds.getId())) {
                                            String recipe_title = ds.getString("recipe_title");
                                            recipeSet.add(new RecipeListViewItem(ds.getId(),house,"",recipe_title,""));
                                        }
                                    }

                                    adapterRecipe = new CustomAdapterRecipe(recipeSet, getApplicationContext());
                                    recipeView.setAdapter(adapterRecipe);

                                    recipeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recipes.this);
                                            View mView = getLayoutInflater().inflate(R.layout.dialog_show_recipe, null);

                                            final TextView recipe_title = mView.findViewById(R.id.recipe_title);
                                            final TextView recipe_ingredients = mView.findViewById(R.id.recipe_ingredients);
                                            final TextView recipe_procedure = mView.findViewById(R.id.recipe_procedure);
                                            final Button button_back = mView.findViewById(R.id.button_back);
                                            final Button button_share = mView.findViewById(R.id.button_share);

                                            db.collection("SavedRecipe").document(recipeSet.get(position).id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    recipe_title.setText(documentSnapshot.getString("recipe_title"));
                                                    recipe_ingredients.setText(documentSnapshot.getString("recipe_ingredients"));
                                                    recipe_procedure.setText(documentSnapshot.getString("recipe_procedure"));
                                                }
                                            });

                                            final String recipe_id = recipeSet.get(position).id;
                                            mBuilder.setView(mView);
                                            // Pops the dialog on the screen
                                            final AlertDialog dialog = mBuilder.create();
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.show();

                                            button_back.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            button_share.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    progressDialog.setMessage("Sharing Recipe with friends");
                                                    progressDialog.show();

                                                    db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            String household = documentSnapshot.getString("household");
                                                            db.collection("Household").document(household).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    String temp = documentSnapshot.getString("friends");
                                                                    String[] friends = temp.split(" ");
                                                                    for (int i = 0; i < friends.length; i++) {
                                                                        final DocumentReference house = db.collection("Household").document(friends[i]);
                                                                        house.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                String shared_recipe = documentSnapshot.getString("shared_recipe_list");
                                                                                if (shared_recipe.indexOf(recipe_id) == -1) {
                                                                                    shared_recipe += (recipe_id + " ");
                                                                                    house.update("shared_recipe_list", shared_recipe);
                                                                                } else {
                                                                                    Toast.makeText(getApplicationContext(), "The recipe is already shared with some of the hosueholds", Toast.LENGTH_LONG);
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }



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
        sharedRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this, SavedRecipe.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });

    }


//    public String GetHouseholdName(){
//        String household = db.collection("Users").document(user.getUid()).get().getResult().getString("Household");
//        return household;
//    }

}




