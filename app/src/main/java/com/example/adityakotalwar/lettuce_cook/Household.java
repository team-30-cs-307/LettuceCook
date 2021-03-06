package com.example.adityakotalwar.lettuce_cook;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alishalakkad on 2/15/19.
 */
@IgnoreExtraProperties
public class Household {

    private String householdName;
   // private ArrayList<String> members = new ArrayList<>();
    String notification_ids = "";
    private String members;
    private String friendRequests = "";
    private String noti_list = "";
    private String recipe_list = "";
    private String shared_recipe_list = "";
    private String recipe_shared_with_friends = "";
    private String friends = "";
    //private @ServerTimestamp Date timestamp;

    public Household(String members, String friendRequests, String friends){
        this.members = members;
        this.friendRequests = friendRequests;
        this.friends = friends;
        this.noti_list = "";
        this.recipe_list = "";
        this.shared_recipe_list = "";
        this.recipe_shared_with_friends = "";
    }

    public Household(){

    }

//    public void setHouseholdName(String householdName){
//        this.householdName = householdName;
//    }

   // public String getHouseholdName(){
    //    return householdName;
    //}

    public void setMember(String member){
        this.members = member;
    }

    public void setInvited(String friendRequests){
        this.friendRequests = friendRequests;
    }


    public String getMembers(){
        return members;
    }


}
