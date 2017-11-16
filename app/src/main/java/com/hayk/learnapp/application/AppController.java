package com.hayk.learnapp.application;

import android.app.Application;

import com.hayk.learnapp.rest.RESTHelper;
import com.hayk.learnapp.rest.ServerAPI;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by User on 13.11.2017.
 */

public class AppController extends Application {
    private static ServerAPI serverAPI;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static synchronized ServerAPI getServerAPI(){
        if(serverAPI == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RESTHelper.getBaseURL())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            serverAPI = retrofit.create(ServerAPI.class);
        }
        return serverAPI;
    }


}
