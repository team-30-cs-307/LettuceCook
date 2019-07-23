package com.example.adityakotalwar.lettuce_cook;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static java.security.AccessController.getContext;

public class SearchRecipe extends AppCompatActivity {

    private ListView recipeView;
    ArrayList<RecipeListViewItem> recipeSet = new ArrayList<>();
    private static CustomAdapterRecipe adapterRecipe;

    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button mapsButton;
    private Button searchButton;

    private String content;
    private EditText recipeName;

    private Button sharedRecipeButton;
    public ProgressDialog progressDialog;

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> ingredi = new ArrayList<>();

    final FirebaseFirestore db =  FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DrawerLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);
        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        mapsButton = findViewById(R.id.buttonMaps);

        recipeView = findViewById(R.id.my_list_view2);

        progressDialog = new ProgressDialog(this);
//        final  ArrayList<String> recipe2 = new ArrayList<>();

        coordinatorLayout =  findViewById(R.id.activity_drawer);

        coordinatorLayout.setOnTouchListener(new OnSwipeTouchListener(SearchRecipe.this) {
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), Friends.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        Intent intent = getIntent();
        String temp_ingr = intent.getStringExtra("recipe_name");
        String recipe_name = temp_ingr.replace(' ', '+');

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        String temp = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?number=10&offset=0&query="+recipe_name;
        String url = Uri.parse(temp).buildUpon().build().toString();

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray recipes = response.getJSONArray("results");

                            for(int i = 0; i < recipes.length(); i++){
                                JSONObject id =  recipes.getJSONObject(i);
                                String readInMin = id.getString("readyInMinutes");
                                String title = id.getString("title");
                                String rid = id.getString("id");
                                recipeSet.add(new RecipeListViewItem(rid,"","Ready In Minutes: " + readInMin,title,""));
                            }
                            adapterRecipe = new CustomAdapterRecipe(recipeSet, getApplicationContext());
                            recipeView.setAdapter(adapterRecipe);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-RapidAPI-Key", "489f0a43bbmshdbcadc67d147cfap1af9eajsnb1f4a4f4f5f9");
                return params;
            }

        };

        rq.add(request);

        recipesButton.setTextColor(Color.parseColor("#5D993D"));
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchRecipe.this, Friends.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchRecipe.this,MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchRecipe.this,Grocery.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        recipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchRecipe.this,Recipes.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        recipeView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchRecipe.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_show_recipe, null);

//                final EditText emailCurrent = (EditText) mView.findViewById(R.id.EmailCurrent);
                final TextView recipe_title = mView.findViewById(R.id.recipe_title);
                final TextView recipe_ingredients = mView.findViewById(R.id.recipe_ingredients);
                final TextView recipe_procedure = mView.findViewById(R.id.recipe_procedure);
                final Button button_save = mView.findViewById(R.id.button_save);
                final Button button_back = mView.findViewById(R.id.button_back);
                final Button button_share = mView.findViewById(R.id.button_share);
                final Button missIngrButton = mView.findViewById(R.id.buttonMissingIngr);
                button_share.setVisibility(View.INVISIBLE);

                mBuilder.setView(mView);
                // Pops the dialog on the screen
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final String id_recipe = recipeSet.get(position).id;
                final String recipe_name = recipeSet.get(position).recipeName;

                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                String temp = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/"+id_recipe+"/information";
                String url = Uri.parse(temp)
                        .buildUpon().build().toString();

                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String instructions = response.getString("instructions");
                                    String time = response.getString("readyInMinutes");

                                    String recipeIngr = "";
                                    JSONArray ingr_list = response.getJSONArray("extendedIngredients");

                                    for(int i = 0; i < ingr_list.length(); i++){
                                        JSONObject id =  ingr_list.getJSONObject(i);
                                        String ingredient = id.getString("name")+": "+id.getString("amount") + " " + id.getString("unit") + "\n";
                                        recipeIngr += ingredient;
                                        ingredi.add(id.getString("name"));
//                                        ingredi.add(id.getString("name"));
//                                        System.out.println("INGI "+ ingredient);
                                    }
