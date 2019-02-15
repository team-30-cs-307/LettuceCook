package com.example.adityakotalwar.lettuce_cook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private Button ButtonSignIn;
    private EditText Email;
    private EditText Password;
    public TextView textViewSignUp;

    public ProgressDialog progressDialog;
    public FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);

        ButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);

        ButtonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        firebaseauth = FirebaseAuth.getInstance();
    }

    private void signinUser(){

        String email = Email.getText().toString().trim();
        String pw = Password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering");
        progressDialog.show();

        firebaseauth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == ButtonSignIn){
            signinUser();
        }
        else{
            startActivity(new Intent(getApplicationContext(), SignUp.class));
        }
    }
}
