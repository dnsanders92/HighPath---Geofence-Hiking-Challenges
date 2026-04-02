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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChallenge(ChallengesEntity challenge);

    @Query("SELECT * FROM challenges")
    List<ChallengesEntity> getAllChallenges();

    @Update
    void updateChallenge(ChallengesEntity challenge);

    @Query("SELECT * FROM challenges WHERE challenge_id = :id")
    ChallengesEntity getChallengeById(int id);

    @Query("SELECT * FROM challenges WHERE challenge_name = :name LIMIT 1")
    ChallengesEntity getChallengeByName(String name);

    @Query("DELETE FROM challenges")
    void deleteAllChallenges();
}