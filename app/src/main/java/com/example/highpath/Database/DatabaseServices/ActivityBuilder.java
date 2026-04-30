package com.example.highpath.Database.DatabaseServices;

import android.util.Log;
import android.content.Context;

import com.example.highpath.Calculations.CalculateFinalMetrics;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.Supabase.InsertActivity;
import com.example.highpath.Database.Supabase.InsertSplits;
import com.example.highpath.Database.Supabase.UpdateUser;
import com.example.highpath.Database.Supabase.UploadGpx;
import com.example.highpath.Utilities.SetActivityName;

public class ActivityBuilder {

    private static final String TAG = "ActivityBuilder";

    public void insertActivity(Context context, String fullPath) {
        new Thread(() -> {

            CalculateFinalMetrics metrics = new CalculateFinalMetrics();
            metrics.calculateMetrics(context, fullPath);
            SetActivityName setActivityName = new SetActivityName();

            LocalDatabase db = LocalDatabase.getInstance(context);
            UserDao userDao = db.userDao();
            ActivityDao activityDao = db.activityDao();

            UserEntity user = userDao.getSingleUser();

            if (user != null) {
                // 1. Create and Save Activity Locally
                ActivityEntity activity = new ActivityEntity();
                activity.setActivityId(user.getUserId() + "_" + System.currentTimeMillis());
                activity.setUserId(user.getUserId());
                activity.setActivityName(setActivityName.getActivityName());
                activity.setDistance(metrics.totalDistanceKM);
                activity.setElevationGain(metrics.totalElevationGainM);
                activity.setElevationLoss(metrics.totalElevationLossM);
                activity.setAveragePace(metrics.averagePace);
                activity.setMovingTime(metrics.totalTimeMoving);
                activity.setActualTime(metrics.totalTimeOutside);
                activity.setGpxFilePath(fullPath);
                activity.setTimestamp(System.currentTimeMillis());
                activity.setCalories(metrics.calories);
                activity.setIsChallenge(false);

                activityDao.insert(activity);

                // Save splits
                if (!metrics.splits.isEmpty()) {
                    for (SplitEntity split : metrics.splits) {
                        split.setActivityId(activity.getActivityId());
                    }
                    db.splitDao().insertSplits(metrics.splits);
                }

                // 2. Update User Stats Locally
                user.setTotalDistanceKm(user.getTotalDistanceKm() + (int) Math.round(metrics.totalDistanceKM));
                user.setTotalElevationM(user.getTotalElevationM() + (int) Math.round(metrics.totalElevationGainM));
                user.setTotalTimeOutsideSeconds(user.getTotalTimeOutsideSeconds() + (int) metrics.totalTimeOutside);
                user.setTotalMovingTimeSeconds(user.getTotalMovingTimeSeconds() + (int) metrics.totalTimeMoving);
                user.setCompletedActivities(user.getCompletedActivities() + 1);

                userDao.update(user);
                Log.d(TAG, "Activity and user stats saved locally.");

                // 3. Sync Activity to Supabase
                InsertActivity.insert(
                        context,
                        activity.getActivityId(),
                        activity.getUserId(),
                        activity.getActivityName(),
                        activity.getDistance(),
                        activity.getElevationGain(),
                        activity.getElevationLoss(),
                        activity.getAveragePace(),
                        activity.getMovingTime(),
                        activity.getActualTime(),
                        activity.getCalories(),
                        activity.getGpxFilePath(),
                        activity.getTimestamp(),
                        activity.getIsChallenge() != null && activity.getIsChallenge(),
                        new InsertActivity.ActivityCallback() {
                            @Override
                            public void onSuccess() { Log.d(TAG, "Supabase activity synced."); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "Supabase activity sync failed: " + error); }
                        }
                );

                InsertSplits.insert(
                        context,
                        (int) user.getUserId(),
                        metrics.splits,
                        new InsertSplits.InsertCallback() {
                            @Override
                            public void onSuccess() { Log.d(TAG, "Splits synced to Supabase."); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "Splits sync failed: " + error); }
                        }
                );;

                // 4. Update User Stats on Supabase
                UpdateUser.updateStatistics(
                        context,
                        user.getUserId(),
                        (double) user.getTotalDistanceKm(),
                        (double) user.getTotalElevationM(),
                        user.getCompletedActivities(),
                        user.getCompletedChallenges(),
                        user.getTotalMovingTimeSeconds(),
                        user.getTotalTimeOutsideSeconds(),
                        new UpdateUser.UpdateCallback() {
                            @Override
                            public void onSuccess() { Log.d(TAG, "Supabase user stats updated."); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "Supabase user update failed: " + error); }
                        }
                );

                // 5. Upload GPX File to Supabase Storage
                UploadGpx gpxUploader = new UploadGpx();
                // Note: Casting long userId to int as per your UploadGpx signature
                gpxUploader.upload(context, (int) user.getUserId(), fullPath, new UploadGpx.UploadCallback() {
                    @Override
                    public void onSuccess(String fileUrl) {
                        Log.d(TAG, "GPX file successfully uploaded to storage: " + fileUrl);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "GPX storage upload failed: " + error);
                    }
                });

            } else {
                Log.w(TAG, "No user found.");
            }
        }).start();
    }
}