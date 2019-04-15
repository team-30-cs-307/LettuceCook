package com.example.adityakotalwar.lettuce_cook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nullable;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class Friends extends AppCompatActivity {
    private Button groceryButton;
    private Button friendsButton;
    private Button stockButton;
    private Button recipesButton;

    private Button friendRequestsButton;
    private Button showUsersButton;
    private TextView listOfUsers;

    ListView listFriends;
    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;

    private ImageButton showNotiButton;
    private String friendToBeAdded;
    private ListView requests_invites;
    private Button showRequestsButton;
    private RequestAdapter requestListAdapter;
    private ArrayList<String> requests = new ArrayList<>();

    private DrawerLayout coordinatorLayout;
    private ActionBarDrawerToggle t;

    private ArrayList<String> notification_title = new ArrayList<String>();
    private ArrayList<String> notification_body = new ArrayList<>();
    private ArrayList<String> sender = new ArrayList<>();
    private ArrayList<String[]> noti_details = new ArrayList<String[]>();

    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    private String householdName;
    String friendRequests;

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
        //final FirebaseFirestore db =  FirebaseFirestore.getInstance();

        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    if (ds.getString("household")!=null && !arrayHouseholds.contains(ds.getString("household"))) {
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

        coordinatorLayout = (DrawerLayout) findViewById(R.id.activity_drawer);
//        additem.performClick();

        coordinatorLayout.setOnTouchListener(new OnSwipeTouchListener(Friends.this) {
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });


        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RequestQueue requestQueue = Volley.newRequestQueue(Friends.this);
                Notifications n = new Notifications();
                try {
                    n.sendNotification(adapter.getItem(i),"We would like to invite you over for dinner", adapter.getItem(i), requestQueue);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                InAppNotiCollection notiCollection = new InAppNotiCollection(adapter.getItem(i), user.getUid(), "Dinner Invitation", adapter.getItem(i) + " has invited you over", Calendar.getInstance().getTime().toString());
                notiCollection.sendInAppNotification(notiCollection);
                Toast.makeText(Friends.this, "Sent invite to  " + adapter.getItem(i), Toast.LENGTH_LONG).show();
            }
        });

        showNotiButton = findViewById(R.id.showNotiButton);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        friendRequestsButton = findViewById(R.id.friendRequest);
        showRequestsButton = findViewById(R.id.showRequests);

        showNotiButton = findViewById(R.id.showNotiButton);
        requests_invites = findViewById(R.id.requests_and_invites);

        friendsButton.setTextColor(Color.parseColor("#5D993D"));
        recipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Friends.this,Recipes.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Friends.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        groceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Friends.this,Grocery.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String hName = documentSnapshot.getString("household");
                        showNoti(hName);
                    }
                });

