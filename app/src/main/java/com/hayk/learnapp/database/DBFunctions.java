package com.hayk.learnapp.database;

import android.content.Context;
import android.content.Intent;
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
    private Context context;
    private static DBFunctions dbFunctions;
    private static RMDatabase rmDatabase;
    private List<User> serverUsers;
    private List<Album> serverAlbums;
    private List<Photo> serverPhotos;
    private Long userID;
    private Long[] albumID;

    public static synchronized DBFunctions getInstance(Context context) {
        if (dbFunctions == null) {
            dbFunctions = new DBFunctions(context);
        }
        return dbFunctions;
    }

    private DBFunctions(Context context) {
        this.context = context;
        rmDatabase = RMDatabase.getInstance(context);
    }

    public synchronized void updateData() {
        getServerUsers();
    }

    public void updateAlbums(long userID) {
        getServerAlbums(userID);
    }


    private void updateUsers() {
        List<User> databaseUsers = getDatabaseUsers();
        if (databaseUsers.size() == 0) {
            rmDatabase.userDao().insertUsers(serverUsers);
            return;
        }
        boolean changed;
        rmDatabase.userDao().insertOrUpdateUsers(serverUsers);
        for (User databaseUser : databaseUsers) {
            changed = false;
            for (User serverUser : serverUsers) {
                if (serverUser.getId() == databaseUser.getId()) {
                    //update current user from server in database
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                //insert current user from server in database
                rmDatabase.userDao().deleteUser(databaseUser);
            }
        }
//        List<Album> albums = daoSession.getAlbumDao().queryBuilder().where(AlbumDao.Properties.UserId.eq(userIds)).list();
//        daoSession.getAlbumDao().deleteInTx(albums);
//        List<Long> albumsIds = new ArrayList<>();
//        for (Album album:albums){
//            albumsIds.add(album.getId());
//        }
//        List<Photo> photos = daoSession.getPhotoDao().queryBuilder().where(PhotoDao.Properties.AlbumId.eq(albumsIds)).list();
//        daoSession.getPhotoDao().deleteInTx(photos);
    }

    private void updateAlbums(List<Album> databaseAlbums) {
        if (databaseAlbums.size() == 0) {
            rmDatabase.albumDao().insertAlbums(serverAlbums);
            return;
        }

        boolean changed;
        rmDatabase.albumDao().insertOrUpdateAlbums(serverAlbums);
        for (Album databaseAlbum :databaseAlbums) {
            changed = false;
            for (Album serverAlbum : serverAlbums) {
                if (serverAlbum.getId() == (databaseAlbum.getId())) {
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                rmDatabase.albumDao().deleteAlbum(databaseAlbum);
            }
        }
//        daoSession.getPhotoDao().deleteInTx(daoSession.getPhotoDao().queryBuilder().where(PhotoDao.Properties.AlbumId.eq(albumsIds)).list());
    }

    private void updatePhotos(List<Photo> databasePhotos) {
        if (databasePhotos.size() == 0) {
            rmDatabase.photoDao().insertPhotos(serverPhotos);
            return;
        }

        boolean changed;
        rmDatabase.photoDao().insertOrUpdateAlbums(serverPhotos);
        for (Photo databasePhoto : databasePhotos) {
            changed = false;
            for (Photo serverPhoto : serverPhotos) {
                if (serverPhoto.getID() == databasePhoto.getID()) {
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                rmDatabase.photoDao().deletePhoto(databasePhoto);
            }
        }
    }

    private void getServerUsers() {
        Call<List<User>> users = AppController.getServerAPI().getUsers();

        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Response<List<User>> response) {
                serverUsers = response.body();
                List<Long> usersIds = new ArrayList<>();
                for (User user : serverUsers) {
                    usersIds.add(user.getId());
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

    private void getServerAlbums(final List<Long> userIds) {
        Call<List<Album>> albums = AppController.getServerAPI().getAlbums(userIds);

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(final Response<List<Album>> response) {
                serverAlbums = response.body();
                List<Long> albumIds = new ArrayList<>();
                for (Album album : serverAlbums) {
                    albumIds.add(album.getId());
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

    private void getServerAlbums(final Long userId) {
        Call<List<Album>> albums = AppController.getServerAPI().getAlbums(userId);

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(final Response<List<Album>> response) {
                serverAlbums = response.body();
                Long[] array = new Long[serverAlbums.size()];
                for (int i = 0; i < serverAlbums.size(); i++) {
                    array[i] = serverAlbums.get(i).getId();
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

    private void getServerPhotos(final List<Long> albumIds) {
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

    private void getServerPhotos(final Long[] albumId) {
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


//    private List<Album> getDatabaseAlbums(String userId) {
//        getSqliteDB();
//        List<Album> albumsList = new ArrayList<>();
//        Cursor albumCursor = sqLiteDatabase.query(DBHelper.ALBUM_TABLE, null, DBHelper.USER_ID + "=" + userId, null, null, null, null);
//        if (albumCursor != null && albumCursor.moveToFirst()) {
//            do {
//                albumsList.add(new Album(albumCursor.getString(albumCursor.getColumnIndex(DBHelper.USER_ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ID)), albumCursor.getString(albumCursor.getColumnIndex(DBHelper.ALBUM_TITLE))));
//            } while (albumCursor.moveToNext());
//            albumCursor.close();
//        }
//        return albumsList;
//    }
//
//    private List<Photo> getDatabasePhotos() {
//        getSqliteDB();
//        List<Photo> photosList = new ArrayList<>();
//        Cursor photosCursor = sqLiteDatabase.query(DBHelper.PHOTO_TABLE, null, null, null, null, null, null);
//        if (photosCursor != null && photosCursor.moveToFirst()) {
//            do {
//                photosList.add(new Photo(photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ALBUM_ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_TITLE)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_URL)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL))));
//            } while (photosCursor.moveToNext());
//            photosCursor.close();
//        }
//        return photosList;
//    }
//
//    private List<Photo> getDatabasePhotos(String[] albumId) {
//        getSqliteDB();
//        List<Photo> photosList = new ArrayList<>();
//        for (int i =0;i<albumId.length;i++) {
//            Cursor photosCursor = sqLiteDatabase.query(DBHelper.PHOTO_TABLE, null, DBHelper.ALBUM_ID + "=" + albumId[i], null, null, null, null);
//            if (photosCursor != null && photosCursor.moveToFirst()) {
//                do {
//                    photosList.add(new Photo(photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ALBUM_ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.ID)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_TITLE)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_URL)), photosCursor.getString(photosCursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL))));
//                } while (photosCursor.moveToNext());
//                photosCursor.close();
//            }
//        }
//        return photosList;
//    }
//
//    public Cursor getUsersCursor(){
//        getSqliteDB();
//        return sqLiteDatabase.query(DBHelper.USER_TABLE,null,null,null,null,null,null);
//    }
//
//    public Cursor getAlbumsCursor(String userId){
//        getSqliteDB();
//        return sqLiteDatabase.query(DBHelper.ALBUM_TABLE,null,DBHelper.USER_ID + "=" + userId,null,null,null,null);
//    }
//
//    public Cursor getPhotosCursor(String albumId){
//        getSqliteDB();
//        return sqLiteDatabase.query(DBHelper.PHOTO_TABLE,null,DBHelper.ALBUM_ID + "=" + albumId,null,null,null,null);
//    }
//
//    private void insertPhotos(List<Photo> photosList) {
//        getSqliteDB();
//        ContentValues contentValues = new ContentValues();
//        for (Photo photo : photosList) {
//            contentValues.put(DBHelper.ID, photo.getID());
//            contentValues.put(DBHelper.ALBUM_ID, ((Photo) photo).getAlbumId());
//            contentValues.put(DBHelper.PHOTO_TITLE, ((Photo) photo).getTitle());
//            contentValues.put(DBHelper.PHOTO_URL, ((Photo) photo).getUrl());
//            contentValues.put(DBHelper.PHOTO_THUMB_URL, ((Photo) photo).getThumbnailUrl());
//            sqLiteDatabase.insert(DBHelper.PHOTO_TABLE, null, contentValues);
//            contentValues.clear();
//        }
//    }
//
//    private void deleteUsers(String[] userIds) {
//        getSqliteDB();
//        sqLiteDatabase.delete(DBHelper.USER_TABLE, DBHelper.ID + "=?", userIds);
//        deleteAlbums(userIds);
//    }
//
//    private void deleteAlbums(String[] userIds) {
//        getSqliteDB();
//        Cursor albumsCursor = sqLiteDatabase.query(DBHelper.ALBUM_TABLE, new String[]{DBHelper.ID}, DBHelper.USER_ID + "=?", userIds, null, null, null);
//        if (albumsCursor != null && albumsCursor.moveToFirst()) {
//            String[] albumsIds = new String[albumsCursor.getCount()];
//            do {
//                albumsIds[albumsCursor.getPosition()] = albumsCursor.getString(albumsCursor.getColumnIndex(DBHelper.ID));
//            } while (albumsCursor.moveToNext());
//            albumsCursor.close();
//            sqLiteDatabase.delete(DBHelper.ALBUM_TABLE, DBHelper.USER_ID + "=?", userIds);
//            deletePhotos(albumsIds);
//        }
//    }
//
//    private void deletePhotos(String[] albumsIds) {
//        getSqliteDB();
//        sqLiteDatabase.delete(DBHelper.PHOTO_TABLE, DBHelper.ALBUM_ID + "=?", albumsIds);
//    }

    public List<User> getDatabaseUsers(){
        return rmDatabase.userDao().getUsers();
    }

    public List<Album> getDatabaseAlbums(){
        return rmDatabase.albumDao().getAlbums();
    }

    public List<Album> getDatabaseAlbums(long userId){
        return rmDatabase.albumDao().getAlbumsByUserId(userId);
    }

    public List<Photo> getDatabasePhotos(){
        return rmDatabase.photoDao().getPhotos();
    }

    public List<Photo> getDatabasePhotos(long albumId){
        return rmDatabase.photoDao().getPhotosByAlbumId(albumId);
    }

    public void clearDatabase(){
        rmDatabase.userDao().deleteAllUsers();
    }
//
//    private void getSqliteDB(){
//        if(sqLiteDatabase == null){
//            db = new DBHelper(context);
//            sqLiteDatabase = db.getWritableDatabase();
//        }
//    }

    private class DatabaseUpdateing extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (serverUsers != null) {
                updateUsers();
                serverUsers = null;
            }
            if (serverAlbums != null) {
                updateAlbums(getDatabaseAlbums());
                serverAlbums = null;
            }
            if (serverPhotos != null) {
                updatePhotos(getDatabasePhotos());
                serverPhotos = null;
            }
            context.sendBroadcast(new Intent(DB_UPDATED_ACTION));
            return null;
        }
    }

    private class AlbumsUpdateing extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (serverAlbums != null) {
                updateAlbums(rmDatabase.albumDao().getAlbumsByUserId(userID));
                serverAlbums = null;
                userID = null;
            }
            if (serverPhotos != null) {
                for (int i=0;i<albumID.length;i++){
                    updatePhotos(rmDatabase.photoDao().getPhotosByAlbumId(albumID[i]));
                }
                serverPhotos = null;
                albumID = null;
            }
            context.sendBroadcast(new Intent(ALBUM_UPDATED_ACTION));
            return null;
        }
    }
}

