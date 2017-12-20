package com.hayk.learnapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hayk.learnapp.rest.Album;
import com.hayk.learnapp.rest.AlbumDao;
import com.hayk.learnapp.rest.Photo;
import com.hayk.learnapp.rest.PhotoDao;
import com.hayk.learnapp.rest.User;
import com.hayk.learnapp.rest.UserDao;

/**
 * Created by User on 20.12.2017.
 */

@Database(entities = {User.class, Album.class, Photo.class},version = 1)
public abstract class RMDatabase extends RoomDatabase{

    private static RMDatabase rmDatabase;

    public abstract UserDao userDao();
    public abstract AlbumDao albumDao();
    public abstract PhotoDao photoDao();

    public static RMDatabase getInstance(Context context){
        if(rmDatabase == null){
            rmDatabase = Room.databaseBuilder(context.getApplicationContext(),RMDatabase.class,"room_db").allowMainThreadQueries().build();
        }
        return rmDatabase;
    }
}