//                sendNoti();
            }
        });

        showRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequests();
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
                    public void onClick(final DialogInterface dialog, int which) {
                        friendToBeAdded = input.getText().toString();

                        db.collection("Household").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<String> houses = new ArrayList<>();
                                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                    houses.add(ds.getId());
                                }
                                if(!houses.contains(friendToBeAdded)){
                                    Toast.makeText(Friends.this, "Invalid household name!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                else{
                                    sendFriendRequest(friendToBeAdded);
                                }
                            }
                        });

                        //System.out.println("getting username " +userToBeAdded);

                       // sendFriendRequest(friendToBeAdded);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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


    public void sendFriendRequest(final String friend) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DocumentReference dr = db.collection("Users").document(user.getUid());
        final DocumentReference house = db.collection("Household").document(friend);


        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("household");
                house.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String friendRequests = documentSnapshot.getString("friendRequests");
                        if (friendRequests == null) {
                            friendRequests = hName;
                        } else {
                            friendRequests += " " + hName;
                        }
                        // System.out.println("this is fififiififiififfi "+friendRequests);
                        db.collection("Household").document(friend).update("friendRequests", friendRequests);

                         /*Sends notification if a household send a friend request to another user*/
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        Notifications n = new Notifications();
                        try {
                            n.sendNotification("Friend Request", householdName + "has sent you a friend request!", friend, requestQueue);
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        }

                        return;

                    }
                });
            }
        });

    }
    public void getRequests(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DocumentReference dr = db.collection("Users").document(user.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                String householdName = documentSnapshot.getString("household");
                // System.out.println(householdName + "here");
                db.collection("Household").document(householdName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        System.out.println(documentSnapshot.getString("friendRequests") + "here");
                        String[] inviteForHousehold = documentSnapshot.getString("friendRequests").toString().split(" ");
                        ArrayList<String> requests = new ArrayList<>();
                        final int size = inviteForHousehold.length;
                        for (int i = 0; i < size; i++) {
                            requests.add(inviteForHousehold[i]);

                        }
                        for (String j : requests) {
                            if (!j.equals(" ") && !j.equals("")) {
                                requestListAdapter = new Friends.RequestAdapter(getApplicationContext(), requests);

                                requests_invites.setAdapter(requestListAdapter);
                            }
                        }
                    }
                });
            }
        });
    }

    public void accept(final String newFriend){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DocumentReference username = db.collection("Users").document(user.getUid());
        final DocumentReference otherHouse = db.collection("Household").document(newFriend);


        username.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("household");
                db.collection("Household").document(hName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String currFriend = documentSnapshot.getString("friends");
                        if(currFriend == null){
                            currFriend = newFriend;
                        }else{
                            currFriend += " " + newFriend;
                        }
                        String[] currFriendRequests = documentSnapshot.getString("friendRequests").split(" ");
                        String newFriendRequests = removeFriendRequest(currFriendRequests, newFriend);
                        db.collection("Household").document(hName).update("friends", currFriend);
                        db.collection("Household").document(hName).update("friendRequests", newFriendRequests);

                        otherHouse.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String friend = documentSnapshot.getString("friends");
                                if (friend == null) {
                                    friend = hName;
                                } else {
                                    friend += " " + hName;
                                }
                                // System.out.println("this is fififiififiififfi "+friendRequests);
                                db.collection("Household").document(newFriend).update("friends", friend);

                         /*Sends notification if a household send a friend request to another user*/
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                Notifications n = new Notifications();
                                try {
                                    n.sendNotification("Friend Request Accepted!", householdName + "has accepted your friend request!", hName, requestQueue);
                                } catch (InstantiationException e1) {
                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }
                                return;
                            }
                        });
                    }
                });

            }
        });
        requests.remove(newFriend);
    }

    public void decline(final String removeFriend){
        System.out.println("coming here");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference username = db.collection("Users").document(user.getUid());
        username.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String hName = documentSnapshot.getString("household");
                db.collection("Household").document(hName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String[] requests = documentSnapshot.getString("friendRequests").split(" ");
                        String newfriendRequests = removeFriendRequest(requests, removeFriend);
                        db.collection("Household").document(hName).update("friendRequests", newfriendRequests);
                        Toast.makeText(Friends.this, "Invitation declined!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private String removeFriendRequest(String [] mems, String user){
        for(String a : mems)
            System.out.println(a);
        String newMems = "";
        for(int i=0; i< mems.length; i++){
            if(!mems[i].equalsIgnoreCase(user)){
                if(newMems.length()==0){
                    newMems = mems[i];
                }
                else {
                    newMems += " " + mems[i];
                }
            }
        }
        return newMems;
    }

    public void showNoti(final String household){

        final FirebaseFirestore db =  FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Friends.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_noti_view, null);
        mBuilder.setView(mView);
        listView = mView.findViewById(R.id.noti_view);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();

        ImageButton back_button = mView.findViewById(R.id.back_button);

        notification_body.clear();
        notification_title.clear();
        sender.clear();

        db.collection("Household").document(household).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final ArrayList<String> notifications = new ArrayList<String>(Arrays.asList(documentSnapshot.get("noti_list").toString().split(" ")));
                db.collection("Notification").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot ds: queryDocumentSnapshots){
                            if(notifications.contains(ds.getId())){
                                notification_body.add(ds.get("noti_body").toString());
                                notification_title.add(ds.get("noti_title").toString());
                                sender.add(ds.get("sender_userName").toString());
                            }
                        }
                        CustomAdapter customAdapter = new CustomAdapter(notification_title, notification_body, sender);
                        listView.setAdapter(customAdapter);
                    }
                });
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(notification_title.get(i).contains("dinner")){
                    AlertDialog.Builder inv = new AlertDialog.Builder(Friends.this);
                    inv.setMessage("Do you accept their invitation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    Notifications n = new Notifications();
                                    try {
                                        n.sendNotification("Accepted!",householdName+" wants to come over!", user.getUid(), requestQueue);
                                    } catch (InstantiationException e1) {
                                        e1.printStackTrace();
                                    } catch (IllegalAccessException e1) {
                                        e1.printStackTrace();
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    Notifications n = new Notifications();
                                    try {
                                        n.sendNotification("Rejected!",householdName+" doesn't want to come over!", user.getUid(), requestQueue);
                                    } catch (InstantiationException e1) {
                                        e1.printStackTrace();
                                    } catch (IllegalAccessException e1) {
                                        e1.printStackTrace();
                                    }
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = inv.create();
                    alertDialog.show();
                    // }

                }
                return true;
            }
        });
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

    class RequestAdapter extends BaseAdapter {

        ArrayList<String> invites;
        Context context;

        public RequestAdapter(Context context, ArrayList<String> invites) {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = getLayoutInflater().inflate(R.layout.dialog_join_decline, null);
                //convertView = getLayoutInflater().inflate(R.layout.dialog_join_decline, null);
                TextView inviteName = convertView.findViewById(R.id.invite_name);
                Button joinButton = convertView.findViewById(R.id.join_button);
                Button declineButton = convertView.findViewById(R.id.decline_button);

                inviteName.setText(invites.get(position) + " has sent you a friend request");
                joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accept(invites.get(position));
                    }
                });
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // System.out.println("lols ho gaya");
                        decline(invites.get(position));
                    }
                });
            }



            return convertView;
        }
    }

}

