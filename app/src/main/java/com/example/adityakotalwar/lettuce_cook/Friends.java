package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class Friends extends AppCompatActivity {
    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button showUsersButton;
    private TextView listOfUsers;

    private Button showNotiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        groceryButton = findViewById(R.id.buttonGrocery);
        stockButton = findViewById(R.id.buttonStock);
        friendsButton = findViewById(R.id.buttonFriends);
        recipesButton = findViewById(R.id.buttonRecipes);
        showUsersButton = findViewById(R.id.showUsers);
        listOfUsers = findViewById(R.id.listUsers);

        showNotiButton = findViewById(R.id.showNotiButton);

        recipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Friends.this,Recipes.class);
                startActivity(intent);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Friends.this,MainActivity.class);
                startActivity(intent);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Friends.this,Grocery.class);
                startActivity(intent);
            }
        });

        showUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTheUsers();
            }
        });

        showNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoti("totalwar1");
//                sendNoti();
            }
        });


    }

    public void showTheUsers(){
        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // store the user details in a userCollection class
        System.out.println(user.getUid());
        final DocumentReference dr = db.collection("Users").document(user.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("household");
                    final DocumentReference dr2 = db.collection("Household").document(hName);
                    dr2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for(DocumentSnapshot ds : queryDocumentSnapshots ){
                                        if(ds.getString("household").equals(hName)){
                                            listOfUsers.append(ds.getString("username") + "\n");
                                        }
                                    }
                                }
                            });

                        }
                    });
                    // household.addMember(user.getUid());
                    db.collection("Users").document(user.getUid()).update("invited", "");
                    //   db.collection("Household").document(hName).update("members", household.getMembers());

            }
        });

    }

    public void showNoti(final String household){
        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Friends.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_show_notification, null);

        ListView listView = mView.findViewById(R.id.noti_view);
        ArrayList<String> notifications = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(arrayAdapter);

        mBuilder.setView(mView);
        // Pops the dialog on the screen
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

//        populateNoti(arrayAdapter, db, household);
        db.collection("Household").document(household).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        arrayAdapter.clear();
                        populateNoti(arrayAdapter, db, household);
                    }
                });

    }

    public void populateNoti(final ArrayAdapter<String> arrayAdapter, final FirebaseFirestore db, String household){

        db.collection("Household").document(household).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String[] notifications = documentSnapshot.get("noti_list").toString().split(" ");
                for(int i=0; i<notifications.length; i++){
                    arrayAdapter.clear();
                    db.collection("Notification").document(notifications[i]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            arrayAdapter.add(documentSnapshot.get("noti_body").toString());
                        }
                    });
                }

            }
        });

    }

//    public String[] notiSplitter(String noti_list){
//        String[] notifications = noti_list.split("|");
//        return notifications;
//    }

    public void sendNoti(){
        InAppNotiCollection noti = new InAppNotiCollection("totalwar1","aditya","alisha","have my babies");
        noti.sendInAppNotification(noti);
    }
}

