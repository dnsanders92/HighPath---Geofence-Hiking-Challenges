package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.highpath.Database.Entities.ChallengesEntity;

import java.util.List;

@Dao
public interface ChallengesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChallenge(ChallengesEntity challenge);

    @Query("SELECT * FROM challenges")
    List<ChallengesEntity> getAllChallenges();

    @Query("SELECT * FROM challenges WHERE challenge_id = :id LIMIT 1")
    ChallengesEntity getChallengeById(int id);

    // --- ADD THESE TWO METHODS BELOW ---

    /**
     * Finds the challenge row that matches the summit's region (e.g., "Ulster").
     */
    @Query("SELECT * FROM challenges WHERE region = :regionName LIMIT 1")
    ChallengesEntity getChallengeByRegion(String regionName);

    /**
     * Saves the updated 'completed_summits' count back to the database.
     */
    @Update
    void updateChallenge(ChallengesEntity challenge);
}