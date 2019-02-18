package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button goToRecipes;
    private Button buttonLogout;
    private Button update;
    ArrayList<String> stock = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    private FirebaseFirestore db;

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

        addItemB = (Button) findViewById(R.id.button_add_item);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        goToRecipes = (Button) findViewById(R.id.go_to_recipes_button);
        update = (Button) findViewById(R.id.update);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);

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
        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "This is working", Toast.LENGTH_LONG).show();

                //CollectionReference dbUser = db.collection("Grocery");
                String id = firebaseAuth.getCurrentUser().getUid();
                Grocery grocery = new Grocery(id, arrayAdapter);
                // Gets the userId of the person logged in.
                Map<String, Object> data = new HashMap<>();
                data.put("userId", grocery.getUserId());
                data.put("groceries", id);
                db.collection("Grocery")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_LONG).show();
                            }
                        });
                finish();

                //db.collection("Grocery").document("first-one").set(grocery);

                // store the user details in a userCollection class
                // stores the user information if a failure pops a toast

            }
        });



    }
    //Grocery class so that I can create an object for groceries
    public class Grocery{
        private String userId;
        private String Household;
        ArrayAdapter arrayAdapter1;
       public Grocery(){

       }
       //remove this constructor after household part is done.
       public Grocery(String userId, ArrayAdapter arrayAdapter1){
           this.userId = userId;
           this.arrayAdapter1 = arrayAdapter1;

       }
       public Grocery(String userId, String Household, ArrayAdapter arrayAdapter1){
           this.userId = userId;
           this.Household = Household;
           this.arrayAdapter1 = arrayAdapter1;
       }
       public String getUserId() {
           return userId;

       }
       public String getHousehold(){
           return Household;

       }
       public ArrayAdapter getArrayAdapter1(){
           return arrayAdapter1;

       }
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
