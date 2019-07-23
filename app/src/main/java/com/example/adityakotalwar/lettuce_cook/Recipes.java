package com.example.adityakotalwar.lettuce_cook;
import android.app.ActionBar;
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
import android.widget.EditText;
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
import java.util.Calendar;
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
    private Button mapsButton;
    private Button chooseIngreButton;
    private Button searchButton;

    private EditText recipeName;

    private Button sharedRecipeButton;
    private String household;
    public ProgressDialog progressDialog;

    ArrayList<String> list = new ArrayList<>();
    String recipe_ingr = "";

    final FirebaseFirestore db =  FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DrawerLayout coordinatorLayout;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        mapsButton = findViewById(R.id.buttonMaps);

        sharedRecipeButton = findViewById(R.id.SharedRecipeButton);
        chooseIngreButton = findViewById(R.id.buttonChooseIngredients);
        searchButton = findViewById(R.id.search_button);
        recipeName = findViewById(R.id.recipe_search);
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
                final String house = documentSnapshot.getString("household");
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
                                            final Button button_missingingr = mView.findViewById(R.id.buttonMissingIngr);

                                            recipe_ingr = "";

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
                                            //getMissingIngredients();

                                            button_missingingr.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.collection("SavedRecipe").document(recipeSet.get(position).id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            recipe_ingr = documentSnapshot.getString("recipe_ingredients");
                                                            System.out.println(recipe_ingr+ " RECIPE INGREEEEEEE");
                                                            String[] ingrs = recipe_ingr.split("\n");
                                                            System.out.println(recipe_ingr + " THIS IS SAVED INGRRRRRRR");
                                                            ArrayList<String> finalingrs = new ArrayList<>();
                                                            for(String tempingr : ingrs){
                                                                String temp = "";
                                                                System.out.println("TEMPINGRRRRRR     "+tempingr);
                                                                for(int i=0; i<tempingr.length(); i++){
                                                                    char ch = tempingr.charAt(i);
                                                                    if(ch != ':'){
                                                                        temp += ch;
                                                                    }
                                                                    else{
                                                                        break;
                                                                    }
                                                                }
                                                                System.out.println("THID ID THE INGRREEE SAVEDDD    " + temp);
                                                                finalingrs.add(temp);
                                                                getMissingIngredients(finalingrs);

                                                            }

                                                        }
                                                    });
                                                }
                                            });

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
                                                            final DocumentReference house = db.collection("Household").document(household);

                                                            house.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(final DocumentSnapshot documentSnapshot) {

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
                                                                            String recipes_shared_with_friends = documentSnapshot.getString("recipe_shared_with_friends");
                                                                            recipes_shared_with_friends += (recipe_id + " ");
                                                                            house.update("recipe_shared_with_friends", recipes_shared_with_friends);
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
                                ingrs += (ingredients.getItemAtPosition(i).toString() + " ");
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
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this, MapsActivity.class));
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = recipeName.getText().toString().trim();
                if(name.isEmpty()){
                    recipeName.setError("Enter a recipe");
                }
                else{
                    Intent intent = new Intent(Recipes.this, SearchRecipe.class);
                    intent.putExtra("recipe_name", name);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

    }
    public void getMissingIngredients(final ArrayList<String> ingredi) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(db);
        // firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //final String id = firebaseAuth.getCurrentUser().getUid();
        //String household = GetCurrentHouseholdName();

        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                household = documentSnapshot.getString("household");
                db.collection("Household").document(household).collection("Grocery Items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> missingIngr = new ArrayList<>();
                        boolean contain = false;
                        String miss = "";
                        for(String ingr : ingredi){
                            miss = ingr;
                            for(QueryDocumentSnapshot qs : queryDocumentSnapshots){
                                if((ingr.contains(qs.getId()) || qs.getId().contains(ingr)) && qs.getString("status").equals("stock")){
                                    System.out.println("THIS IS THE ingredient     " + ingr);
                                    contain = true;
                                    break;
                                }
                            }
                            if(!contain){
                                missingIngr.add(miss);
                            }
                            else{
                                contain = false;
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


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recipes.this);
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
                Recipes.this,
                android.R.layout.simple_list_item_multiple_choice,list
        );
        ingredients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ingredients.setAdapter(adapter);

        suggestIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        askAFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        ArrayList<String> selected = new ArrayList<>();
                final ArrayList<String> ingToFriend = new ArrayList<>();
                int cntChoice = ingredients.getCount();
                SparseBooleanArray sparseBooleanArray = ingredients.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
//                                selected.add(ingredients.getItemAtPosition(i).toString());
                        ingToFriend.add(ingredients.getItemAtPosition(i).toString());
                        list.remove(ingredients.getItemAtPosition(i).toString());
                    }
                }
                System.out.println("ingtofriends    " +ingToFriend.get(0));
                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String household = documentSnapshot.getString("household");
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
                                        ArrayList<String> select_friends = new ArrayList<>();
                                        SparseBooleanArray sparseBooleanArray = friend_list.getCheckedItemPositions();
                                        for (int i = 0; i < cntChoice; i++) {
                                            if (sparseBooleanArray.get(i)) {
                                                select_friends.add(friend_list.getItemAtPosition(i).toString());
//                                                                          ingrs += friend_list.getItemAtPosition(i).toString() + " ";
                                            }
                                        }

                                        for (int i = 0; i < select_friends.size(); i++) {
                                            for(String ing : ingToFriend) {
                                                RequestQueue requestQueue = Volley.newRequestQueue(Recipes.this);
                                                Notifications n = new Notifications();
                                                try {
                                                    n.sendNotification(select_friends.get(i), household +" would like to borrow " + ing + " ingredient from your household", "bobo", requestQueue);
                                                } catch (InstantiationException | IllegalAccessException e) {
                                                    e.printStackTrace();
                                                }
                                                InAppNotiCollection notiCollection = new InAppNotiCollection(select_friends.get(i), user.getUid(), "Asking for " + ing, household + " has asked for an ingredient", Calendar.getInstance().getTime().toString());
                                                notiCollection.sendInAppNotification(notiCollection);
                                                Toast.makeText(Recipes.this, "Sent message to  " + select_friends.get(i), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        dialog1.dismiss();

                                    }

                                });

                            }
                        });
                    }
                });
                //dialog.dismiss();
                repopulate(ingredients, list);
            }
        });

        addGroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        ArrayList<String> selected = new ArrayList<>();
                ArrayList<String> ingToGrocery = new ArrayList<>();
                int cntChoice = ingredients.getCount();
                SparseBooleanArray sparseBooleanArray = ingredients.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
