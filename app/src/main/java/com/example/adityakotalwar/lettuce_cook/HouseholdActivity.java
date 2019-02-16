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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HouseholdActivity extends AppCompatActivity {

    private Button createHouseholdButton;
    private EditText householdText;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);

        createHouseholdButton = (Button) findViewById(R.id.HouseholdButton);
        householdText = (EditText) findViewById(R.id.householdText);

        createHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createHousehold();
            }
        });
    }

    public void createHousehold(){

        String householdName = householdText.getText().toString();

        db = FirebaseFirestore.getInstance();
        CollectionReference dbHousehold = db.collection("Household");
        // Gets the userId of the person loggen in.


        final Household household = new Household();
        household.setHouseholdName(householdName);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    System.out.println("hello");
                    // User is signed in
                    household.addMember(user.getUid());
                }
                // ...
            }
        };

        // store the user details in a userCollection class
        dbHousehold.add(household)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

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


}
