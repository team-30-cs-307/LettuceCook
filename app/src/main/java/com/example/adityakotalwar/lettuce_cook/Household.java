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
    private ArrayList<String> members = new ArrayList<>();
    //private @ServerTimestamp Date timestamp;

    public Household(String householdName){
        this.householdName = householdName;
    }

    public Household(){

    }

    public void setHouseholdName(String householdName){
        this.householdName = householdName;
    }

    public String getHouseholdName(){
        return householdName;
    }

    public void addMember(String member){
        members.add(member);
    }

    public ArrayList getMembers(){
        return members;
    }


}
