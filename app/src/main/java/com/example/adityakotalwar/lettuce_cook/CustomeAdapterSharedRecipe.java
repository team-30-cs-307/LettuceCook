package com.example.adityakotalwar.lettuce_cook;

import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

class CustomAdapterSharedRecipe extends ArrayAdapter<RecipeListViewItem> implements View.OnClickListener {

    final FirebaseFirestore db =  FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayList<RecipeListViewItem> recipeSet;
    Context mContext;

    private static class ViewHolder{
        TextView recipeName;
        TextView recipeSummary;
        TextView householdName;
        TextView time;
        ImageButton star_button;
    }

    public CustomAdapterSharedRecipe(ArrayList<RecipeListViewItem> recipeSet, Context context){
        super(context, R.layout.listview_recipe_item, recipeSet);
        this.recipeSet = recipeSet;
        this.mContext = context;
    }
    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        final RecipeListViewItem recipe = (RecipeListViewItem) obj;

        switch(v.getId()){
            case R.id.star_button:
                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String house = documentSnapshot.getString("household");
                        final DocumentReference dr = db.collection("Household").document(house);
                        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String rec = documentSnapshot.getString("SavedRecipe");
                                if(!rec.contains("recipe.id")){
                                    rec += recipe.id + " ";
                                    dr.update("SavedRecipe", rec);
                                }

                            }
                        });
                    }
                });

        }

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
            convertView = inflater.inflate(R.layout.listview_recipe_item, parent, false);

            viewHolder.householdName = (TextView) convertView.findViewById(R.id.householdName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.timeOfPost);
            viewHolder.recipeName = (TextView) convertView.findViewById(R.id.recipeName);
            viewHolder.recipeSummary = (TextView) convertView.findViewById(R.id.recipeSummary);
            viewHolder.star_button = (ImageButton) convertView.findViewById(R.id.star_button);

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
        viewHolder.star_button.setVisibility(View.INVISIBLE);
        return convertView;
    }


}


