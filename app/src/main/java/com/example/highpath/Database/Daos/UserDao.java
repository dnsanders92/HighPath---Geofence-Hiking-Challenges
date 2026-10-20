package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy; // Added this import
import androidx.room.Query;
import androidx.room.Update;

import com.example.highpath.Database.Entities.UserEntity;

@Dao
public interface UserDao {

    @Query("SELECT EXISTS(SELECT 1 FROM user LIMIT 1)")
    boolean hasUser();

    @Query("SELECT COUNT(*) FROM user")
    int getUserCount();

    // This tells Room: "If a user with this ID exists, overwrite their stats."
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    // Alternative: You can also use a dedicated Update method
    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM user LIMIT 1")
    UserEntity getSingleUser();

    @Query("SELECT * FROM user LIMIT 1")
    UserEntity getUser();

    @Query("SELECT user_id FROM user LIMIT 1")
    int getUserId();

    // Cross Table Queries
    @Query("SELECT MAX(distance) FROM activities")
    Double getLongestDistance();

    @Query("SELECT MAX(elevation_gain) FROM activities")
    Double getHighestElevationGain();
}