package com.example.adityakotalwar.lettuce_cook;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notifications {
    public void sendNotification(String Title, String Message, String Household, RequestQueue requestQueue) throws InstantiationException, IllegalAccessException {
        //Our json object will look like
        String URL = "https://fcm.googleapis.com/fcm/send";
        JSONObject object = new JSONObject();

        try{
            String temp = "/topics/"+Household;
            object.put("to",temp);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",Title);
            notificationObj.put("body", Message);
            object.put("notification", notificationObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization", "key=AIzaSyBcVZrRkIsbKBw5B8PklOOZOoCX_3jw_CM");
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
