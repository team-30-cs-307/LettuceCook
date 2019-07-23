
package com.example.adityakotalwar.lettuce_cook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HouseholdActivity extends AppCompatActivity implements View.OnClickListener{

    private Button createHouseholdButton;
    private EditText householdText;
    private FirebaseFirestore db;
    private Button joinButton;
    private Button showInvitesButton;
    private TextView invitesText;
    private Button showUsersButton;
    private TextView listOfUsers;

    private ListView inviting_view;
    private CustomAdapter inviteListAdapter;

    private String householdName;


    private ArrayList<String> invites = new ArrayList<>();
    //final Household household = new Household();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household2);


        createHouseholdButton = (Button) findViewById(R.id.householdButton);
        householdText = (EditText) findViewById(R.id.householdText);

        joinButton = findViewById(R.id.joinHouseholdButton);
        showInvitesButton = findViewById(R.id.showInvites);
        invitesText = findViewById(R.id.invites);
        showUsersButton = findViewById(R.id.showUsers);
        listOfUsers = findViewById(R.id.listUsers);
        inviting_view = findViewById(R.id.invite_view2);
//        ArrayList<String> inv = getInvites();
//        inviteListAdapter = new CustomAdapter(this,inv);
//
//       inviting_view.setAdapter(inviteListAdapter);
      //  showInvites();
        db = FirebaseFirestore.getInstance();

        createHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                householdName = householdText.getText().toString();
                if(householdName.isEmpty()){
                    householdText.setError("Enter a household name");
                    return;
                }
                db.collection("Household").document(householdName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            householdText.setError("Enter a unique user name");
                            return;
                        }
                        else{
                            createHousehold();
                            // manages the redirection to MAINACTIVITY as well
                        }
                    }
                });
            }
        });

//        joinButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder logout_confir = new AlertDialog.Builder(HouseholdActivity.this);
//                logout_confir.setMessage("Are you sure you want to join this household")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                join();
//                                finish();
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.cancel();
//                            }
//                        });
//                AlertDialog alertDialog = logout_confir.create();
//                alertDialog.show();
//            }
//        });

        showInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInvites();
            }
        });

        showUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTheUsers();
            }
        });

        //invite();

