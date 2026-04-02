package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Calculations.CalculateFinalMetrics;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.UserEntity;

public class ActivityBuilder {

    private static final String TAG = "ActivityBuilder";

    public void insertActivity(Context context, String fullPath) {
        new Thread(() -> {
            CalculateFinalMetrics metrics = new CalculateFinalMetrics();
            metrics.calculateMetrics(fullPath);

            LocalDatabase db = LocalDatabase.getInstance(context);
            UserDao userDao = db.userDao();
            ActivityDao activityDao = db.activityDao();

            UserEntity user = userDao.getSingleUser();

            if (user != null) {
                // 1. Create and Save the Activity Record
                ActivityEntity activity = new ActivityEntity();
                activity.setActivityId(user.getUserId() + "_" + System.currentTimeMillis());
                activity.setActivityName("Finished Hike");
                activity.setDistance(metrics.totalDistanceKM);
                activity.setElevationGain(metrics.totalElevationGainM);
                activity.setElevationLoss(metrics.totalElevationLossM);
                activity.setAveragePace(metrics.averagePace);
                activity.setMovingTime(metrics.totalTimeMoving);
                activity.setActualTime(metrics.totalTimeOutside);
                activity.setGpxFilePath(fullPath);
                activity.setTimestamp(System.currentTimeMillis());
                activity.setCalories(1230); // Placeholder

                activityDao.insertActivity(activity);

                // 2. Update the User's Cumulative Statistics
                // Note: We cast doubles to ints to match your UserEntity fields
                user.setTotalDistanceKm(user.getTotalDistanceKm() + (int) Math.round(metrics.totalDistanceKM));
                user.setTotalElevationM(user.getTotalElevationM() + (int) Math.round(metrics.totalElevationGainM));
                user.setTotalTimeOutsideSeconds(user.getTotalTimeOutsideSeconds() + (int) metrics.totalTimeOutside);
                user.setTotalMovingTimeSeconds(user.getTotalMovingTimeSeconds() + (int) metrics.totalTimeMoving);
                user.setCompletedActivities(user.getCompletedActivities() + 1);

                // 3. Persist the changes to the user table
                userDao.update(user);

                Log.d(TAG, "Activity saved and lifetime stats updated for: " + user.getFirstName());
            } else {
                Log.w(TAG, "Activity not saved: No user found.");
            }
        }).start();
    }
}