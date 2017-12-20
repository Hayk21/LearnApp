package com.hayk.learnapp.rest;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by User on 13.11.2017.
 */

@Entity(foreignKeys = @ForeignKey(entity = User.class,parentColumns = "id",childColumns = "userId",onDelete = CASCADE))
public class Album {
    @PrimaryKey
    private long id;

    private long userId;
    private String title;


    public Album(long userId, long id, String title) {
        this.userId = userId;
        this.id = id;
        this.title = title;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
