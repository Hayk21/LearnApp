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
public interface AlbumDao {

    @Query("SELECT * FROM Album")
    List<Album> getAlbums();

    @Query("SELECT * FROM Album WHERE userId = :user")
    List<Album> getAlbumsByUserId(Long user);

    @Insert
    void insertAlbums(List<Album> albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAlbums(List<Album> albums);

    @Delete
    void deleteAlbum(Album album);

    @Query("DELETE FROM Album")
    void deleteAllAlbums();
}
