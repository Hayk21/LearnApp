package com.hayk.learnapp.rest;

import retrofit.Callback;
import retrofit.Response;

/**
 * Created by User on 15.11.2017.
 */

public abstract class MyCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Response<T> response) {
        onMyResponse(response.body(),response);
    }

   abstract void onMyResponse(T responseBody, Response<T> response);
}
