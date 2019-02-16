package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button goToRecipes;
    private Button buttonLogout;
    private  Button friendsButton;
    private Button groceryButton;


    ArrayList<String> stock = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),   SignUp.class));
        }

        addItemB = findViewById(R.id.button_add_item);
        addItemT = findViewById(R.id.edit_text_add_item);
        listView = findViewById(R.id.my_list_view2);
        goToRecipes = findViewById(R.id.go_to_recipes_button);
        friendsButton = findViewById(R.id.buttonFriends);
        groceryButton = findViewById(R.id.buttonGrocery);

        buttonLogout = findViewById(R.id.buttonLogout);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);
        listView.setAdapter(arrayAdapter);

        addItemB.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        buttonLogout.setOnClickListener(this);

        goToRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Recipes.class);
                startActivity(intent);
            }
        });
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Friends.class);
                startActivity(intent);
            }
        });

        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Grocery.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_add_item:
                String itemEntered = addItemT.getText().toString();
                arrayAdapter.add(itemEntered);

                Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
                break;
        }
        if(v == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, SignIn.class));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stock.remove(position);
        arrayAdapter.notifyDataSetChanged();
        //   FileHelper.writeData(items, this);
        Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
    }
}
