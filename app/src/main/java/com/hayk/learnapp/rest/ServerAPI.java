package com.hayk.learnapp.rest;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by User on 10.11.2017.
 */

public interface ServerAPI {
    @GET("users")
    Call<List<User>> getUsers();
}
