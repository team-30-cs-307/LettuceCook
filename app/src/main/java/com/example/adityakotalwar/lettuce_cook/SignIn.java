package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private Button ButtonSignIn;
    private EditText Email;
    private EditText Password;
    public TextView textViewSignUp;
    public TextView textViewForgotPw;

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
        textViewForgotPw = (TextView) findViewById(R.id.textViewForgotPw);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);

        ButtonSignIn.setOnClickListener(this);
        textViewForgotPw.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        firebaseauth = FirebaseAuth.getInstance();
    }

    private void signinUser(){

        String email = Email.getText().toString().trim();
        String pw = Password.getText().toString().trim();

        if(email.isEmpty()){
            Email.setError("Enter Email");
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Password.setError("Enter Password");
            return;
        }

        progressDialog.setMessage("Signing In");
        progressDialog.show();

        firebaseauth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), HouseholdActivity.class));
                        }
                    }
                });
    }

    private void forgotPw(String EmailReset){
        firebaseauth.sendPasswordResetEmail(EmailReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignIn.this, "Please check your email account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), SignIn.class));
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SignIn.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    @Override
    public void onClick(View view) {
        if(view == ButtonSignIn){
            signinUser();
        }
        if(view == textViewForgotPw){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignIn.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_forgot_pw, null);

            final EditText EmailReset = (EditText) mView.findViewById(R.id.EmailReset);
            Button ButtonForgotPw = (Button) mView.findViewById(R.id.ButtonForgotPw);

            mBuilder.setView(mView);
            // Pops the dialog on the screen
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            ButtonForgotPw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tempEmail = EmailReset.getText().toString();
                    if(!tempEmail.isEmpty()){
                        dialog.dismiss();
                        forgotPw(tempEmail);
                    }
                    else{
                        Toast.makeText(SignIn.this, "Enter an Email!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(view == textViewSignUp){
            startActivity(new Intent(getApplicationContext(), SignUp.class));
        }

    }
}
