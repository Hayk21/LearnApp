package com.hayk.learnapp.rest;

/**
 * Created by User on 13.11.2017.
 */

public class RESTHelper {
    private static final String baseURL = "https://jsonplaceholder.typicode.com";
    private static String users = "/users";
    private static String albums = "/albums";

    public static String getUsers(){
        return baseURL + users;
    }

    public static String getAlbums(){
        return baseURL + albums;
    }
}
