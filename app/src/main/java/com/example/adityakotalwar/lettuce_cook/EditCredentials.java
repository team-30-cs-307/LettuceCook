package com.example.adityakotalwar.lettuce_cook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditCredentials extends AppCompatActivity {
    private Button changePassword = (Button) findViewById(R.id.changePassword);
    FirebaseAuth auth;
    private EditText text = (EditText) findViewById(R.id.newPassword);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_credentials);
    //    auth = FirebaseAuth.getInstance();


    }


}
