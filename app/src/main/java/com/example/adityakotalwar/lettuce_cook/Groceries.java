package com.example.adityakotalwar.lettuce_cook;

import android.widget.ArrayAdapter;

public class Groceries {
    private String userid, description, status;
    public Groceries(){


    }
    public Groceries(String userid, String description, String status){
        this.userid = userid;
        this.description = description;
        this.status = status;


    }
    public Groceries(String userid, String status){
        this.status = status;
        this.userid = userid;

    }


    public String getUserid() {
        return userid;
    }
    public String getStatus(){
        return status;
    }
    public String getDescription(){
        return description;
    }
}
