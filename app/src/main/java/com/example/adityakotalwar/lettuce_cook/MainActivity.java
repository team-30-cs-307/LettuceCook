
package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addItemB;
    private EditText addItemT;
    private ListView listView;
    private Button goToRecipes;
    private Button buttonLogout;
    private Button update;
    private String Household;
    private int flag = 0;
    private String description;
    private EditText addDescription;

    private Button buttonFriends;

    private Button editPwButton;
    private Button editUserNameButton;
    private Button leaveHouseholdButton;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> stock = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            }
        });

        addItemB = (Button) findViewById(R.id.button_add_item);
        update = (Button) findViewById(R.id.update);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        addDescription = (EditText) findViewById(R.id.edit_text_add_description);

        goToRecipes = (Button) findViewById(R.id.go_to_recipes_button);
        buttonFriends = (Button) findViewById(R.id.buttonFriends);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        editPwButton = (Button) findViewById(R.id.editPwButton);
        editUserNameButton = (Button) findViewById(R.id.editUserNameButton);
        leaveHouseholdButton = findViewById(R.id.leaveHouseholdButton);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);

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

        addItemB.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        buttonLogout.setOnClickListener(this);
        editPwButton.setOnClickListener(this);
        editUserNameButton.setOnClickListener(this);

        buttonFriends.setOnClickListener(this);

        listView.setAdapter(arrayAdapter);


        leaveHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

//        goToRecipes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Recipes.class);
//                startActivity(intent);
//            }
//        });


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
        String userid = firebaseAuth.getCurrentUser().getUid();
        Groceries groceries = new Groceries(userid, description, status);
        db.collection("Household").document(HouseholdName).collection("Grocery Items").document(item).set(groceries);

        InAppNotiCollection notiCollection = new InAppNotiCollection(HouseholdName, userid, "Grocery Item Added!", item + " added to Stock!" );
        notiCollection.sendInAppNotification(notiCollection);
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
        switch (v.getId()) {
            case R.id.button_add_item:
                String itemEntered = addItemT.getText().toString();
                String descEntered = addDescription.getText().toString();

                flag = 0;

//                addItemT.setText("");
//                arrayAdapter.add(itemEntered);

                if (!(GroceryItemContains(itemEntered, GetCurrentHouseholdName()))) {

                    addItemToGroceryCollection(itemEntered, descEntered, "stock", GetCurrentHouseholdName());
                    arrayAdapter.clear();
                    repopulate(arrayAdapter, GetCurrentHouseholdName());
                    addItemT.setText("");
                    addDescription.setText("");


                } else {
                    Toast.makeText(this, "You already have this grocery", Toast.LENGTH_SHORT).show();

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
        if(v == goToRecipes){
            finish();
            startActivity(new Intent(getApplicationContext(), Recipes.class));
        }
        if(v == buttonFriends){
            finish();
            startActivity(new Intent(getApplicationContext(), Friends.class));
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
                        ArrayList<String> listMembers = new ArrayList<>();
                        for (Object item : documentSnapshot2.getData().values()) {
                            listMembers.add(item.toString());
                            System.out.println(item.toString());
                        }
                        listMembers.remove(user.getUid());
                        db.collection("Household").document(hName).update("members", "");
                        db.collection("Household").document(hName).update("members", listMembers);

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

    private void editPw(){
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

    private void editUserName(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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
}




