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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

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
                addMember();
            }
        });
    }

    public void createHousehold(){

        householdName = householdText.getText().toString();

        db = FirebaseFirestore.getInstance();
        CollectionReference dbHousehold = db.collection("Household");
        // Gets the userId of the person loggen in.



       // household.setHouseholdName(householdName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            household.addMember(user.getUid());

        }
        //db.document(householdName).set(household);
        // store the user details in a userCollection class
        dbHousehold.document(householdName)
                .set(household)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

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

    public void addMember(){
        final String member = addMemberText.getText().toString().trim();
        db = FirebaseFirestore.getInstance();
        final List<String> list = new ArrayList<>();
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            boolean userPresent = false;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //list.add(document.getId());
                        if(document.getId().equals(member)){
                            household.addMember(member);
                            db.collection("Household").document(householdName).set(household, SetOptions.merge());
                            userPresent = true;
                        }
                    }
                    if(!userPresent){
                        System.out.println("aint no user like this");
                    }
                } else {
                    System.out.println("not happening");
                }
            }
        });




    }


}
