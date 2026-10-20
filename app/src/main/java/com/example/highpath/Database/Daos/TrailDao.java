package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.highpath.Database.Entities.TrackPointEntity;
import com.example.highpath.Database.Entities.TrailEntity;

import java.util.List;

@Dao
public interface TrailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrail(TrailEntity trail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackPoint(TrackPointEntity trackPoint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackPoints(List<TrackPointEntity> trackPoints);

    @Query("SELECT * FROM trails")
    List<TrailEntity> getAllTrails();

    @Query("SELECT * FROM trails WHERE trail_id = :id LIMIT 1")
    TrailEntity getTrailById(String id);

    @Query("SELECT * FROM track_points WHERE trail_id = :trailId ORDER BY point_index ASC")
    List<TrackPointEntity> getPointsForTrail(String trailId);
}