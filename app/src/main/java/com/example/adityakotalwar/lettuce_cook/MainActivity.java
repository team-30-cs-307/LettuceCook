package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
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
import android.widget.TextView;
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
    private String description;
    private EditText addDescription;



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
        addDescription = (EditText) findViewById(R.id.edit_text_add_description);


        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        //getButton = (Button) findViewById(R.id.getButton);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);
        listView.setAdapter(arrayAdapter);

        addItemB.setOnClickListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),arrayAdapter.getItem(i), Toast.LENGTH_LONG).show();
                deleteGrocery(GetCurrentHouseholdName(), arrayAdapter.getItem(i));
                arrayAdapter.clear();
                repopulate(arrayAdapter, GetCurrentHouseholdName());

                return false;
            }
        });
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
                //groceries(arrayAdapter, id, "Hardcoded ID");
                arrayAdapter.clear();
                repopulate(arrayAdapter, GetCurrentHouseholdName());

            }
        });




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

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_add_item:
                if(addItemT.getText().toString().equals(null) || addItemT.getText().toString().equals("")){

                    Toast.makeText(this, "Please enter a grocery name", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    String itemEntered = addItemT.getText().toString();
                    String descEntered = addDescription.getText().toString();
                    //Toast.makeText(this, "your household is " + GetCurrentHouseholdName(), Toast.LENGTH_SHORT).show();
                    flag = 0;
                    if (!(GroceryItemContains(itemEntered, GetCurrentHouseholdName()))) {


                        //IMPORTANT DO NOT DELETE


                        /*description = "";
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_add_comment, null);

                        final EditText Desc = findViewById(R.id.Description);
                        final Button Add_desc = findViewById(R.id.Add_description);

                        //final EditText EmailReset = (EditText) mView.findViewById(R.id.EmailReset);
                        //Button ButtonForgotPw = (Button) mView.findViewById(R.id.ButtonForgotPw);

                        mBuilder.setView(mView);
                        // Pops the dialog on the screen
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        Add_desc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                description = Desc.getText().toString();



                            }
                        });*/



                        addItemToGroceryCollection(itemEntered, descEntered, "stock", GetCurrentHouseholdName());
                        arrayAdapter.clear();
                        repopulate(arrayAdapter, GetCurrentHouseholdName());
                        addItemT.setText("");
                        addDescription.setText("");


                    } else {
                        Toast.makeText(this, "You already have this grocery", Toast.LENGTH_SHORT).show();

                    }
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

    public void deleteGrocery(String Household /*Name of the household the user is in*/, String item /*Item to be deleted*/){
        db.collection("Household").document(Household).collection("Grocery Items").document(item)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Grocery deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();

                    }
                });

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
        //String house = null;
        docrefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //user.getUsername();


                Household = documentSnapshot.getString("household");




            }
        });
        docrefUser.get().addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                Household = "bye";
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

    }

    //Add grocery item to the database
    public void addItemToGroceryCollection(String item, String description, String status, String HouseholdName){
        String userid = firebaseAuth.getCurrentUser().getUid();
        Groceries groceries = new Groceries(userid, description, status);
        db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

    }

    public void repopulate(final ArrayAdapter ArrayAdapter, String HouseholdName){

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
}