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

    /**
     * Inserts a single trail into the database.
     * Replaces the old data if the trail_id already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrail(TrailEntity trail);

    /**
     * Inserts a specific GPS track point for a trail.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackPoint(TrackPointEntity trackPoint);

    /**
     * Retrieves all available trails.
     * Used for populating the list in ExploreFragment.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackPoints(List<TrackPointEntity> trackPoints);

    @Query("SELECT * FROM trails")
    List<TrailEntity> getAllTrails();

    /**
     * Retrieves a specific trail's details using its ID.
     * Used in TrailSummaryFragment to show name, distance, and ascent.
     */
    @Query("SELECT * FROM trails WHERE trail_id = :id LIMIT 1")
    TrailEntity getTrailById(String id);

    /**
     * Retrieves all GPS points for a specific trail, ordered by their sequence.
     * Essential for drawing the polyline on a Google Map or Mapbox view.
     */
    @Query("SELECT * FROM track_points WHERE trail_id = :trailId ORDER BY point_index ASC")
    List<TrackPointEntity> getPointsForTrail(String trailId);
}