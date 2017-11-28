package com.hayk.learnapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.hayk.learnapp.application.AppController;
import com.hayk.learnapp.other.Utils;
import com.hayk.learnapp.rest.Album;
import com.hayk.learnapp.rest.Photo;
import com.hayk.learnapp.rest.User;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by User on 16.11.2017.
 */

public class DBFunctions {
    public static final String DB_UPDATED_ACTION = "db_updated_action";
    public static final String ALBUM_UPDATED_ACTION = "album_updated_action";
    private static DBHelper db;
    private static SQLiteDatabase sqLiteDatabase;
    private Context context;
    private static DBFunctions dbFunctions;
    private List<User> serverUsers;
    private List<Album> serverAlbums;
    private List<Photo> serverPhotos;
    private String userID;
    private String[] albumID;

    public static synchronized DBFunctions getInstance(Context context) {
        if (dbFunctions == null) {
            dbFunctions = new DBFunctions(context);
            sqLiteDatabase = db.getWritableDatabase();
        }
        return dbFunctions;
    }

    private DBFunctions(Context context) {
        this.context = context;
        db = new DBHelper(context);
    }

    public synchronized void updateData() {
        getServerUsers();
    }

    public void updateAlbums(String userID) {
        getServerAlbums(userID);
    }

    public SQLiteDatabase getDatabase(){
        return sqLiteDatabase;
    }

    private void updateUsers() {
        getSqliteDB();
        List<User> databaseUsers = getDatabaseUsers();
        if (databaseUsers.size() == 0) {
            insertUsers(serverUsers);
            return;
        }

        boolean changed;
        ContentValues contentValues = new ContentValues();
        for (User serverUser : serverUsers) {
            changed = false;
            contentValues.clear();
            contentValues.put(DBHelper.USER_REAL_NAME, serverUser.getName());
            contentValues.put(DBHelper.USER_NICK_NAME, serverUser.getUsername());
            contentValues.put(DBHelper.USER_EMAIL, serverUser.getEmail());
            for (User databaseUser : databaseUsers) {
                if (serverUser.getID().equals(databaseUser.getID())) {
                    //update current user from server in database
                    sqLiteDatabase.update(DBHelper.USER_TABLE, contentValues, DBHelper.ID + "=" + databaseUser.getID(), null);
                    databaseUsers.remove(databaseUser);
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                //insert current user from server in database
                contentValues.put(DBHelper.ID, serverUser.getID());
                sqLiteDatabase.insert(DBHelper.USER_TABLE, null, contentValues);
            }
        }

        if (databaseUsers.size() != 0) {
            //delete that users from database,which dont have server
            String[] userIds = new String[databaseUsers.size()];
            for (int i = 0; i < databaseUsers.size(); i++) {
                userIds[i] = databaseUsers.get(i).getID();
            }
            deleteUsers(userIds);
        }
    }

    private void updateAlbums(List<Album> databaseAlbums) {
        getSqliteDB();
        if (databaseAlbums.size() == 0) {
            insertAlbums(serverAlbums);
            return;
        }

        boolean changed;
        ContentValues contentValues = new ContentValues();
        for (Album serverAlbum : serverAlbums) {
            changed = false;
            contentValues.clear();
            contentValues.put(DBHelper.USER_ID, serverAlbum.getUserId());
            contentValues.put(DBHelper.ALBUM_TITLE, serverAlbum.getTitle());
            for (Album databaseAlbum : databaseAlbums) {
                if (serverAlbum.getID().equals(databaseAlbum.getID())) {
                    sqLiteDatabase.update(DBHelper.ALBUM_TABLE, contentValues, DBHelper.ID + "=" + databaseAlbum.getID(), null);
                    databaseAlbums.remove(databaseAlbum);
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                contentValues.put(DBHelper.ID, serverAlbum.getID());
                sqLiteDatabase.insert(DBHelper.ALBUM_TABLE, null, contentValues);
            }
        }

        if (databaseAlbums.size() != 0) {
            String[] albumsIds = new String[databaseAlbums.size()];
            for (int i = 0; i < databaseAlbums.size(); i++) {
                albumsIds[i] = databaseAlbums.get(i).getID();
            }
            deleteAlbums(albumsIds);
        }
    }

    private void updatePhotos(List<Photo> databasePhotos) {
        getSqliteDB();
        if (databasePhotos.size() == 0) {
            insertPhotos(serverPhotos);
            return;
        }

        boolean changed;
        ContentValues contentValues = new ContentValues();
        for (Photo serverPhoto : serverPhotos) {
            changed = false;
            contentValues.clear();
            contentValues.put(DBHelper.ALBUM_ID, serverPhoto.getAlbumId());
            contentValues.put(DBHelper.PHOTO_TITLE, serverPhoto.getTitle());
            contentValues.put(DBHelper.PHOTO_URL, serverPhoto.getUrl());
            contentValues.put(DBHelper.PHOTO_THUMB_URL, serverPhoto.getThumbnailUrl());
            for (Photo databasePhoto : databasePhotos) {
                if (serverPhoto.getID().equals(databasePhoto.getID())) {
                    sqLiteDatabase.update(DBHelper.PHOTO_TABLE, contentValues, DBHelper.ID + "=" + databasePhoto.getID(), null);
                    databasePhotos.remove(databasePhoto);
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                contentValues.put(DBHelper.ID, serverPhoto.getID());
                sqLiteDatabase.insert(DBHelper.PHOTO_TABLE, null, contentValues);
            }
        }

        if (databasePhotos.size() != 0) {
            String[] photosIds = new String[databasePhotos.size()];
            for (int i = 0; i < databasePhotos.size(); i++) {
                photosIds[i] = databasePhotos.get(i).getID();
            }
            deletePhotos(photosIds);
        }
    }

    private void getServerUsers() {
        Call<List<User>> users = AppController.getServerAPI().getUsers();

        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Response<List<User>> response) {
                serverUsers = response.body();
                List<String> usersIds = new ArrayList<>();
                for (User user : serverUsers) {
                    usersIds.add(user.getID());
                }
                getServerAlbums(usersIds);
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(context, "Users list not load", Toast.LENGTH_SHORT).show();
//                plusQueue();
                if (Utils.getInstance(context).getConnectivity()) {
                    getServerUsers();
                }
            }
        });
    }

