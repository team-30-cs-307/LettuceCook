package com.example.adityakotalwar.lettuce_cook;

import com.example.adityakotalwar.lettuce_cook.Remote.IGoogleAPIService;
import com.example.adityakotalwar.lettuce_cook.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static IGoogleAPIService getGoogleAPIService(){

        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);

    }

}
