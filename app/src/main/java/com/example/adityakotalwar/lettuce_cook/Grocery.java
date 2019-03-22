package com.example.adityakotalwar.lettuce_cook;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.support.design.widget.CoordinatorLayout;


import java.util.ArrayList;

import javax.annotation.Nullable;

public class Grocery extends AppCompatActivity {
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
    private Button additem;
    private FirebaseFirestore db;
    private String Household;
    private int flag;
    private int SnackFlag = 0;
    private Groceries grocery;
    private FirebaseAuth firebaseAuth;
    ArrayAdapter<String> GroceryArray;
    ArrayAdapter<String> StockArray;
    ArrayList<String> DeletedItems;

    DrawerLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grocery);


        Buttondelete = findViewById(R.id.DeleteGrocery);
        Buttonupdate = findViewById(R.id.updateToStock);
        Buttongrocery = findViewById(R.id.buttonGrocery);
        ButtonStock = findViewById(R.id.buttonStock);
        ButtonRecipes = findViewById(R.id.go_to_recipes_button);
        Buttonfriends = findViewById(R.id.buttonFriends);
        GroceryList = findViewById(R.id.GroceryListView);
        MoveToStockList = findViewById(R.id.MoveToStockListView);
        AdditemText = findViewById(R.id.edit_text_add_item);
        additem = findViewById(R.id.button_add_item);
        AddDescText = findViewById(R.id.edit_text_add_description);
        GroceryArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        StockArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        GroceryList.setAdapter(GroceryArray);
        MoveToStockList.setAdapter(StockArray);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        GetCurrentHouseholdName();
        FirebaseApp.initializeApp(this);



        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUp.class));
            return;
        }
        db.collection("Users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.get("household").equals("")){
                    finish();
                    startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
                }
            }
        });
        additem.setOnClickListener(Listen);



        coordinatorLayout =  findViewById(R.id.activity_drawer);
        GroceryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SnackFlag = 0;
                //final String household = GetCurrentHouseholdName();
                final String item = GroceryArray.getItem(position);
                final Groceries DeletedGrocery = getGroceryAt(GetCurrentHouseholdName(), item);
                //deleteGrocery(GetCurrentHouseholdName(), item);
                GroceryArray.remove(item);
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SnackFlag = 1;
                                //addItemToGroceryCollection(item, DeletedGrocery.getDescription(), DeletedGrocery.getStatus(), GetCurrentHouseholdName());
                                Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Item restored", Snackbar.LENGTH_SHORT);
                                snackbar1.show();

                                GroceryArray.add(item);




                            }
                        });



                  snackbar.show();
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //see Snackbar.Callback docs for event details
                        if(SnackFlag == 0){

                            deleteGrocery(GetCurrentHouseholdName(), item);
                        }

                    }
                });
//                if(SnackFlag == 0){
//                    deleteGrocery(GetCurrentHouseholdName(), item);
//
//                }
//                else{
//                    GroceryArray.add(item);
//
//                }



                //deleteGrocery(GetCurrentHouseholdName(), GroceryArray.getItem(position));



                return false;
            }
        });




    }

    public Groceries getGroceryAt(String household, String item){

        DocumentReference documentReference = db.collection("Household").document(household).collection("Grocery Items").document(item);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                grocery = documentSnapshot.toObject(Groceries.class);
            }
        });
        return grocery;

    }


    private View.OnClickListener Listen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_add_item:
                    flag = 0;
                    String ItemEntered = AdditemText.getText().toString();
                    if (ItemEntered.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter an item", Toast.LENGTH_SHORT).show();
                        break;

                    }
                    realtime(GetCurrentHouseholdName());
                    //Toast.makeText(getApplicationContext(), GetCurrentHouseholdName(), Toast.LENGTH_SHORT).show();
                    //GetCurrentHouseholdName();
                    String Description = AddDescText.getText().toString();

                    if(!GroceryItemContains(ItemEntered, GetCurrentHouseholdName())){

                        addItemToGroceryCollection(ItemEntered, Description, "grocery", GetCurrentHouseholdName());
                        AdditemText.setText("");
                        AddDescText.setText("");
                        Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(getApplicationContext(), "This item is already in the grocery list", Toast.LENGTH_SHORT).show();
                    }


                    break;
            }
        }
    };


    public String GetCurrentHouseholdName() {
        final DocumentReference docrefUser;
        final String id = firebaseAuth.getCurrentUser().getUid();
        docrefUser = db.collection("Users").document(id);
        //String house = null;
        docrefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //user.getUsername();
                Household = documentSnapshot.getString("household");
                //Household = "hi";
                //System.out.println("\n\n\n\n\n\n\n\n" + Household+ "\n\n\n\n\n\n");
                //house = documentSnapshot.getString("household");

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

    public void realtime(final String householdName) {
        db.collection("Household").document(householdName).collection("Grocery Items").whereEqualTo("status", "grocery")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //System.out.println("going in here");
                        GroceryArray.clear();
                        //repopulate(arrayAdapter, householdName);
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            GroceryArray.add(doc.getId());
                        }
                    }
                });


    }

    public void addItemToGroceryCollection(String item, String description, String status, String HouseholdName){
        String userid = firebaseAuth.getCurrentUser().getUid();
        Groceries groceries = new Groceries(userid, description, status);
        db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

        InAppNotiCollection notiCollection = new InAppNotiCollection(HouseholdName, userid, "Grocery Item Added to Grocery list!", item + " added to Grocery!" );
        notiCollection.sendInAppNotification(notiCollection);
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

}
