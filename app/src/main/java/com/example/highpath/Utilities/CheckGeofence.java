package com.example.highpath.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Calculations.CalculateDistance;
import com.example.highpath.Database.Entities.ChallengeItemEntity;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.Supabase.InsertChallengeItem;
import com.example.highpath.Models.GPXData;

import java.util.List;

public class CheckGeofence {

    private final double geofenceRadius = 15.0;

    public void checkGeofence(Context context, GPXData gpxData) {
        if (context == null || gpxData == null) return;

        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(context);
            List<SummitsEntity> allSummits = db.summitsDao().getAllSummits();

            if (allSummits == null || allSummits.isEmpty()) return;

            // Get user ID for Supabase sync
            UserEntity user = db.userDao().getSingleUser();
            int userId = user != null ? (int) user.getUserId() : -1;

            for (int i = 0; i < gpxData.latitudes.size(); i++) {
                for (SummitsEntity summit : allSummits) {

                    if (summit.isCompleted()) continue;

                    double distance = CalculateDistance.equirectangular(
                            gpxData.latitudes.get(i),
                            gpxData.longitudes.get(i),
                            summit.getLatitude(),
                            summit.getLongitude());

                    if (distance <= geofenceRadius) {
                        summit.setCompleted(true);
                        updateDatabase(context, summit, userId);

                        if (SetActivityName.summitName == null) {
                            SetActivityName.summitName = summit.getSummitName();
                        }
                    }
                }
            }
        }).start();
    }

    private void updateDatabase(Context context, SummitsEntity s, int userId) {
        LocalDatabase db = LocalDatabase.getInstance(context);

        // 1. Mark summit completed
        db.summitsDao().updateSummit(s);

        // 2. Find all challenge_items that reference this summit and mark them completed
        List<ChallengeItemEntity> items = db.challengeItemDao().getItemsBySummitId(s.getSummitId());

        if (items == null || items.isEmpty()) return;

        for (ChallengeItemEntity item : items) {
            item.setCompleted(true);
            db.challengeItemDao().updateItem(item);

            // 3. Sync completed item to Supabase
            if (userId != -1) {
                InsertChallengeItem.insert(context, userId, item, new InsertChallengeItem.InsertCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("GEOFENCE", "Challenge item synced to Supabase");
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("GEOFENCE", "Challenge item sync failed: " + error);
                    }
                });
            }

            // 4. Update the parent challenge progress
            ChallengesEntity challenge = db.challengesDao().getChallengeById(item.getChallengeId());
            if (challenge != null) {
                int completed = db.challengeItemDao().getCompletedCountForChallenge(item.getChallengeId());
                int total = db.challengeItemDao().getTotalCountForChallenge(item.getChallengeId());
                challenge.setCompletedSummits(completed);
                if (completed >= total) {
                    challenge.setCompleted(true);
                }
                db.challengesDao().updateChallenge(challenge);
                Log.d("GEOFENCE", "Updated challenge: " + challenge.getChallengeName());
            }
        }
    }
}