package com.example.adityakotalwar.lettuce_cook;

import android.app.ActivityOptions;
import android.app.AlertDialog;
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

    private Button add_item_button;
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button recipesButton;
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
            startActivity(new Intent(getApplicationContext(),   SignUp.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

        addItemB = findViewById(R.id.button_add_item);
//        addItemT = findViewById(R.id.edit_text_add_item);
        listView = findViewById(R.id.my_list_view2);

        buttonLogout = findViewById(R.id.buttonLogout);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);
        listView.setAdapter(arrayAdapter);

        addItemB.setOnClickListener(this);
//        listView.setOnItemClickListener(this);
        buttonLogout.setOnClickListener(this);

        findViewById(R.id.go_to_recipes_button).setOnClickListener(this);
        findViewById(R.id.buttonFriends).setOnClickListener(this);
        findViewById(R.id.buttonGrocery).setOnClickListener(this);
    }

    private void create_add_item_dialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        final EditText ItemName = (EditText) mView.findViewById(R.id.ItemName);
        final EditText ItemDescription = (EditText) mView.findViewById(R.id.ItemDescription);
        Button ButtonAddItem = (Button) mView.findViewById(R.id.ButtonAddItem);
        Button ButtonCancelItem = (Button) mView.findViewById(R.id.ButtonCancelItem);

        mBuilder.setView(mView);
        // Pops the dialog on the screen
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        ButtonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempName = ItemName.getText().toString();
                String tempDesc = ItemName.getText().toString();
                if(!tempName.isEmpty()){
                    add_item(tempName, tempDesc, dialog);
                }
                else{
                    ItemName.setError("Enter an email!");
                }
            }
        });
        ButtonCancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void add_item(final String ItemName, final String ItemDesc, final AlertDialog alertDialog){
        arrayAdapter.add(ItemName);
        alertDialog.dismiss();
        Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_add_item:
                create_add_item_dialog();
                break;
            case R.id.go_to_recipes_button:
                startActivity(new Intent(MainActivity.this,Recipes.class));
                break;
            case R.id.buttonGrocery:
                startActivity(new Intent(MainActivity.this,Grocery.class));
                break;
            case R.id.buttonLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, SignIn.class));
                break;
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
