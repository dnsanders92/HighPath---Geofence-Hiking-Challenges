package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.UserEntity;

public class ActivityBuilder {

    // Spoof values
    double distance = 23.5;
    double elevation = 100.0;
    int timeOutside = 2000;
    int timeMoving = 1000;

    public void insertActivity(Context context, String filePath) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                ActivityDao activityDao = db.activityDao();
                UserDao userDao = db.userDao();

                // 1. Generate Activity
                ActivityEntity activity = new ActivityEntity();
                UserEntity user = userDao.getSingleUser();
                String userIdStr = (user != null) ? String.valueOf(user.getUserId()) : "guest";

                activity.setActivityId(userIdStr + "_" + System.currentTimeMillis());
                activity.setActivityName("Finished Hike");
                activity.setDistance(distance);
                activity.setElevationGain(elevation);
                activity.setElevationLoss(elevation);
                activity.setMovingTime(timeMoving);
                activity.setActualTime(timeOutside);
                activity.setAveragePace(11.45);
                activity.setCalories(1234.0);
                activity.setGpxFilePath(filePath);
                activity.setTimestamp(System.currentTimeMillis());

                // 2. Update the User Profile (Synchronous on this thread)
                UpdateProfile updateProfile = new UpdateProfile();
                updateProfile.update(
                        context,
                        distance,
                        elevation,
                        1,    // +1 Activity
                        0,    // +0 Challenges
                        timeOutside,
                        timeMoving
                );

                // 3. Save Activity to DB
                activityDao.insertActivity(activity);

                Log.d("ActivityBuilder", "Process Complete: Profile updated and Activity saved.");

            } catch (Exception e) {
                Log.e("ActivityBuilder", "Error in post-processing thread", e);
            }
        }).start();
    }
}