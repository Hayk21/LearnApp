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
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getUsers();

    @Insert
    void insertUsers(List<User> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateUsers(List<User> users);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM User")
    void deleteAllUsers();
}
