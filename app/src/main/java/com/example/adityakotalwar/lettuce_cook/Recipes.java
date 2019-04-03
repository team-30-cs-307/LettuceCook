package com.example.adityakotalwar.lettuce_cook;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
        // in this class content from api needs to be collected and displayed on the
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes);
        recipeView.setAdapter(arrayAdapter);

        recipes.add("Apple Crumble");
        recipes.add("Brown Butter Apple Crumble");

        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


//        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                String house = documentSnapshot.getString("household");
//                db.collection("Household").document(house).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                        String[] tempRecipes = documentSnapshot.get("recipe_list").toString().split(" ");
//                        final int size = tempRecipes.length;
//                        for(int i=0; i<size; i++){
//
//                            db.collection("SavedRecipe").document(tempRecipes[i]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    recipes.add(documentSnapshot.getString("recipe_title"));
//                                }
//                            });
//                        }
//
//                    }
//                });
//            }
//        });



        chooseIngreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recipes.this,SuggestedRecipe.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(Recipes.this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
            }
        });

        recipeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Recipes.this,RecipeInformation.class);
                startActivity(intent);
            }
        });
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recipes.this,Recipes.class);
                startActivity(intent);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recipes.this,MainActivity.class);
                startActivity(intent);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HashMap<String[], ArrayList<String[]>> t = recipeDetails("479101");
//                Intent intent = new Intent(Grocery.this,MainActivity.class);
//                startActivity(intent);
            }
        });

    }



}



