package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.highpath.Database.Entities.TrackPointEntity;

import java.util.List;

@Dao
public interface TrackPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackPoints(List<TrackPointEntity> trackPoints);

    @Query("SELECT * FROM track_points WHERE trail_id = :trailId ORDER BY point_index ASC")
    List<TrackPointEntity> getPointsForTrail(String trailId);

    @Query("DELETE FROM track_points WHERE trail_id = :trailId")
    void deletePointsForTrail(String trailId);
}