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

    public void checkGeofence(Context context, GPXData gpxData) {
        if (context == null || gpxData == null) return;

        LocalDatabase db = LocalDatabase.getInstance(context);
        List<SummitsEntity> allSummits = db.summitsDao().getAllSummits();

        if (allSummits == null) return;

        for (int i = 0; i < gpxData.latitudes.size(); i++) {
            for (int j = 0; j < allSummits.size(); j++) {

                SummitsEntity summit = allSummits.get(j);

                double distance = CalculateDistance.equirectangular(
                        gpxData.latitudes.get(i),
                        gpxData.longitudes.get(i),
                        summit.getLatitude(),
                        summit.getLongitude());

                if (distance <= geofenceRadius) {
                    updateDatabase(context, summit);
                }
            }
        }
    }

    private void updateDatabase(Context context, SummitsEntity s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(context);

                if (s != null && !s.isCompleted()) {
                    s.setCompleted(true);
                    db.summitsDao().updateSummit(s);

                    ChallengesEntity challenge = db.challengesDao().getChallengeByRegion(s.getRegion());
                    if (challenge != null) {
                        challenge.setCompletedSummits(challenge.getCompletedSummits() + 1);
                        if (challenge.getCompletedSummits() >= challenge.getTotalSummits()) {
                            challenge.setCompleted(true);
                        }
                        db.challengesDao().updateChallenge(challenge);
                        Log.d("GEOFENCE", "Updated " + challenge.getChallengeName());
                    }
                }
            }
        }).start();
    }
}