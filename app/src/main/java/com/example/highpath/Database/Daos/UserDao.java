package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.highpath.Database.Entities.UserEntity;

@Dao
public interface UserDao {

    @Query("SELECT EXISTS(SELECT 1 FROM user LIMIT 1)")
    boolean hasUser();

    @Query("SELECT COUNT(*) FROM user")
    int getUserCount();

    @Insert
    void insert(UserEntity user);

    @Query("SELECT * FROM user LIMIT 1")
    UserEntity getSingleUser();

    @Query("SELECT * FROM user LIMIT 1")
    UserEntity getUser();

}
