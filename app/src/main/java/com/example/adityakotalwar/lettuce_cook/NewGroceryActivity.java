package com.example.adityakotalwar.lettuce_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewGroceryActivity extends AppCompatActivity {
    private Button Buttondelete;
    private Button Buttonupdate;
    private Button Buttongrocery;
    private Button ButtonStock;
    private Button ButtonRecipes;
    private Button Buttonfriends;
    private ListView GroceryList;
    private ListView MoveToStockList;
    private EditText AdditemText;
    private EditText AddDescText;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    ArrayAdapter<String> newarray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grocery);

        Buttondelete = findViewById(R.id.DeleteGrocery);
        Buttonupdate = findViewById(R.id.updateToStock);
        Buttongrocery = findViewById(R.id.buttonGrocery);
        ButtonStock = findViewById(R.id.buttonStock);
        ButtonRecipes = findViewById(R.id.buttonRecipes);
        Buttonfriends  =findViewById(R.id.buttonFriends);
        GroceryList = findViewById(R.id.GroceryListView);
        MoveToStockList = findViewById(R.id.MoveToStockListView);
        AdditemText = findViewById(R.id.edit_text_add_item);
        AddDescText = findViewById(R.id.edit_text_add_description);

        GroceryList.setAdapter(newarray);
        Buttonupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newarray.add("lmao");
            }
        });


    }
}
