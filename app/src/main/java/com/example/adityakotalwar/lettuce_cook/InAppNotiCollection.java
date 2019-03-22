package com.example.adityakotalwar.lettuce_cook;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class InAppNotiCollection {

        String householdName;
        String sender_userName;
        String noti_title;
        String noti_body;
        String notifications;

        InAppNotiCollection(String householdName, String sender_userName, String noti_title, String noti_body){
            this.householdName = householdName;
            this.sender_userName = sender_userName;
            this.noti_title = noti_title;
            this.noti_body = noti_body;
            this.notifications = "";
        }

        void sendInAppNotification(final InAppNotiCollection inAppNotificationCollection){
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference dbNoti = db.collection("Notification");
            final DocumentReference house = db.collection("Household").document(inAppNotificationCollection.householdName);


            //Saves the Notification in NotificationCollection
            db.collection("Users").document(sender_userName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    inAppNotificationCollection.sender_userName = documentSnapshot.getString("username");
                    dbNoti.add(inAppNotificationCollection).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            house.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    notifications = documentSnapshot.get("noti_list").toString();
                                    notifications += documentReference.getId() + " ";
                                    house.update("noti_list", notifications);
                                }
                            });
                        }
                    });
                }
            });



//            dbNoti.document().set(inAppNotificationCollection).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    house.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                            String noti = documentSnapshot.get("noti_list").toString();
//                            house.update("noti_list", noti +"|"+.getId());
//                            return;
//                            //saves a string of noti ids in Household Collection
//                        }
//                    });
////                    db.collection("Household").document(householdName).update("noti_list",dbNoti.getId());
//                }
//            })
//            ;
        }

    }


