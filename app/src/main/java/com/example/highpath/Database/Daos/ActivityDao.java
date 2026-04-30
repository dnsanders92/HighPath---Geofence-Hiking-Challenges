package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.highpath.Database.Entities.ActivityEntity;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActivityEntity activity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivities(List<ActivityEntity> activities);

    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    List<ActivityEntity> getAllActivities();

    @Query("SELECT * FROM activities WHERE activity_id = :id LIMIT 1")
    ActivityEntity getActivityById(String id);

    @Query("DELETE FROM activities")
    void deleteAllActivities();

    @Update
    void update(ActivityEntity activity);

    @Delete
    void deleteActivity(ActivityEntity activity);

    @Query("SELECT MAX(distance) FROM activities")
    Double getLongestDistance();

    @Query("SELECT MAX(elevation_gain) FROM activities")
    Double getHighestElevationGain();
}