package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.highpath.Database.Entities.SplitEntity;

import java.util.List;

@Dao
public interface SplitDao {

    @Insert
    void insertSplits(List<SplitEntity> splits);

    @Query("SELECT * FROM splits WHERE activity_id = :activityId ORDER BY split_index ASC")
    List<SplitEntity> getSplitsForActivity(String activityId);

    @Query("DELETE FROM splits WHERE activity_id = :activityId")
    void deleteSplitsByActivityId(String activityId);

    @Query("SELECT MIN(split_pace) FROM splits WHERE split_distance >= 1.0")
    Double getFastestKmSplit();
}