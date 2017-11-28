package com.hayk.learnapp.rest;

/**
 * Created by User on 13.11.2017.
 */

public class Album {
    private String userId;
    private String id;
    private String title;

    public Album(){

    }

    public Album(String userId, String id, String title) {
        this.userId = userId;
        this.id = id;
        this.title = title;
    }


    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
