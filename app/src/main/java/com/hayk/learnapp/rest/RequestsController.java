package com.hayk.learnapp.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by User on 13.11.2017.
 */

public class RequestsController {
    private static Context context;
    private RequestQueue requestQueue;
    private static RequestsController requestsController;

    private RequestsController(Context context){
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized RequestsController getInstance(Context context){
        if(requestsController == null){
            requestsController = new RequestsController(context);
        }
        return requestsController;
    }

    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }
}