//                                selected.add(ingredients.getItemAtPosition(i).toString());
                        ingToGrocery.add(ingredients.getItemAtPosition(i).toString());
                    }
                }

                Grocery gr = new Grocery();

                for(String i : ingToGrocery){
                    gr.addItemToGroceryCollection(i, "", "grocery", household);
                    list.remove(i);
                    repopulate(ingredients, list);
                }

                Toast.makeText(Recipes.this, "Ingredients added to grocery!", Toast.LENGTH_LONG).show();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    void getIngrSubstitute(String ingredient){
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        String temp = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/ingredients/substitutes?ingredientName="+ingredient;
        String url = Uri.parse(temp).buildUpon().build().toString();

        System.out.println("INGI START");
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            System.out.println("INGI " + message);
                            ArrayList<String> substitutes = new ArrayList<>();
                            JSONArray ingr_list = response.getJSONArray("substitutes");
                            System.out.println("INGI rushank ");
                            for(int i = 0; i < ingr_list.length(); i++){
//                                JSONObject id =  ingr_list.getJSONObject(i);
//                                String ingredient = id.getString("name")+": "+id.getString("amount") + " " + id.getString("unit") + "\n";
                                substitutes.add(ingr_list.getString(i));
                                System.out.println("INGI rushank "+ substitutes.get(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("INGI SUX");
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
    void repopulate(ListView ingredients, ArrayList<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Recipes.this,
                android.R.layout.simple_list_item_multiple_choice,list
        );
        ingredients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ingredients.setAdapter(adapter);
    }

}






