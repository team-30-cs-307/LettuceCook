
package com.example.adityakotalwar.lettuce_cook;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button buttonLogout;
    private Button update;
    private String Household;
    private int flag = 0;
    private String description;
    private EditText addDescription;

    private Button buttonFriends;
    private Button buttonGroceries;
    private Button buttonStock;
    private Button buttonRecipes;

    private Button editPwButton;
    private Button editUserNameButton;
    private Button leaveHouseholdButton;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> stock = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    String userToBeAdded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        dl = (DrawerLayout)findViewById(R.id.activity_drawer);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dl.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), Grocery.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), Recipes.class));
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });



        final TextView userNameDisp = findViewById(R.id.UserNameTextView);
        final TextView householdDisp = findViewById(R.id.HouseholdTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

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
                userNameDisp.setText("Hello " + documentSnapshot.getString("username") + " !");
                householdDisp.setText("House "+documentSnapshot.getString("household"));
                realtime(documentSnapshot.getString("household"));
            }
        });

        addItemB = (Button) findViewById(R.id.button_add_item);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        addDescription = (EditText) findViewById(R.id.edit_text_add_description);

        buttonRecipes = (Button) findViewById(R.id.buttonRecipes);
        buttonFriends = (Button) findViewById(R.id.buttonFriends);
        buttonGroceries = (Button) findViewById(R.id.buttonGrocery);
        buttonStock = findViewById(R.id.buttonStock);

        buttonRecipes.setOnClickListener(this);
        buttonFriends.setOnClickListener(this);
        buttonGroceries.setOnClickListener(this);
        buttonStock.setTextColor(Color.parseColor("#5D993D"));

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                //Toast.makeText(getApplicationContext(),arrayAdapter.getItem(i), Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("What should happen to this item");
                builder.setMessage("Message content of Title");
                builder.setCancelable(false);

                builder.setPositiveButton("Remove from stock", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        deleteGrocery(GetCurrentHouseholdName(), arrayAdapter.getItem(position));
                        //               Toast.makeText(MainActivity.this,"REMOVE FROM STOCK", Toast.LENGTH_LONG).show();
//                        arrayAdapter.clear();
//                        repopulate(arrayAdapter, GetCurrentHouseholdName());
                    }
                });

                builder.setNegativeButton("Remove from stock + Add to grocery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        Toast.makeText(MainActivity.this,"GROCERY LIST", Toast.LENGTH_LONG).show();

//                        deleteGrocery(GetCurrentHouseholdName(), arrayAdapter.getItem(j));
//                        arrayAdapter.clear();
//                        repopulate(arrayAdapter, GetCurrentHouseholdName());
                        String item = arrayAdapter.getItem(position);
                        db.collection("Household").document(GetCurrentHouseholdName()).collection("Grocery Items").document(item).update("status", "grocery");


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });

        addItemB.setOnClickListener(this);

        listView.setAdapter(arrayAdapter);

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
        final Context obj = this;

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.edit_name:
                        editUserName();
                        return true;
                    case R.id.edit_pw:
                        editPw();
                        return true;
                    case R.id.leave_house:
                        AlertDialog.Builder logout_confir = new AlertDialog.Builder(MainActivity.this);
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
                        AlertDialog.Builder logout_confir1 = new AlertDialog.Builder(MainActivity.this);
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
                    case R.id.add_member:
                        AlertDialog.Builder builder = new AlertDialog.Builder(obj);
                        builder.setTitle("Enter username of member to be invited");

// Set up the input
                        final EditText input = new EditText(obj);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        // input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);

// Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userToBeAdded = input.getText().toString();
                                System.out.println("getting username " +userToBeAdded);
                                addMember(userToBeAdded);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    default:
                        return true;
                }
            }
        });
    }



    public void deleteGrocery(final String Household /*Name of the household the user is in*/, final String  item /*Item to be deleted*/){
        db.collection("Household").document(Household).collection("Grocery Items").document(item)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        InAppNotiCollection notiCollection = new InAppNotiCollection(Household, firebaseAuth.getCurrentUser().getUid(),
                                "Grocery Item Deleted!", item + " added to Stock!", Calendar.getInstance().getTime().toString() );
                        notiCollection.sendInAppNotification(notiCollection);
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

    //adds the grocery item created in the function to the database
    public void groceries(ArrayAdapter arrayAdapter, String userid, String Household){

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
        if(item.equals("")){
            return;
        }
        String userid = firebaseAuth.getCurrentUser().getUid();
        Groceries groceries = new Groceries(userid, description, status);
        db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

        InAppNotiCollection notiCollection = new InAppNotiCollection(HouseholdName, userid, "Grocery Item Added!", item + " added to Stock!"
        ,Calendar.getInstance().getTime().toString());
        notiCollection.sendInAppNotification(notiCollection);
    }


    public void realtime(final String householdName){
        db.collection("Household").document(householdName).collection("Grocery Items").whereEqualTo("status", "stock")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //System.out.println("going in here");
                        arrayAdapter.clear();
                        //repopulate(arrayAdapter, householdName);
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                           arrayAdapter.add(doc.getId());
                        }
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_item:
                String itemEntered = addItemT.getText().toString();
                String descEntered = addDescription.getText().toString();

                flag = 0;
                realtime(GetCurrentHouseholdName());
                if(itemEntered.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter an item", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (!(GroceryItemContains(itemEntered, GetCurrentHouseholdName()))) {

                    addItemToGroceryCollection(itemEntered, descEntered, "stock", GetCurrentHouseholdName());
                    arrayAdapter.clear();
                    //repopulate(arrayAdapter, GetCurrentHouseholdName());
                    addItemT.setText("");
                    addDescription.setText("");
                } else {
                    Toast.makeText(this, "You already have this grocery", Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
                break;
        }
        if (v == buttonLogout) {
            buttonLogout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder logout_confir = new AlertDialog.Builder(MainActivity.this);
                            logout_confir.setMessage("Are you sure you want to logout")
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
                            AlertDialog alertDialog = logout_confir.create();
                            alertDialog.show();

                        }
                    }
            );
        }
        if(v == editPwButton){
            editPw();
        }
        if(v == editUserNameButton){
            editUserName();
        }
        if(v == buttonRecipes){
            finish();
            startActivity(new Intent(getApplicationContext(), Recipes.class));
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
        if(v == buttonFriends){
            finish();
            startActivity(new Intent(getApplicationContext(), Friends.class));
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
        if(v == buttonGroceries) {
            finish();
            Intent myIntent = new Intent(MainActivity.this, Grocery.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stock.remove(position);
        arrayAdapter.notifyDataSetChanged();
        //   FileHelper.writeData(items, this);
        Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
    }

    public void leaveHousehold() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // store the user details in a userCollection class
        System.out.println(user.getUid());
        final DocumentReference dr = db.collection("Users").document(user.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("household");
                db.collection("Users").document(user.getUid()).update("household", "");

                final DocumentReference dr2 = db.collection("Household").document(hName);
                dr2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot2) {

                        String[] listMembers = documentSnapshot2.getString("members").split(" ");
                        String newMembers = remove(listMembers, user.getUid());
                        db.collection("Household").document(hName).update("members", newMembers);
                        /*Notification chunk */

                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        Notifications n = new Notifications();
                        try {
                            String message = user.getEmail()+" has left "+ hName + "!";
                            n.sendNotification("A member has left the household", message, hName, requestQueue);
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        }
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(hName);

                        /* Completed */

                        startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
                    }
                });
                // household.addMember(user.getUid());
                //db.collection("Users").document(user.getUid()).update("invited", "");
                //   db.collection("Household").document(hName).update("members", household.getMembers());
            }

        });
    }

    private String remove(String [] mems, String user){
        for(String a : mems)
            System.out.println(a);
        String newMems = "";
        for(int i=0; i< mems.length; i++){
            if(!mems[i].equalsIgnoreCase(user)){
                if(newMems.length()==0){
                    newMems = mems[i];
                }
                else {
                    newMems += " " + mems[i];
                }
            }
        }
        return newMems;
    }


    void editPw(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_edit_pw, null);

