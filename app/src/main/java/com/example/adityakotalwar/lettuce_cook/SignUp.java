package com.example.adityakotalwar.lettuce_cook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    // form fields
    private EditText UserName;
    private EditText Email;
    private EditText Password;

   // private String userName;

    //buttons
    private Button ButtonSignup;
    private TextView textViewSignIn;

    //progressBar
    public ProgressDialog progressDialog;

    //firebase auth object
    public FirebaseAuth firebaseauth;
    private FirebaseFirestore db;

    // databaseReference Object
    private DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        UserName = (EditText) findViewById(R.id.UserName);
        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        ButtonSignup = (Button) findViewById(R.id.ButtonSignup);
        textViewSignIn = (TextView)findViewById(R.id.textViewSignIn);

        ButtonSignup.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

        firebaseauth = FirebaseAuth.getInstance();
    }

    private void registerUser(){

        final String userName = UserName.getText().toString().trim();
        final String email = Email.getText().toString().trim();
        String pw = Password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering");
        progressDialog.show();

        firebaseauth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        // If user creation was successful we enter the if
                        if(task.isSuccessful()){
                            CollectionReference dbUser = db.collection("Users");
                            String id = firebaseauth.getCurrentUser().getUid();
                            // Gets the userId of the person loggen in.
                            UserCollection user = new UserCollection(email, userName, id);
                            // store the user details in a userCollection class
                            dbUser.add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                            // stores the user information if a failure pops a toast
                            finish();
                            // finishes the current activity
                            startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
                            //redirects to MainActivity
                        }
                        else{
                            Toast.makeText(SignUp.this, "Not Registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Does this entire authification and check whether email already exists


    }

    @Override
    public void onClick(View view) {
        if(view == ButtonSignup){
            registerUser();
        }
        if(view == textViewSignIn){
            startActivity(new Intent(this, SignIn.class));
        }
    }
}
