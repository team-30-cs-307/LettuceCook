package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private FirebaseFirestore db;
    private Button editCredientialsButton;


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
        }
        db = FirebaseFirestore.getInstance();
        addItemB = (Button) findViewById(R.id.button_add_item);
        update = (Button) findViewById(R.id.update);
        addItemT = (EditText) findViewById(R.id.edit_text_add_item);
        listView = (ListView) findViewById(R.id.my_list_view2);
        goToRecipes = (Button) findViewById(R.id.go_to_recipes_button);
        editCredientialsButton = findViewById(R.id.editCredientials);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        getButton = (Button) findViewById(R.id.getButton);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stock);
        listView.setAdapter(arrayAdapter);

        addItemB.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        buttonLogout.setOnClickListener(this);

        goToRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Recipes.class);
                startActivity(intent);
            }
        });

        editCredientialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_pw, null);

                final EditText currPassword = (EditText) mView.findViewById(R.id.PwCurrent);
                final EditText text = (EditText) mView.findViewById(R.id.EmailReset);
                final EditText confirmText = (EditText) mView.findViewById(R.id.ConfirmPwReset);
                Button changePassword = (Button) mView.findViewById(R.id.ButtonConfirm);

                mBuilder.setView(mView);
                // Pops the dialog on the screen
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                        String Email = user.getEmail();
//                        Log.i("check", user.getEmail().toString());
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail().toString(), currPassword.getText().toString());
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    user.updatePassword(text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "WRONG", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "SAXXXXXXX", Toast.LENGTH_LONG).show();

                                            }
                                            //           }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();

                                }
                            }
                            //  }
                            //  }
                        });

                    }
                });
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = firebaseAuth.getCurrentUser().getUid();
                groceries(arrayAdapter, id, "Hardcoded ID");
            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Grocery").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            if (ds.getId().equals("groceries")) {
                                String itemEntered;
                                itemEntered = ds.getString("groceries");
                                String[] arrOfgroceries = itemEntered.split(",");
                                for (int i = 0; i < arrOfgroceries.length; i++) {
                                    arrayAdapter.add(arrOfgroceries[i]);
                                }
                                break;

                            }


                        }

                    }
                });
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stock.remove(position);
        arrayAdapter.notifyDataSetChanged();
        //   FileHelper.writeData(items, this);
        Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
    }
}
