package com.example.highpath.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Calculations.CalculateDistance;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Utilities.GPX.GPXData;

import java.util.List;

public class CheckGeofence {

    private final double geofenceRadius = 15.0;

    // Run this entire method off the main thread
    public void checkGeofence(Context context, GPXData gpxData) {
        if (context == null || gpxData == null) return;

        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(context);
            List<SummitsEntity> allSummits = db.summitsDao().getAllSummits();

            if (allSummits == null || allSummits.isEmpty()) return;

            for (int i = 0; i < gpxData.latitudes.size(); i++) {
                for (SummitsEntity summit : allSummits) {

                    // Skip already-completed summits early
                    if (summit.isCompleted()) continue;

                    double distance = CalculateDistance.equirectangular(
                            gpxData.latitudes.get(i),
                            gpxData.longitudes.get(i),
                            summit.getLatitude(),
                            summit.getLongitude());

                    if (distance <= geofenceRadius) {
                        // Mark completed on the object immediately to prevent
                        // duplicate processing by later loop iterations
                        summit.setCompleted(true);
                        updateDatabase(context, summit);

                        if (SetActivityName.summitName == null) {
                            SetActivityName.summitName = summit.getSummitName();
                        }
                    }
                }
            }
        }).start();
    }

    private void updateDatabase(Context context, SummitsEntity s) {
        // Already running on a background thread — no need to nest another Thread
        LocalDatabase db = LocalDatabase.getInstance(context);

        db.summitsDao().updateSummit(s);

        ChallengesEntity challenge = db.challengesDao().getChallengeByRegion(s.getRegion());
        if (challenge != null) {
            challenge.setCompletedSummits(challenge.getCompletedSummits() + 1);
            if (challenge.getCompletedSummits() >= challenge.getTotalSummits()) {
                challenge.setCompleted(true);
            }
            db.challengesDao().updateChallenge(challenge);
            Log.d("GEOFENCE", "Updated challenge: " + challenge.getChallengeName());
        }
    }
}