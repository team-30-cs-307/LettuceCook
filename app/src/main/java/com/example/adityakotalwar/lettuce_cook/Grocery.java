package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.Calendar;

import javax.annotation.Nullable;

public class Grocery extends MainActivity {
    private Button Buttondelete;
    private Button Buttonupdate;

    private Button ButtonGrocery;
    private Button ButtonStock;
    private Button ButtonRecipes;
    private Button ButtonFriends;

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
    ArrayList<String> currentIngredients;

    private DrawerLayout coordinatorLayout;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grocery);
        //setContentView(R.layout.activity_suggrecipe);

        Buttondelete = findViewById(R.id.DeleteGrocery);
        Buttonupdate = findViewById(R.id.updateToStock);

        ButtonGrocery = findViewById(R.id.buttonGrocery);
        ButtonStock = findViewById(R.id.buttonStock);
        ButtonRecipes = findViewById(R.id.buttonRecipes);
        ButtonFriends = findViewById(R.id.buttonFriends);
        ButtonGrocery.setTextColor(Color.parseColor("#5D993D"));

        GroceryList = findViewById(R.id.GroceryListView);
        MoveToStockList = findViewById(R.id.MoveToStockListView);
        AdditemText = findViewById(R.id.edit_text_add_item);
        additem = findViewById(R.id.button_add_item);
        AddDescText = findViewById(R.id.edit_text_add_description);
        GroceryArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        StockArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        GroceryList.setAdapter(GroceryArray);
        MoveToStockList.setAdapter(StockArray);

        GroceryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        GetCurrentHouseholdName();
        FirebaseApp.initializeApp(this);

        additem.setOnClickListener(Listen);
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUp.class));
            return;
        }
        db.collection("Users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //additem.performClick();
                if(documentSnapshot.getString("household").equals("")){
                    finish();
                    startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
                }
                realtime(documentSnapshot.getString("household"));
            }
        });

        ButtonStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        ButtonFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Friends.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        ButtonRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Recipes.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        coordinatorLayout =  findViewById(R.id.activity_drawer);
//        additem.performClick();

        coordinatorLayout.setOnTouchListener(new OnSwipeTouchListener(Grocery.this) {
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        t = new ActionBarDrawerToggle(this, coordinatorLayout,R.string.Open, R.string.Close);

        coordinatorLayout.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.home:
                        return true;
                    case R.id.edit_name:
                        editUserName();
                        return true;
                    case R.id.edit_pw:
                        editPw();
                        return true;
                    case R.id.leave_house:
                        AlertDialog.Builder logout_confir = new AlertDialog.Builder(getApplicationContext());
                        logout_confir.setMessage("Are you sure you want to leave the household")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        leaveHousehold();
                                        finish();
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
                        return true;
                    case R.id.logout:
                        AlertDialog.Builder logout_confir1 = new AlertDialog.Builder(getApplicationContext());
                        logout_confir1.setMessage("Are you sure you want to logout")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseAuth.signOut();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), SignIn.class));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        AlertDialog alertDialog1 = logout_confir1.create();
                        alertDialog1.show();
                        return true;
                    default:
                        return true;
                }


            }
        });


        GroceryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder logout_confir = new AlertDialog.Builder(Grocery.this);
                logout_confir.setMessage("Do you want to add this grocery to stock or delete the item?")
                        .setCancelable(false)

                        .setPositiveButton("Add to stock", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String item = GroceryArray.getItem(position);
                                deleteGrocery(GetCurrentHouseholdName(), item);
                                StockArray.add(item);
                            }
                        })
                        .setNeutralButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .setNegativeButton("Delete from everywhere", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SnackFlag = 0;
                        //final String household = GetCurrentHouseholdName();
                        final String item = GroceryArray.getItem(position);
                        final Groceries DeletedGrocery = getGroceryAt(GetCurrentHouseholdName(), item);
                        //deleteGrocery(GetCurrentHouseholdName(), item);
                        GroceryArray.remove(item);
                        final Snackbar snackbar = Snackbar.make(coordinatorLayout, item + " deleted", Snackbar.LENGTH_LONG)
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
                                if (SnackFlag == 0) {

                                    deleteGrocery(GetCurrentHouseholdName(), item);
                                }

                            }
                        });

                    }
                });
                AlertDialog alertDialog = logout_confir.create();
                alertDialog.show();
                return false;

            }
        });

        MoveToStockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = StockArray.getItem(position);
                StockArray.remove(item);
                addItemToGroceryCollection(item, "", "grocery", GetCurrentHouseholdName());
            }
        });

        Buttonupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i< StockArray.getCount(); i++){

                    addItemToGroceryCollection(StockArray.getItem(i), "", "stock", GetCurrentHouseholdName());

                }
                StockArray.clear();

            }
        });

        //additem.performClick();
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

    public boolean ArrayadapterContains(String item, ArrayAdapter arrayAdapter){

        for(int i = 0; i < arrayAdapter.getCount(); i++){
            if(arrayAdapter.getItem(i).equals(item)){
                return true;

            }

        }
        return false;
    }


    private View.OnClickListener Listen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_add_item:
                    flag = 0;
                    realtime(GetCurrentHouseholdName());
                    String ItemEntered = AdditemText.getText().toString();
                    if (ItemEntered.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter an item", Toast.LENGTH_SHORT).show();
                        break;
                    }

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
        firebaseAuth = FirebaseAuth.getInstance();
        final String id = firebaseAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
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
        if(!GroceryItemContains(item, HouseholdName)) {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();//added by alisha
            db = FirebaseFirestore.getInstance();//added by alisha
            //String userid = firebaseAuth.getCurrentUser().getUid();
            Groceries groceries = new Groceries(userid, description, status);
            db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

            InAppNotiCollection notiCollection = new InAppNotiCollection(HouseholdName, userid, "Grocery Item Added to Grocery list!", item + " added to Grocery!", Calendar.getInstance().getTime().toString());
            notiCollection.sendInAppNotification(notiCollection);
        }
    }

    public boolean GroceryItemContains(final String groceryName, String HouseholdName){
        db = FirebaseFirestore.getInstance();//added by alisha
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
