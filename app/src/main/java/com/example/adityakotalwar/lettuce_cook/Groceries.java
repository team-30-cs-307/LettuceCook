package com.example.adityakotalwar.lettuce_cook;

import android.widget.ArrayAdapter;

public class Groceries {
    private String Household, name, userid, Groceries;
    public Groceries(){


    }
    public Groceries(String Household, String userid, String Groceries){
        this.Groceries = Groceries;
        this.Household = Household;
        this.userid = userid;


    }

    public String getHousehold() {
        return Household;
    }

    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

    public String getGroceries() {
        return Groceries;
    }
}