//                final EditText emailCurrent = (EditText) mView.findViewById(R.id.EmailCurrent);
        final EditText PwCurrent = (EditText) mView.findViewById(R.id.PwCurrent);
        final EditText PwReset = (EditText) mView.findViewById(R.id.PwReset);
        final EditText ComfirmPwReset = (EditText) mView.findViewById(R.id.ConfirmPwReset);
        final Button ButtonEditPw = (Button) mView.findViewById(R.id.ButtonChangeConfirm);

        mBuilder.setView(mView);
        // Pops the dialog on the screen
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        ComfirmPwReset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pw = PwReset.getText().toString();
                String cpw = ComfirmPwReset.getText().toString();
                if(pw.length() > 0 && cpw.length() > 0){
                    if(!cpw.equals(pw)){
                        PwReset.setError("Does not match Password Entered!");
                        PwReset.setText("");
                        ComfirmPwReset.setText("");
                        return;
                    }
                }
            }
        });

        ButtonEditPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currPass = PwCurrent.getText().toString();
                final String newPass = PwReset.getText().toString();
                String confirmNewPass = ComfirmPwReset.getText().toString();

                if(currPass.isEmpty() | newPass.isEmpty() | confirmNewPass.isEmpty()){
                    if(currPass.isEmpty()){
                        PwCurrent.setError("Password Dummy!");
                    }
                    if(newPass.isEmpty()){
                        PwReset.setError("New Password dummy");
                    }
                    if(confirmNewPass.isEmpty()){
                        ComfirmPwReset.setError("New Confirm Password dummy");
                    }
                    return;
                }

                String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, currPass);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Not happening",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else{
                                    Toast.makeText(getApplicationContext(),"FUXXX",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    void editUserName(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getApplicationContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_edit_username, null);

        final EditText PwCurrent = (EditText) mView.findViewById(R.id.PwCurrent);
        final EditText NewUserName = (EditText) mView.findViewById(R.id.NewUserName);
        final Button ButtonEditUserName = (Button) mView.findViewById(R.id.ButtonUserNameChangeConfirm);

        mBuilder.setView(mView);
        // Pops the dialog on the screen
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        ButtonEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, PwCurrent.getText().toString());

                user.reauthenticate(credential).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    db.collection("Users").document(user.getUid()).update("username", NewUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                dialog.dismiss();
                                                Toast.makeText(MainActivity.this, "YUP", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "NOPE", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    PwCurrent.setError("Incorrect Password");
                                    PwCurrent.setText("");
                                }
                            }
                        });

            }
        });

    }

    public void addMember(final String member){


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final String householdName = documentSnapshot.getString("household");
                db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        boolean userPresent = false;
                        for(DocumentSnapshot ds : queryDocumentSnapshots ){
                            userPresent = false;
                            System.out.println("GETTING HERE " +ds.getString("username")+ " " +member);
                            if(ds.getString("username").equals(member)){
                                System.out.println("GETTING HERE 222222222");
                                String invites = ds.getString("invited");
                                invites = householdName;
                               // System.out.println(invites);
                                db.collection("Users").document(ds.getId()).update("invited", invites);
                              //   db.collection("Users").document(ds.getId()).update("invited", "");
                                Toast.makeText(getApplicationContext(), "User invited!", Toast.LENGTH_LONG).show();
                                userPresent = true;

                        /*Sends notification if a household invites a particular user*/
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                Notifications n = new Notifications();
                                try {
                                    n.sendNotification("Invitation",householdName+" has invited you to their household!", ds.getId(), requestQueue);
                                } catch (InstantiationException e1) {
                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }
                                break;
                               // return;
                            }
                        }
                        // if(!userPresent){

                        ///}
                    }
                });
            }
        });


        //Extracting participants ArrayList from each document
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}




