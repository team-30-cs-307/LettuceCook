package com.example.adityakotalwar.lettuce_cook.Remote;

import com.example.adityakotalwar.lettuce_cook.Model.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);
}
