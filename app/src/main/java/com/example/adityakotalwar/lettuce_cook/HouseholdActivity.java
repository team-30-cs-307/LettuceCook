package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HouseholdActivity extends AppCompatActivity {

    private Button createHouseholdButton;
    private EditText householdText;
    private FirebaseFirestore db;
    private EditText addMemberText;
    private Button addMemberButton;

    private String householdName;

    final Household household = new Household();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);

        createHouseholdButton = (Button) findViewById(R.id.HouseholdButton);
        householdText = (EditText) findViewById(R.id.householdText);

        addMemberText = (EditText) findViewById(R.id.addMemberText);
        addMemberButton = findViewById(R.id.addMemberButton);

        createHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createHousehold();
            }
        });

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String member = addMemberText.getText().toString().trim();
                if(!member.isEmpty()) {
                    addMember(member);
                }else{
                    Toast.makeText(HouseholdActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createHousehold(){

        householdName = householdText.getText().toString();

        db = FirebaseFirestore.getInstance();
        CollectionReference dbHousehold = db.collection("Household");
        // Gets the userId of the person loggen in.
        DocumentReference house = dbHousehold.document(householdName);

        System.out.println(house.getId()+" house id");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            household.addMember(user.getUid());
        }
        // store the user details in a userCollection class
        dbHousehold.document(householdName)
                .set(household)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Users").document(user.getUid()).update("household", householdName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HouseholdActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        // stores the user information if a failure pops a toast
        //finish();
        // finishes the current activity
    }

    public void addMember(final String member){

        db = FirebaseFirestore.getInstance();
        final List<String> list = new ArrayList<>();


        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                boolean userPresent = false;
                for(DocumentSnapshot ds : queryDocumentSnapshots ){
                   userPresent = false;
                    System.out.println("entering");
                    if(ds.getString("username").equals(member)){
                       // ArrayList<String> listMembers = ds.get
                        household.addMember(ds.getId());
                        db.collection("Household").document(householdName).update("members", household.getMembers());
                        System.out.println(ds.getId());
                        System.out.println("household name : " + householdName);
                        db.collection("Users").document(ds.getId()).update("household", householdName);
                        userPresent = true;
                        break;
                    }
                }
                if(!userPresent){
                    Toast.makeText(HouseholdActivity.this, "Enter a valid username!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


}
