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
                // get id so room overwrites the same row
                int existingId = user.getUserId();
                Log.d("UpdateProfile", "updating user id: " + existingId);

                // increment stats
                user.setTotalDistanceKm(user.getTotalDistanceKm() + (int) distanceKM);
                user.setTotalElevationM(user.getTotalElevationM() + (int) elevationM);
                user.setCompletedActivities(user.getCompletedActivities() + activitiesIncrement);
                user.setCompletedChallenges(user.getCompletedChallenges() + challengesIncrement);
                user.setTotalTimeOutsideSeconds(user.getTotalTimeOutsideSeconds() + timeOutsideSeconds);
                user.setTotalMovingTimeSeconds(user.getTotalMovingTimeSeconds() + timeMovingSeconds);

                // save
                userDao.insert(user);

                Log.d("UpdateProfile", "stats updated. new distance: " + user.getTotalDistanceKm());
            } else {
                Log.e("UpdateProfile", "user was null. profile not updated.");
            }
        } catch (Exception e) {
            Log.e("UpdateProfile", "error updating profile", e);
        }
    }
}
