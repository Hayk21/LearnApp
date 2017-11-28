package com.hayk.learnapp.other;

import android.content.Context;
import android.net.ConnectivityManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by User on 23.11.2017.
 */

public class Utils {
    private static Context context;
    private static Utils utils;

    public static synchronized Utils getInstance(Context cont){
        context = cont;
        if(utils == null){
            utils = new Utils();
        }
        return utils;
    }

    public boolean getConnectivity(){
        if (((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null){
            return true;
        }
        return false;
    }
}
