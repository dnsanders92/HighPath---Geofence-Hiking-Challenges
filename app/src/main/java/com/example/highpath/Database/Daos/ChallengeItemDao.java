package com.example.highpath.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.highpath.Database.Entities.ChallengeItemEntity;

import java.util.List;

@Dao
public interface ChallengeItemDao {

    @Insert
    void insertItem(ChallengeItemEntity item);

    @Insert
    void insertItems(List<ChallengeItemEntity> items);

    @Update
    void updateItem(ChallengeItemEntity item);

    @Query("SELECT * FROM challenge_items WHERE challenge_id = :challengeId")
    List<ChallengeItemEntity> getItemsForChallenge(int challengeId);

    @Query("SELECT * FROM challenge_items WHERE challenge_id = :challengeId AND is_completed = 1")
    List<ChallengeItemEntity> getCompletedItemsForChallenge(int challengeId);

    @Query("SELECT COUNT(*) FROM challenge_items WHERE challenge_id = :challengeId AND is_completed = 1")
    int getCompletedCountForChallenge(int challengeId);

    @Query("SELECT COUNT(*) FROM challenge_items WHERE challenge_id = :challengeId")
    int getTotalCountForChallenge(int challengeId);

    @Query("UPDATE challenge_items SET is_completed = 1 WHERE summit_id = :summitId")
    void markSummitCompleted(int summitId);

    @Query("SELECT * FROM challenge_items WHERE summit_id = :summitId")
    List<ChallengeItemEntity> getItemsBySummitId(int summitId);
}