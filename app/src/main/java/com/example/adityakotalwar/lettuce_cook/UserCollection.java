package com.example.adityakotalwar.lettuce_cook;

public class UserCollection {

    private String username, email, household, userId;

    public UserCollection(){

    }

    public UserCollection(String username, String email, String userId, String household){
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getUsername(){
        return username;
    }
    public  String getEmail(){
        return email;
    }
    public void setHousehold(String household){
        this.household = household;
    }
    public String getHousehold(){
        return household;
    }
    public String getUserId(){
        return userId;
    }
}

//The User module stores the User details
