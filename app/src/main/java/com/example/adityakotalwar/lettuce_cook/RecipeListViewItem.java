package com.example.adityakotalwar.lettuce_cook;

public class RecipeListViewItem {

    String householdName;
    String time;
    String recipeName;
    String recipeSummary;
    String id;

    public RecipeListViewItem(String id,String householdName, String time, String recipeName, String recipeSummary){
        this.householdName = householdName;
        this.time = time;
        this.recipeName = recipeName;
        this.recipeSummary = recipeSummary;
        this.id = id;
    }

}
