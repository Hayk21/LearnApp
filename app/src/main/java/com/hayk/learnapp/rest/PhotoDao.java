package com.hayk.learnapp.rest;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by User on 20.12.2017.
 */

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM Photo")
    List<Photo> getPhotos();

    @Query("SELECT * FROM Photo WHERE albumId = :album")
    List<Photo> getPhotosByAlbumId(Long album);

    @Insert
    void insertPhotos(List<Photo> photos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAlbums(List<Photo> photos);

    @Delete
    void deletePhoto(Photo photo);

    @Query("DELETE FROM Photo")
    void deleteAllPhotos();
}
