package com.example.adityakotalwar.lettuce_cook;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;

import javax.annotation.Nullable;

class CustomAdapterSharedRecipe extends ArrayAdapter<RecipeListViewItem> implements View.OnClickListener {

    final FirebaseFirestore db =  FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayList<RecipeListViewItem> recipeSet;
    Context mContext;
    String house;

    private static class ViewHolder{
        TextView recipeName;
        TextView recipeSummary;
        TextView householdName;
        TextView time;
        ImageButton star_button;
        ImageButton trash_button;
    }

    public CustomAdapterSharedRecipe(ArrayList<RecipeListViewItem> recipeSet, Context context, String house){
        super(context, R.layout.listview_recipe_item, recipeSet);
        this.recipeSet = recipeSet;
        this.mContext = context;
        this.house = house;
    }
    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        final RecipeListViewItem recipe = (RecipeListViewItem) obj;

//        switch(v.getId()){
//            case R.id.star_button:
//
//
//        }

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final RecipeListViewItem recipe = getItem(position);
        final ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_recipe_item, parent, false);

            viewHolder.householdName = (TextView) convertView.findViewById(R.id.householdName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.timeOfPost);
            viewHolder.recipeName = (TextView) convertView.findViewById(R.id.recipeName);
            viewHolder.recipeSummary = (TextView) convertView.findViewById(R.id.recipeSummary);
            viewHolder.star_button = (ImageButton) convertView.findViewById(R.id.star_button);
            viewHolder.trash_button = (ImageButton) convertView.findViewById(R.id.trash_button);

            db.collection("Household").document(house).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    String rec = documentSnapshot.getString("recipe_list");
                    if(rec.contains(recipe.id)){
                        viewHolder.star_button.setBackgroundResource(R.drawable.star_saved_button);
                    }
//                    rec = documentSnapshot.getString("shared_recipe_list");
//                    if(rec.contains(recipe.id)){
//
//                    }
                }

            });

            viewHolder.star_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference dr = db.collection("Household").document(house);
                    dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String rec = documentSnapshot.getString("recipe_list");
                            if(!rec.contains(recipe.id)){
                                rec += recipe.id + " ";
                                dr.update("recipe_list", rec);
                            }
                            viewHolder.star_button.setBackgroundResource(R.drawable.star_saved_button);
                        }
                    });

                }
            });

            viewHolder.trash_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final DocumentReference dr = db.collection("Household").document(house);

                    dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String recipes = documentSnapshot.getString("shared_recipe_list");
                            recipes = recipes.replace(recipe.id+" ", "");
                            dr.update("shared_recipe_list", recipes);

                        }
                    });
                }
            });

            result = convertView;
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.recipeName.setText(recipe.recipeName);
        viewHolder.time.setText(recipe.time);

        viewHolder.recipeSummary.setText(recipe.recipeSummary);
        viewHolder.householdName.setText(recipe.householdName);
        return convertView;
    }


}


