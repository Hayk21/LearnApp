package com.hayk.learnapp.application;

import android.app.Application;

import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.database.RMDatabase;
import com.hayk.learnapp.other.Utils;
import com.hayk.learnapp.rest.RESTHelper;
import com.hayk.learnapp.rest.ServerAPI;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by User on 13.11.2017.
 */

public class AppController extends Application {
    private static ServerAPI serverAPI;
    private RMDatabase rmDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        if (getSharedPreferences(MainActivity.APP_PREF,MODE_PRIVATE).getBoolean(MainActivity.KEY_FOR_LOG,false) && Utils.getInstance(this).getConnectivity()) {
//            DBFunctions.getInstance(this).updateData();
            DBFunctions.getInstance(getApplicationContext()).updateData();
        }
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
