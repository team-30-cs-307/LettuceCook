package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nullable;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class Friends extends AppCompatActivity {
    private Button groceryButton;
    private Button friendsButton;
    private Button friendRequestsButton;
    private Button stockButton;
    private Button recipesButton;
    private Button showUsersButton;
    private TextView listOfUsers;
    ListView listFriends;
    SearchView searchView;
    ArrayAdapter<String> adapter;

    private Button showNotiButton;
    private String friendToBeAdded;

    FirebaseFirestore db =  FirebaseFirestore.getInstance();

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
        listFriends = findViewById(R.id.listviewFriends);
        final ArrayList<String> arrayFriends = new ArrayList<>();
        final ArrayList<String> arrayHouseholds = new ArrayList<>();
        final FirebaseFirestore db =  FirebaseFirestore.getInstance();

        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    if (!arrayFriends.contains(ds.getString("username"))) {
                        arrayFriends.add(ds.getString("username"));
                        arrayHouseholds.add(ds.getString("household"));
                    }
                }
            }
        });
        //  arrayFriends.addAll(Arrays.asList(getResources().getStringArray(R.array.array_friends)));

        adapter = new ArrayAdapter<>(
                Friends.this,
                android.R.layout.simple_list_item_1,
                arrayHouseholds);
        listFriends.setAdapter(adapter);

        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RequestQueue requestQueue = Volley.newRequestQueue(Friends.this);
                Notifications n = new Notifications();
                try {
                    n.sendNotification(adapter.getItem(i),"We would like to invite you over for dinner", adapter.getItem(i), requestQueue);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
//                InAppNotiCollection notiCollection = new InAppNotiCollection(adapter.getItem(i), " ", "Friend Reuqest Sent!", adapter.getItem(i) );
//                notiCollection.sendInAppNotification(notiCollection);
                Toast.makeText(Friends.this, "Sent invite to  " + adapter.getItem(i), Toast.LENGTH_LONG).show();
              // Toast.makeText(Friends.this, "Hello " + arrayHouseholds.get(arrayFriends.indexOf(adapter.getItem(i))), Toast.LENGTH_LONG).show();
            }
        });

        showNotiButton = findViewById(R.id.showNotiButton);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        friendRequestsButton = findViewById(R.id.friendRequest);

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

        final Context obj = this;
        friendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(obj);
                builder.setTitle("Enter household name to send friend request to");

// Set up the input
                final EditText input = new EditText(obj);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                // input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        friendToBeAdded = input.getText().toString();
                        //System.out.println("getting username " +userToBeAdded);
                        sendFriendRequest(friendToBeAdded);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

    public void sendFriendRequest(final String friend){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final String householdName = documentSnapshot.getString("household");
                db.collection("Household").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots ){
                            if(ds.getId().equals(friend)){
                                db.collection("Household").document(ds.getId()).update("friendRequests", householdName);
                                Toast.makeText(getApplicationContext(), "Friend Request Sent!", Toast.LENGTH_LONG).show();


                        /*Sends notification if a household send a friend request to another user*/
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                Notifications n = new Notifications();
                                try {
                                    n.sendNotification("Friend Request",householdName+"has sent you a friend request!", ds.getId(), requestQueue);
                                } catch (InstantiationException e1) {
                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }

                                return;
                            }
                        }
                        // if(!userPresent){

                        ///}
                    }
                });
            }
        });
    }

    public void showNoti(final String household){

        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Friends.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_noti_view, null);
        mBuilder.setView(mView);
        final ListView listView = mView.findViewById(R.id.noti_view);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final ArrayList<String> notification_title = new ArrayList<>();
        final ArrayList<String> notification_body = new ArrayList<>();
        final ArrayList<String> sender = new ArrayList<>();

        db.collection("Household").document(household).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        notification_body.clear();
                        notification_title.clear();
                        sender.clear();
                        populateNoti(listView, db, household, notification_title, notification_body, sender);
                    }
                });

    }

    public void populateNoti(final ListView listView, final FirebaseFirestore db, final String household,
                             final ArrayList<String> notification_title, final ArrayList<String> notification_body, final ArrayList<String> sender){


        db.collection("Household").document(household).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String[] notifications = documentSnapshot.get("noti_list").toString().split(" ");
                final int size = notifications.length;
                for(int i=0; i<size; i++){

                    db.collection("Notification").document(notifications[i]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            notification_title.add(documentSnapshot.get("noti_title").toString());
                            notification_body.add(documentSnapshot.get("noti_body").toString());
                            sender.add(documentSnapshot.get("sender_userName").toString());

                            CustomAdapter customAdapter = new CustomAdapter(notification_title, notification_body, sender);
                            listView.setAdapter(customAdapter);

                        }
                    });
                }

            }
        });



    }

    public void sendNoti(){
        InAppNotiCollection noti = new InAppNotiCollection("totalwar1","aditya","alisha","have my babies");
        noti.sendInAppNotification(noti);
    }

    class CustomAdapter extends BaseAdapter{

        ArrayList<String> notiTitle;
        ArrayList<String> notiBody;
        ArrayList<String> sender;

        public CustomAdapter(ArrayList<String> notiTitle, ArrayList<String> notiBody, ArrayList<String> sender) {

            this.notiTitle = notiTitle;
            this.notiBody = notiBody;
            this.sender = sender;

        }
        @Override
        public int getCount() {
            return this.notiTitle.size();
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
            convertView = getLayoutInflater().inflate(R.layout.dialog_noti_view_listview_layout, null);
            TextView noti_title = convertView.findViewById(R.id.invite_name);
            TextView noti_body = convertView.findViewById(R.id.noti_body);
            TextView sender_username = convertView.findViewById(R.id.sender_username);

            noti_title.setText(notiTitle.get(position));
            noti_body.setText(notiBody.get(position));
            sender_username.setText(sender.get(position));

            return convertView;
        }
    }

}

