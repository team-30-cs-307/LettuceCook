package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button goToRecipes;
    private Button buttonLogout;
    private Button update;
    private Button getButton;
    private FirebaseFirestore db;
    private UserCollection user;
    private String Household;
    private int flag = 0;



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
        db = FirebaseFirestore.getInstance();
        addItemB = (Button) findViewById(R.id.button_add_item);
        update = (Button) findViewById(R.id.update);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        goToRecipes = (Button) findViewById(R.id.go_to_recipes_button);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        getButton = (Button) findViewById(R.id.getButton);

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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = firebaseAuth.getCurrentUser().getUid();
                groceries(arrayAdapter, id, "Hardcoded ID");
            }
        });



        //--------------------------------------------------------------------------------------------------------------
        //add a real time update listener to the main activity




        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------
    //Updates the grocery list anytime the page is opened
        db.collection("Household").document("Household").collection("Grocery Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    /*if (ds.getId().equals("groceries")) {
                        String itemEntered;
                        itemEntered = ds.getString("groceries");
                        String[] arrOfgroceries = itemEntered.split(",");
                        for (int i = 0; i < arrOfgroceries.length; i++) {
                            if (contains(arrayAdapter, arrOfgroceries[i])) {

                            } else {
                                arrayAdapter.add(arrOfgroceries[i]);

                            }
                        }
                        break;

                    }*/
                    String GroceryName = ds.getId();
                    if(!contains(arrayAdapter, GroceryName)){
                        arrayAdapter.add(GroceryName);

                    }


                }

            }
        });



        //-----------------------------------------------
        //-----------------------------------------------------------------------------------------------------------
        //add a real time update listener to the main activity


        final DocumentReference docrefUser;
        final String id = firebaseAuth.getCurrentUser().getUid();
        docrefUser = db.collection("Users").document(id);
        docrefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //user.getUsername();
                Household = documentSnapshot.getString("household");

            }
        });
        //Household = user.getHousehold();
        //Toast.makeText(this, "your name is " + Household, Toast.LENGTH_SHORT).show();
        //Log("your name is " + Household);
        /*DocumentReference docrefHouse = db.collection("Household").document(Household);
        docrefHouse.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @OverrideonItemClick
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists() ){
                    Toast.makeText(getApplicationContext(), "maybe true", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();

                }
            }
        });*/




        //--------------------------------------------------------------------------------------------------------------




//update the grocery list whenever the update button is pressed
        /*getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Household").document("Household").collection("Grocery Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots ){
                            /*if(ds.getId().equals("groceries")){
                                String itemEntered;
                                itemEntered = ds.getString("groceries");
                                String [] arrOfgroceries = itemEntered.split(",");
                                for (int i = 0; i <arrOfgroceries.length ; i++) {
                                    if(contains(arrayAdapter, arrOfgroceries[i])){

                                    }else{
                                        arrayAdapter.add(arrOfgroceries[i]);

                                    }
                                }
                                break;

                            }
                            String GroceryName = ds.getId();
                            if(!contains(arrayAdapter, GroceryName)){
                                arrayAdapter.add(GroceryName);

                            }


                        }

                    }
                });
            }
        });
*/


    }



    //checks if arrayadapter contains the required string
    public boolean contains(ArrayAdapter arrayAdapter, String string){

        for (int i = 0; i < arrayAdapter.getCount(); i++) {

                if (arrayAdapter.getItem(i).equals(string)) {
                    return true;

                }

        }
        return false;

    }

    //Function to check if grocery items collection contains the grocery

    public boolean GroceryItemContains(final String groceryName, String HouseholdName){

        //First collection and second collection will always be the same
        db.collection("Household").document(HouseholdName).collection("Grocery Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    if(ds.getId().equals(groceryName)){
                        flag = 1;
                        break;
                    }

                }

            }
        });
        /*db.collection("Household").document(HouseholdName).collection("Grocery Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //List<String> list = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){

                        //arrayAdapter.add(document.getId());
                        if(groceryName.equals(document.getId())){
                            flag = 1;
                        }
                    }
                }
            }
        });*/

        if(flag == 1){
            return true;
        }
        return false;

    }

    //Gets the household the user is in
    public String GetCurrentHouseholdName(){
        final DocumentReference docrefUser;
        final String id = firebaseAuth.getCurrentUser().getUid();
        docrefUser = db.collection("Users").document(id);
        docrefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //user.getUsername();
                Household = documentSnapshot.getString("household");


            }
        });

        return Household;

    }
//adds the grocery item created in the function to the database
    public void groceries(ArrayAdapter arrayAdapter, String userid, String Household){
       /* Map<String, Object> grocery = new HashMap<>();
        grocery.put("userId", userid);
        grocery.put("household", Household);
        grocery.put("groceries", arrayAdapter);*/
        String Grocerylist = "";
        for (int i = 0; i < arrayAdapter.getCount() ; i++) {
            /*if (i == arrayAdapter.getCount() - 1) {
                Grocerylist += arrayAdapter.getItem(i);
            } else {
                Grocerylist += arrayAdapter.getItem(i) + ",";
            }*/

            if(!(arrayAdapter.getItem(i) == null || arrayAdapter.getItem(i).toString().equals(""))){
                 Groceries groceries = new Groceries(userid, "Describe", "stock");
                 db.collection("Household").document("Household").collection("Grocery Items").document(arrayAdapter.getItem(i).toString()).set(groceries);
        }
        }
        /*db.collection("Grocery")
                .add(groceries)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    //Add grocery item to the database
    public void addItemToGroceryCollection(String item, String description, String status, String HouseholdName){
        String userid = firebaseAuth.getCurrentUser().getUid();
        Groceries groceries = new Groceries(userid, description, status);
        db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

    }

    public void repopulate(final ArrayAdapter ArrayAdapter, String HouseholdName){

        /*db.collection("Household").document(HouseholdName).collection("Grocery Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {

                    ArrayAdapter.add(ds.getId());

                }

            }
        });*/
        db.collection("Household").document(HouseholdName).collection("Grocery Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //List<String> list = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){

                        arrayAdapter.add(document.getId());
                    }
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_add_item:
                String itemEntered = addItemT.getText().toString();
                //Toast.makeText(this, "your household is " + GetCurrentHouseholdName(), Toast.LENGTH_SHORT).show();
                    flag = 0;
                if(!(GroceryItemContains(itemEntered, GetCurrentHouseholdName()))){
                    addItemToGroceryCollection(itemEntered, "description", "stock", GetCurrentHouseholdName());
                    arrayAdapter.clear();
                    repopulate(arrayAdapter, GetCurrentHouseholdName());
                    addItemT.setText("");


                }
                else{
                    Toast.makeText(this, "You already have this grocery", Toast.LENGTH_SHORT).show();

                }


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
