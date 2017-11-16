package com.hayk.learnapp.rest;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by User on 10.11.2017.
 */

public interface ServerAPI {
    @GET("users")
    Call<List<User>> getUsers();

    @GET("albums")
    Call<List<Album>> getAlbums(@Query("userId") int userId);

    @GET("photos")
    Call<List<Photo>> getPhotos(@Query("albumId") int albumId);

//    @GET("photos")
//    void getPhotos(@Query("albumId")int albumId, Callback<List<Photo>> callback);
}
