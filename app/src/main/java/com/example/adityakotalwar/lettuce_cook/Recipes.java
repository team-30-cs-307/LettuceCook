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
import java.util.Arrays;
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

    ArrayList<String> list = new ArrayList<>();

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
                                            button_share.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            String household = documentSnapshot.getString("household");
                                                            db.collection("Household").document(household).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                    String temp = documentSnapshot.getString("friends");
                                                                    System.out.println("FRIENDS: " + temp);
                                                                    ArrayList<String> friends = new ArrayList<>(Arrays.asList(temp.split(" ")));

                                                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recipes.this);
                                                                    View mView = getLayoutInflater().inflate(R.layout.dialog_ingr_select, null);

                                                                    final ListView friend_list = mView.findViewById(R.id.listViewStock);
                                                                    final Button submit_list = mView.findViewById(R.id.get_recipe);
                                                                    final TextView plain_text = mView.findViewById(R.id.plain_text);
                                                                    plain_text.setText("Friend Households");

                                                                    mBuilder.setView(mView);
                                                                    final AlertDialog dialog1 = mBuilder.create();
                                                                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                    dialog1.show();

                                                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                                            Recipes.this,
                                                                            android.R.layout.simple_list_item_multiple_choice,friends
                                                                    );
                                                                    friend_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                                                    friend_list.setAdapter(adapter);

                                                                    submit_list.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            String ingrs = "";
                                                                            int cntChoice = friend_list.getCount();
                                                                            ArrayList<String> share_friends = new ArrayList<>();
                                                                            SparseBooleanArray sparseBooleanArray = friend_list.getCheckedItemPositions();
                                                                            for (int i = 0; i < cntChoice; i++) {
                                                                                if (sparseBooleanArray.get(i)) {
                                                                                    share_friends.add(friend_list.getItemAtPosition(i).toString());
//                                                                          ingrs += friend_list.getItemAtPosition(i).toString() + " ";
                                                                                }
                                                                            }

                                                                            for (int i = 0; i < share_friends.size(); i++) {
                                                                                final DocumentReference house = db.collection("Household").document(share_friends.get(i));
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
                                                                            dialog1.dismiss();
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
                            });
                        }



                    }
                });

            }
        });

        chooseIngreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Recipes.this,SuggestedRecipe.class);
//                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(Recipes.this,
//                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
//                startActivity(intent, bundle);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recipes.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_ingr_select, null);

                final ListView ingredients = mView.findViewById(R.id.listViewStock);
                final Button submit = mView.findViewById(R.id.get_recipe);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String hName = documentSnapshot.getString("household");
                        db.collection("Household").document(hName).collection("Grocery Items").whereEqualTo("status", "stock")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String selected = "";
                                        //  ArrayList<String> list = new ArrayList<>();
                                        list.clear();
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            list.add(document.getId());
                                        }

                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                Recipes.this,
                                                android.R.layout.simple_list_item_multiple_choice,list
                                                );
                                        ingredients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                        ingredients.setAdapter(adapter);
                                    }
                                });
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ArrayList<String> selected = new ArrayList<>();
                        String ingrs = "";
                        int cntChoice = ingredients.getCount();
                        SparseBooleanArray sparseBooleanArray = ingredients.getCheckedItemPositions();
                        for (int i = 0; i < cntChoice; i++) {
                            if (sparseBooleanArray.get(i)) {
//                                selected.add(ingredients.getItemAtPosition(i).toString());
                                ingrs += ingredients.getItemAtPosition(i).toString() + " ";
                            }
                        }
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), SuggestedRecipe.class);
                        intent.putExtra("ingredients", ingrs);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });


            }
        });

        recipesButton.setTextColor(Color.parseColor("#5D993D"));
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this, Friends.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this,MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this,Grocery.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        sharedRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String house = documentSnapshot.getString("household");
                        Intent intent = new Intent(Recipes.this, SavedRecipe.class);
                        intent.putExtra("house",house);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

            }
        });

    }

    public void getMissingIngredients(){}

    }




}



