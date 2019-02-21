package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
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
    private Button getButton;
    String household;

    private Button editPwButton;
    private Button editUserNameButton;

    private FirebaseFirestore db;
    private Button leaveHouseholdButton;


    ArrayList<String> stock = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUp.class));
            return;
        }


        final FirebaseUser user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

//        final DocumentReference dr = db.collection("Users").document(user.getUid());
//        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                household = documentSnapshot.get("household").toString();
//                Toast.makeText(getApplicationContext(),household, Toast.LENGTH_LONG).show();
//            }
//        });
//        if(household.equals("")){
//            finish();
//            startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
//        }

        addItemB = (Button) findViewById(R.id.button_add_item);
        update = (Button) findViewById(R.id.update);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        goToRecipes = (Button) findViewById(R.id.go_to_recipes_button);
//        editCredientialsButton = findViewById(R.id.editCredientials);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        editPwButton = (Button) findViewById(R.id.editPwButton);
        editUserNameButton = (Button) findViewById(R.id.editUserNameButton);
        leaveHouseholdButton = findViewById(R.id.leaveHouseholdButton);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);
        listView.setAdapter(arrayAdapter);

        addItemB.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        buttonLogout.setOnClickListener(this);
        editPwButton.setOnClickListener(this);
        editUserNameButton.setOnClickListener(this);

        leaveHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveHousehold();
            }
        });

        goToRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Recipes.class);
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


        }


    public void groceries(ArrayAdapter arrayAdapter, String userid, String Household) {
       /* Map<String, Object> grocery = new HashMap<>();
        grocery.put("userId", userid);
        grocery.put("household", Household);
        grocery.put("groceries", arrayAdapter);*/
        String Grocerylist = "";
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            if (i == arrayAdapter.getCount() - 1) {
                Grocerylist += arrayAdapter.getItem(i);
            } else {
                Grocerylist += arrayAdapter.getItem(i) + ",";
            }
        }
        Groceries groceries = new Groceries("Hardcoded", userid, Grocerylist);
        db.collection("Grocery").document("groceries").set(groceries);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_item:
                String itemEntered = addItemT.getText().toString();
                addItemT.setText("");
                arrayAdapter.add(itemEntered);


                Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
                break;
        }
        if (v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), SignIn.class));
        }
        if(v == editPwButton){
            editPw();
        }
        if(v == editUserNameButton){
            editUserName();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stock.remove(position);
        arrayAdapter.notifyDataSetChanged();
        //   FileHelper.writeData(items, this);
        Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
    }

    public void leaveHousehold(){
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
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
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

