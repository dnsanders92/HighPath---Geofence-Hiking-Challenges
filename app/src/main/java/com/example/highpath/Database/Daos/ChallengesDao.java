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

    @Query("SELECT * FROM challenges WHERE completed_summits > 0")
    List<ChallengesEntity> getActiveChallenges();

    @Query("SELECT * FROM challenges WHERE challenge_id = :id LIMIT 1")
    ChallengesEntity getChallengeById(int id);

    @Query("SELECT * FROM challenges WHERE region = :regionName LIMIT 1")
    ChallengesEntity getChallengeByRegion(String regionName);

    @Update
    void updateChallenge(ChallengesEntity challenge);

    @Query("UPDATE challenges SET completed_summits = :count WHERE challenge_id = :challengeId")
    void updateCompletedSummits(int challengeId, int count);

    @Query("UPDATE challenges SET is_completed = 1 WHERE challenge_id = :challengeId")
    void markChallengeCompleted(int challengeId);

    @Query("SELECT * FROM challenges WHERE completed_summits = 0")
    List<ChallengesEntity> getInactiveChallenges();
}