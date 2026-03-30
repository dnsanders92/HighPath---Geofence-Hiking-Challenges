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
            // 1. Run the calculations first
            CalculateFinalMetrics metrics = new CalculateFinalMetrics();
            metrics.calculateMetrics(fullPath); // Essential: This populates the variables

            // 2. Get Database instances
            LocalDatabase db = LocalDatabase.getInstance(context);
            UserDao userDao = db.userDao();
            ActivityDao activityDao = db.activityDao();

            // 3. Get User and build Entity
            UserEntity user = userDao.getSingleUser();

            if (user != null) {
                ActivityEntity activity = new ActivityEntity();

                // Set Identity and Name
                activity.setActivityId(user.getUserId() + "_" + System.currentTimeMillis());
                activity.setActivityName("Finished Hike");

                // Map the calculated metrics
                activity.setDistance(metrics.totalDistanceKM);
                activity.setElevationGain(metrics.totalElevationGainM);
                activity.setElevationLoss(metrics.totalElevationLossM);
                activity.setAveragePace(metrics.averagePace);
                activity.setMovingTime(metrics.totalTimeMoving);
                activity.setActualTime(metrics.totalTimeOutside);

                // Placeholder/Additional Data
                activity.setCalories(1230);
                activity.setGpxFilePath(fullPath);
                activity.setTimestamp(System.currentTimeMillis());

                // 4. Insert into Database
                activityDao.insertActivity(activity);

                Log.d(TAG, "Activity successfully saved for user: " + user.getUserId());
            } else {
                Log.w(TAG, "Could not save activity: No user found in database.");
            }
        }).start();
    }
}