//                                    System.out.println("Ingredients===="+recipeIngr);
                                    recipe_title.setText(recipe_name);
                                    recipe_ingredients.setText(recipeIngr);
                                    recipe_procedure.setText(instructions);

                                    missIngrButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getMissingIngredients();
                                        }
                                    });

                                    //getMissingIngredients(ingredients);

                                    final String finalRecipeIngr = recipeIngr;
                                    button_save.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {

                                            AlertDialog.Builder logout_confir = new AlertDialog.Builder(SearchRecipe.this);
                                            logout_confir.setMessage("Are you sure you want to save the Recipe?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                            final CollectionReference saveRecipe = db.collection("SavedRecipe");

                                                            db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    String household = documentSnapshot.getString("household");
                                                                    final DocumentReference house = db.collection("Household").document(household);
                                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                                                    String format = simpleDateFormat.format(new Date());

                                                                    SaveRecipeCollection saveRecipesCollection = new SaveRecipeCollection(recipe_name, recipe_ingredients.getText().toString(),recipe_procedure.getText().toString(), household, format);

                                                                    saveRecipe.add(saveRecipesCollection).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(final DocumentReference documentReference) {
                                                                            house.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                    String recipe = documentSnapshot.get("recipe_list").toString();
                                                                                    recipe += documentReference.getId() + " ";
                                                                                    house.update("recipe_list", recipe);
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });finish();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    });
                                            AlertDialog alertDialog = logout_confir.create();
                                            alertDialog.show();
                                            //Saves the Notification in NotificationCollection




                                        }
                                    });


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("X-RapidAPI-Key", "489f0a43bbmshdbcadc67d147cfap1af9eajsnb1f4a4f4f5f9");
                        return params;
                    }

                };

                rq.add(request);

                button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });


    }

    public void getMissingIngredients() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(db);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //final String id = firebaseAuth.getCurrentUser().getUid();
        //String household = GetCurrentHouseholdName();

        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String household = documentSnapshot.getString("household");
                db.collection("Household").document(household).collection("Grocery Items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> missingIngr = new ArrayList<>();
                        String miss = "";
                        for(String ingr : ingredi){
                            miss = ingr;
                            boolean contain = false;
                            for(QueryDocumentSnapshot qs : queryDocumentSnapshots){
                                if((ingr.contains(qs.getId()) || qs.getId().contains(ingr)) && qs.getString("status").equals("stock")){
                                    //   System.out.println("THIS IS THE ingredient     " + ingr);
                                    contain = true;
                                    break;
                                }
                            }
                            if(!contain){
                                missingIngr.add(miss);
                            }
                        }
                        printMissingIngredients(missingIngr);
                    }
                });
            }
        });

        //return missingIngr;
    }
    void printMissingIngredients(ArrayList<String> recipe_ingr){
        // Grocery gr = new Grocery();
        // final String[] missingIngredients = getMissingIngredients().split(":");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchRecipe.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingr_select, null);

        final ListView ingredients = mView.findViewById(R.id.listViewStock);
        final Button suggestIngredient = mView.findViewById(R.id.buttonSubstitute);
        final TextView heading = mView.findViewById(R.id.plain_text);
        final Button askAFriendButton = mView.findViewById(R.id.buttonAskFriend);
        final Button addGroceryButton = mView.findViewById(R.id.buttonAddGrocery);
        final TextView subsIngre = mView.findViewById(R.id.substitute);

        askAFriendButton.setVisibility(View.VISIBLE);
        addGroceryButton.setVisibility(View.VISIBLE);
        suggestIngredient.setVisibility(View.VISIBLE);
        heading.setText("Missing Ingredients");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        list.clear();
        for(String miss : recipe_ingr){
            list.add(miss);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SearchRecipe.this,
                android.R.layout.simple_list_item_multiple_choice,list
        );
        ingredients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ingredients.setAdapter(adapter);

        suggestIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        ArrayList<String> selected = new ArrayList<>();
                subsIngre.setVisibility(View.VISIBLE);
                content = "";
                int cntChoice = ingredients.getCount();
                SparseBooleanArray sparseBooleanArray = ingredients.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
                        subsIngre.append(ingredients.getItemAtPosition(i).toString().toUpperCase() + "\n\n");
//                            System.out.println("INGI : "+ ingredients.getItemAtPosition(i).toString().toUpperCase() + "\n");
                        String ingr = ingredients.getItemAtPosition(i).toString().replace(' ', '+');

                        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                        String temp = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/ingredients/substitutes?ingredientName="+ingr;
                        String url = Uri.parse(temp).buildUpon().build().toString();

                        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String message = response.getString("message");
                                            subsIngre.append(message + "\n");
                                            System.out.println("INGI m: "+ message + "\n");
                                            JSONArray ingr_list = response.getJSONArray("substitutes");
                                            for(int i = 0; i < ingr_list.length(); i++){
                                                subsIngre.append("    " + ingr_list.getString(i) + "\n");
                                            }
//
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                                params.put("X-RapidAPI-Key", "489f0a43bbmshdbcadc67d147cfap1af9eajsnb1f4a4f4f5f9");
                                return params;
                            }

                        };

                        rq.add(request);




                    }
                }
                subsIngre.setText(content);
//                    dialog.dismiss();
//                    Intent intent = new Intent(getApplicationContext(), SuggestedRecipe.class);
//                    intent.putExtra("ingredients", ingrs);
//                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        askAFriendButton.setOnClickListener(new View.OnClickListener() {
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

        addGroceryButton.setOnClickListener(new View.OnClickListener() {
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
}






