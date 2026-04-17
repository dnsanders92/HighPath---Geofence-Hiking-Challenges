package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.highpath.Database.Entities.ActivityEntity;

import java.util.List;

@Dao
public interface ActivityDao {

    // Insert a single activity (matches MainActivity)
    @Insert
    void insertActivity(ActivityEntity activity);

    // Optional: insert multiple activities at once
    @Insert
    void insertActivities(List<ActivityEntity> activities);

    // Get all activities (ordered newest first)
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    List<ActivityEntity> getAllActivities();

    // Optional: get one activity by ID
    @Query("SELECT * FROM activities WHERE activity_id = :id LIMIT 1")
    ActivityEntity getActivityById(String id);

    // Optional: delete all (useful for testing)
    @Query("DELETE FROM activities")
    void deleteAllActivities();

    // Longest distance ever recorded
    @Query("SELECT MAX(distance) FROM activities")
    Double getLongestDistance();

    // Highest elevation gain ever recorded
    @Query("SELECT MAX(elevation_gain) FROM activities")
    Double getHighestElevationGain();

}