//        addMemberButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view) {
//                String member = addMemberText.getText().toString().trim();
//                if(!member.isEmpty()) {
//                    addMember(member);
//                }else{
//                    Toast.makeText(HouseholdActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    @Override
    public void onClick(View view) {
    //    if (view.getId() == R.id.ButtonSignup || view.getId() == R.id.leave_house){
            showInvites();
      //  }
    }

    public ArrayList<String> getInvites(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String[] inviteForHousehold = documentSnapshot.getString("invited").toString().split(" ");
                final int size = inviteForHousehold.length;
                for(int i=0; i<size; i++){

                    invites.add(inviteForHousehold[i]);
                }
            }
        });
        return invites;
    }

    public void createHousehold(){

        householdName = householdText.getText().toString();


        //CollectionReference dbHousehold = db.collection("Household");
        // Gets the userId of the person loggen in.

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

      //  final ArrayList<String> member = new ArrayList<>();
        //member.add(user.getUid());

        final String notification_id = "";
        Household household = new Household(user.getUid(), "","");
//       household.setMember(user.getUid());
//       household.setInvited("");
//        System.out.println(member.get(0));
        // store the user details in a userCollection class
        db.collection("Household").document(householdName)
                .set(household)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Users").document(user.getUid()).update("household", householdName);
                        db.collection("Household").document(householdName).update("members", user.getUid()+" ");
                        db.collection("Household").document(householdName).update("friendRequests", "");
                        db.collection("Household").document(householdName).update("friends", "");
                        db.collection("Household").document(householdName).update("shared_recipe_list", "");
                        db.collection("Household").document(householdName).update("recipe_shared_with_friends", "");
                        db.collection("Household").document(householdName).update("recipe_list", "");
                        db.collection("Household").document(householdName).update("noti_list", notification_id);

                        FirebaseMessaging.getInstance().subscribeToTopic(householdName);

                        //finish(); //just commented this out yaas queen
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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


    public void invite(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        // store the user details in a userCollection class
        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot ds : queryDocumentSnapshots ){
                    if(!ds.getString("invited").equals("") && ds.getId().equals(user.getUid())){
                        String name = ds.getString("invited");
                        Toast.makeText(HouseholdActivity.this, "invited to "+name, Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });
    }

    public void join(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // store the user details in a userCollection class
        System.out.println(user.getUid());
        final DocumentReference dr = db.collection("Users").document(user.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("invited");
                if(!hName.equals("")){

                    db.collection("Users").document(user.getUid()).update("household", hName );
                    db.collection("Users").document(user.getUid()).update("invited", "");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    final DocumentReference dr2 = db.collection("Household").document(hName);
                    dr2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            //String hName = documentSnapshot2.getString("invited");
                            String listMembers = documentSnapshot2.getString("members");
                            System.out.println("entered here " + listMembers);
                            listMembers += " " + user.getUid();
                            System.out.println("after appending " + listMembers);
                            db.collection("Household").document(hName).update("members", listMembers);
                            householdName = hName;

                            /*Sends Notification*/
                            RequestQueue requestQueue = Volley.newRequestQueue(HouseholdActivity.this);
                            Notifications n = new Notifications();
                            try {
                                n.sendNotification(hName+" has new member!",user.getEmail()+ " has joined the household", hName, requestQueue);
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            /*Subscribes the user to the household*/
                            FirebaseMessaging.getInstance().subscribeToTopic(hName);
                        }
                    });
                    // household.addMember(user.getUid());
                    db.collection("Users").document(user.getUid()).update("invited", "");
                    //   db.collection("Household").document(hName).update("members", household.getMembers());
                }
            }
        });

    }
    public void decline(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // store the user details in a userCollection class
        System.out.println(user.getUid());
        db.collection("Users").document(user.getUid()).update("invited", "");
        Toast.makeText(HouseholdActivity.this, "Invitation declined!", Toast.LENGTH_LONG).show();


    }

    public void checkUserExists(final String id){

        db.collection("Household").addSnapshotListener(new EventListener<QuerySnapshot>() {
            boolean exists = false;
            ArrayList<String> listMembers = new ArrayList<>();
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    if (ds.getId().equals(householdName)) { //finds the household to check with all the member names in the house
                        for (Object item : ds.getData().values()) {
                            listMembers.add(item.toString());
                        }
                        if (listMembers.contains(id)) {
                            Toast.makeText(HouseholdActivity.this, "This user is already in the household", Toast.LENGTH_LONG).show();
                            exists = true;
                        }
                    }
                }
            }
        });
    }

    public void showTheUsers(){
        // final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // store the user details in a userCollection class
        System.out.println(user.getUid());
        final DocumentReference dr = db.collection("Users").document(user.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("invited");
//                final DocumentReference dr2 = db.collection("Household").document(hName);
//                dr2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                //  @Override
                //   public void onSuccess(DocumentSnapshot documentSnapshot2) {
                db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots ){
                            if(ds.getString("household").equals(hName)){
                                listOfUsers.append("Users in household: \n" +ds.getString("username") + "\n");
                            }
                        }
                    }
                    //  });

                    //}
                });
                // household.addMember(user.getUid());
                db.collection("Users").document(user.getUid()).update("invited", "");
                //   db.collection("Household").document(hName).update("members", household.getMembers());

            }
        });

    }

    public void showInvites(){

        ArrayList<String> inv = getInvites();
        inviteListAdapter = new CustomAdapter(this,inv);

        inviting_view.setAdapter(inviteListAdapter);

    }




    /** This is where the dynamic join decline shit starts
     * there is a new class created that will help dynamically help created
     * class starts here
     * yaaaaas queen
     */

    class CustomAdapter extends BaseAdapter {

        ArrayList<String> invites;
        Context context;

        public CustomAdapter(Context context, ArrayList<String> invites) {

            this.invites = invites;
            this.context = context;

        }
        @Override
        public int getCount() {
            return this.invites.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = getLayoutInflater().inflate(R.layout.dialog_join_decline, null);
            //convertView = getLayoutInflater().inflate(R.layout.dialog_join_decline, null);
            TextView inviteName = convertView.findViewById(R.id.invite_name);
            Button joinButton = convertView.findViewById(R.id.join_button);
            Button declineButton = convertView.findViewById(R.id.decline_button);
            inviteName.setText(invites.get(position));
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    join();
                }
            });
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    decline();
                }
            });



            return convertView;
        }
    }


}

    