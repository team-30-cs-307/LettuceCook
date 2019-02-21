package com.example.adityakotalwar.lettuce_cook;

public class UserCollection {

    private String username, email, household, userId, invited;

    public UserCollection(){

    }

    public UserCollection(String username, String email, String userId, String household, String invited){
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.household = household;
        this.invited = invited;
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
    public String getInvited(){return invited;}
    public void setInvited(String household){
        this.household = household;
    }


}

//The User module stores the User details
