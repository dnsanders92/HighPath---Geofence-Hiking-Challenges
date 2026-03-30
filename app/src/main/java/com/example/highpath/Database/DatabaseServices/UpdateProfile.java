package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.UserEntity;

public class UpdateProfile {

    public void update(Context context,
                       double distanceKM,
                       double elevationM,
                       int activitiesIncrement,
                       int challengesIncrement,
                       int timeOutsideSeconds,
                       int timeMovingSeconds) {

        try {
            UserDao userDao = LocalDatabase.getInstance(context).userDao();
            UserEntity user = userDao.getSingleUser();

            if (user != null) {
                // IMPORTANT: Preserve the existing ID so Room overwrites the same row
                int existingId = user.getUserId();
                Log.d("UpdateProfile", "Updating User ID: " + existingId);

                // Increment values
                user.setTotalDistanceKm(user.getTotalDistanceKm() + (int) distanceKM);
                user.setTotalElevationM(user.getTotalElevationM() + (int) elevationM);
                user.setCompletedActivities(user.getCompletedActivities() + activitiesIncrement);
                user.setCompletedChallenges(user.getCompletedChallenges() + challengesIncrement);
                user.setTotalTimeOutsideSeconds(user.getTotalTimeOutsideSeconds() + timeOutsideSeconds);
                user.setTotalMovingTimeSeconds(user.getTotalMovingTimeSeconds() + timeMovingSeconds);

                // Save back to DB
                userDao.insert(user);

                Log.d("UpdateProfile", "Stats updated. New Distance: " + user.getTotalDistanceKm());
            } else {
                Log.e("UpdateProfile", "User entity was null. Profile not updated.");
            }
        } catch (Exception e) {
            Log.e("UpdateProfile", "Error updating profile", e);
        }
    }
}