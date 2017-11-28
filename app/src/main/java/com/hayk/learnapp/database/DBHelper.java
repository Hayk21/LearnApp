package com.hayk.learnapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 16.11.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "learnapp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String USER_TABLE = "UserTable";
    public static final String ID = "_id";
    public static final String USER_REAL_NAME = "UserName";
    public static final String USER_NICK_NAME = "Nickname";
    public static final String USER_EMAIL = "UserEmail";
    public static final String ALBUM_TABLE = "AlbumTable";
    public static final String USER_ID = "UserId";
    public static final String ALBUM_TITLE= "AlbumTitle";
    public static final String PHOTO_TABLE = "PhotoTable";
    public static final String ALBUM_ID = "AlbumId";
    public static final String PHOTO_TITLE = "PhotoTitle";
    public static final String PHOTO_URL = "PhotoURL";
    public static final String PHOTO_THUMB_URL = "PhotoThumbnail";

    private static final String CREATE_USERS_TABLE = "Create table " + USER_TABLE +
            " (" + ID + " integer primary key," + USER_REAL_NAME + " text," + USER_NICK_NAME + " text," + USER_EMAIL + " text);";

    private static final String CREATE_ALBUMS_TABLE = "Create table " + ALBUM_TABLE + " (" + ID + " integer primary key," + USER_ID + " text," + ALBUM_TITLE + " text);";

    private static final String CREATE_PHOTOS_TABLE = "Create table " + PHOTO_TABLE +
            " (" + ID + " integer primary key," + ALBUM_ID + " text," + PHOTO_TITLE + " text," + PHOTO_URL + " text," + PHOTO_THUMB_URL + " text);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_ALBUMS_TABLE);
        sqLiteDatabase.execSQL(CREATE_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
