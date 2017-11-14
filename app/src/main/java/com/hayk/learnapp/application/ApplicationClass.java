package com.hayk.learnapp.application;

import android.app.Application;

import com.hayk.learnapp.rest.ServerAPI;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by User on 13.11.2017.
 */

public class ApplicationClass extends Application {
    private ServerAPI serverAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serverAPI = retrofit.create(ServerAPI.class);
    }

    public ServerAPI getServerAPI(){
        return serverAPI;
    }


}