    private void getServerAlbums(final List<String> userIds) {
        Call<List<Album>> albums = AppController.getServerAPI().getAlbums(userIds);

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(final Response<List<Album>> response) {
                serverAlbums = response.body();
                List<String> albumIds = new ArrayList<>();
                for (Album album : serverAlbums) {
                    albumIds.add(album.getID());
                }
                getServerPhotos(albumIds);
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(context, "Albums list not load", Toast.LENGTH_SHORT).show();
//                plusQueue();
                if (Utils.getInstance(context).getConnectivity()) {
                    getServerAlbums(userIds);
                }
            }
        });
    }

    private void getServerAlbums(final String userId) {
        Call<List<Album>> albums = AppController.getServerAPI().getAlbums(userId);

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(final Response<List<Album>> response) {
                serverAlbums = response.body();
                String[] array = new String[serverAlbums.size()];
                for (int i = 0; i < serverAlbums.size(); i++) {
                    array[i] = serverAlbums.get(i).getID();
                }
                userID = userId;
                getServerPhotos(array);
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(context, "Albums list not load", Toast.LENGTH_SHORT).show();
//                plusQueue();
                if (Utils.getInstance(context).getConnectivity()) {
                    getServerAlbums(userId);
                }
            }
        });
    }

    private void getServerPhotos(final List<String> albumIds) {
        Call<List<Photo>> photos = AppController.getServerAPI().getPhotos(albumIds);

        photos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Response<List<Photo>> response) {
                serverPhotos = response.body();
                new DatabaseUpdateing().execute();
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(context, "Photos list not load", Toast.LENGTH_SHORT).show();
//                plusQueue();
                if (Utils.getInstance(context).getConnectivity()) {
                    getServerPhotos(albumIds);
                }
            }
        });
    }

    private void getServerPhotos(final String[] albumId) {
        Call<List<Photo>> photos = AppController.getServerAPI().getPhotos(albumId);

        photos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Response<List<Photo>> response) {
                serverPhotos = response.body();
                albumID = albumId;
                new AlbumsUpdateing().execute();
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(context, "Photos list not load", Toast.LENGTH_SHORT).show();
//                plusQueue();
                if (Utils.getInstance(context).getConnectivity()) {
                    getServerPhotos(albumId);
                }
            }
        });
    }

    private List<User> getDatabaseUsers() {
        getSqliteDB();
        List<User> usersList = new ArrayList<>();
        Cursor userCursor = sqLiteDatabase.query(DBHelper.USER_TABLE, null, null, null, null, null, null);
        if (userCursor != null && userCursor.moveToFirst()) {
            do {
                usersList.add(new User(userCursor.getString(userCursor.getColumnIndex(DBHelper.ID)), userCursor.getString(userCursor.getColumnIndex(DBHelper.USER_REAL_NAME)), userCursor.getString(userCursor.getColumnIndex(DBHelper.USER_NICK_NAME)), userCursor.getString(userCursor.getColumnIndex(DBHelper.USER_EMAIL))));
            } while (userCursor.moveToNext());
            userCursor.close();
        }
        return usersList;
    }

    private List<Album> getDatabaseAlbums() {
        getSqliteDB();
        List<Album> albumsList = new ArrayList<>();
        Cursor albumCursor = sqLiteDatabase.query(DBHelper.ALBUM_TABLE, null, null, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            do {
                albumsList.add(new Album(albumCursor.getString(albumCursor.getColumnIndex(DBHelper.USER_ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ALBUM_TITLE))));
            } while (albumCursor.moveToNext());
            albumCursor.close();
        }
        return albumsList;
    }

    private List<Album> getDatabaseAlbums(String userId) {
        getSqliteDB();
        List<Album> albumsList = new ArrayList<>();
        Cursor albumCursor = sqLiteDatabase.query(DBHelper.ALBUM_TABLE, null, DBHelper.USER_ID + "=" + userId, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            do {
                albumsList.add(new Album(albumCursor.getString(albumCursor.getColumnIndex(DBHelper.USER_ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ALBUM_TITLE))));
            } while (albumCursor.moveToNext());
            albumCursor.close();
        }
        return albumsList;
    }

    private List<Photo> getDatabasePhotos() {
        getSqliteDB();
        List<Photo> photosList = new ArrayList<>();
        Cursor photosCursor = sqLiteDatabase.query(DBHelper.PHOTO_TABLE, null, null, null, null, null, null);
        if (photosCursor != null && photosCursor.moveToFirst()) {
            do {
                photosList.add(new Photo(photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ALBUM_ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_TITLE)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_URL)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL))));
            } while (photosCursor.moveToNext());
            photosCursor.close();
        }
        return photosList;
    }

    private List<Photo> getDatabasePhotos(String[] albumId) {
        getSqliteDB();
        List<Photo> photosList = new ArrayList<>();
        for (int i =0;i<albumId.length;i++) {
            Cursor photosCursor = sqLiteDatabase.query(DBHelper.PHOTO_TABLE, null, DBHelper.ALBUM_ID + "=" + albumId[i], null, null, null, null);
            if (photosCursor != null && photosCursor.moveToFirst()) {
                do {
                    photosList.add(new Photo(photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ALBUM_ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_TITLE)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_URL)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL))));
                } while (photosCursor.moveToNext());
                photosCursor.close();
            }
        }
        return photosList;
    }

    public Cursor getUsersCursor(){
        getSqliteDB();
        return sqLiteDatabase.query(DBHelper.USER_TABLE,null,null,null,null,null,null);
    }

    public Cursor getAlbumsCursor(String userId){
        getSqliteDB();
        return sqLiteDatabase.query(DBHelper.ALBUM_TABLE,null,DBHelper.USER_ID + "=" + userId,null,null,null,null);
    }

    public Cursor getPhotosCursor(String albumId){
        getSqliteDB();
        return sqLiteDatabase.query(DBHelper.PHOTO_TABLE,null,DBHelper.ALBUM_ID + "=" + albumId,null,null,null,null);
    }

    private void insertUsers(List<User> usersList) {
        getSqliteDB();
        ContentValues contentValues = new ContentValues();
        for (User user : usersList) {
            contentValues.put(DBHelper.ID, user.getID());
            contentValues.put(DBHelper.USER_REAL_NAME, ((User) user).getName());
            contentValues.put(DBHelper.USER_NICK_NAME, ((User) user).getUsername());
            contentValues.put(DBHelper.USER_EMAIL, ((User) user).getEmail());
            sqLiteDatabase.insert(DBHelper.USER_TABLE, null, contentValues);
            contentValues.clear();
        }
    }

    private void insertAlbums(List<Album> albumsList) {
        getSqliteDB();
        ContentValues contentValues = new ContentValues();
        for (Album album : albumsList) {
            contentValues.put(DBHelper.ID, album.getID());
            contentValues.put(DBHelper.USER_ID, ((Album) album).getUserId());
            contentValues.put(DBHelper.ALBUM_TITLE, ((Album) album).getTitle());
            sqLiteDatabase.insert(DBHelper.ALBUM_TABLE, null, contentValues);
            contentValues.clear();
        }
    }

    private void insertPhotos(List<Photo> photosList) {
        getSqliteDB();
        ContentValues contentValues = new ContentValues();
        for (Photo photo : photosList) {
            contentValues.put(DBHelper.ID, photo.getID());
            contentValues.put(DBHelper.ALBUM_ID, ((Photo) photo).getAlbumId());
            contentValues.put(DBHelper.PHOTO_TITLE, ((Photo) photo).getTitle());
            contentValues.put(DBHelper.PHOTO_URL, ((Photo) photo).getUrl());
            contentValues.put(DBHelper.PHOTO_THUMB_URL, ((Photo) photo).getThumbnailUrl());
            sqLiteDatabase.insert(DBHelper.PHOTO_TABLE, null, contentValues);
            contentValues.clear();
        }
    }

    private void deleteUsers(String[] userIds) {
        getSqliteDB();
        sqLiteDatabase.delete(DBHelper.USER_TABLE, DBHelper.ID + "=?", userIds);
        deleteAlbums(userIds);
    }

    private void deleteAlbums(String[] userIds) {
        getSqliteDB();
        Cursor albumsCursor = sqLiteDatabase.query(DBHelper.ALBUM_TABLE, new String[]{DBHelper.ID}, DBHelper.USER_ID + "=?", userIds, null, null, null);
        if (albumsCursor != null && albumsCursor.moveToFirst()) {
            String[] albumsIds = new String[albumsCursor.getCount()];
            do {
                albumsIds[albumsCursor.getPosition()] = albumsCursor.getString(albumsCursor.getColumnIndex(DBHelper.ID));
            } while (albumsCursor.moveToNext());
            albumsCursor.close();
            sqLiteDatabase.delete(DBHelper.ALBUM_TABLE, DBHelper.USER_ID + "=?", userIds);
            deletePhotos(albumsIds);
        }
    }

    private void deletePhotos(String[] albumsIds) {
        getSqliteDB();
        sqLiteDatabase.delete(DBHelper.PHOTO_TABLE, DBHelper.ALBUM_ID + "=?", albumsIds);
    }

    private void getSqliteDB(){
        if(sqLiteDatabase == null){
            db = new DBHelper(context);
            sqLiteDatabase = db.getWritableDatabase();
        }
    }

    private class DatabaseUpdateing extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (serverUsers != null) {
                updateUsers();
                serverUsers = null;
            } else {
                sqLiteDatabase.delete(DBHelper.USER_TABLE, null, null);
            }
            if (serverAlbums != null) {
                updateAlbums(getDatabaseAlbums());
                serverAlbums = null;
            } else {
                sqLiteDatabase.delete(DBHelper.ALBUM_TABLE, null, null);
            }
            if (serverPhotos != null) {
                updatePhotos(getDatabasePhotos());
                serverPhotos = null;
            } else {
                sqLiteDatabase.delete(DBHelper.PHOTO_TABLE, null, null);
            }
            context.sendBroadcast(new Intent(DB_UPDATED_ACTION));
            return null;
        }
    }

    private class AlbumsUpdateing extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (serverAlbums != null) {
                updateAlbums(getDatabaseAlbums(userID));
                serverAlbums = null;
                userID = null;
            }
            if (serverPhotos != null) {
                updatePhotos(getDatabasePhotos(albumID));
                serverPhotos = null;
                albumID = null;
            }
            context.sendBroadcast(new Intent(ALBUM_UPDATED_ACTION));
            return null;
        }
    }
}

