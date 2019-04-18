package com.example.adityakotalwar.lettuce_cook;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomAdapterGrocery extends ArrayAdapter<RecipeListViewItem> implements View.OnClickListener {

    private ArrayList<RecipeListViewItem> recipeSet;
    Context mContext;


    private static class ViewHolder{
        TextView groceryName;
        TextView groceryDesc;

    }

    public CustomAdapterGrocery(ArrayList<RecipeListViewItem> recipeSet, Context context){
        super(context, R.layout.listview_recipe_item, recipeSet);
        this.recipeSet = recipeSet;
        this.mContext = context;
    }
    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        RecipeListViewItem recipe = (RecipeListViewItem) obj;

//        switch(v.getId()){
//
//
//        }

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        RecipeListViewItem recipe = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_grocery_item, parent, false);

            viewHolder.groceryName = (TextView) convertView.findViewById(R.id.recipeName);
            viewHolder.groceryDesc = (TextView) convertView.findViewById(R.id.recipeDesc);

            result = convertView;
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.groceryName.setText(recipe.id);
        viewHolder.groceryDesc.setText(recipe.householdName);


        return convertView;
    }


}


