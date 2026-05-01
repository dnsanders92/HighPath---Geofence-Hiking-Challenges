package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;

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
import com.example.highpath.Database.Supabase.UploadTrailMap;
import com.example.highpath.MapBox.GenerateTrailMap;
import com.example.highpath.Utilities.SetActivityName;

public class ActivityBuilder {

    private static final String TAG = "ActivityBuilder";

    public void insertActivity(Context context, String fullPath) {

        new Thread(() -> {

            // calculate metrics
            CalculateFinalMetrics metrics = new CalculateFinalMetrics();
            metrics.calculateMetrics(context, fullPath);

            SetActivityName setActivityName = new SetActivityName();

            // generate trail map
            if (metrics.gpx != null) {
                GenerateTrailMap.generateMap(context, metrics.gpx, new GenerateTrailMap.TrailMapCallback() {
                    @Override
                    public void onSuccess(android.graphics.Bitmap bitmap) {
                        Log.d(TAG, "trail map generated");
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "trail map failed: " + error);
                    }
                });

                // short delay to allow async map write
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }

            // database access
            LocalDatabase db = LocalDatabase.getInstance(context);
            UserDao userDao = db.userDao();
            ActivityDao activityDao = db.activityDao();

            UserEntity user = userDao.getSingleUser();

            if (user != null) {

                // create activity
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

                // attach trail map image
                if (metrics.gpx != null && metrics.gpx.trailMapImage != null) {
                    activity.setTrailMapImage(metrics.gpx.trailMapImage);
                }

                // save activity locally
                activityDao.insert(activity);

                // save splits
                if (!metrics.splits.isEmpty()) {
                    for (SplitEntity split : metrics.splits) {
                        split.setActivityId(activity.getActivityId());
                    }
                    db.splitDao().insertSplits(metrics.splits);
                }

                // update user stats
                user.setTotalDistanceKm(user.getTotalDistanceKm() + (int) Math.round(metrics.totalDistanceKM));
                user.setTotalElevationM(user.getTotalElevationM() + (int) Math.round(metrics.totalElevationGainM));
                user.setTotalTimeOutsideSeconds(user.getTotalTimeOutsideSeconds() + (int) metrics.totalTimeOutside);
                user.setTotalMovingTimeSeconds(user.getTotalMovingTimeSeconds() + (int) metrics.totalTimeMoving);
                user.setCompletedActivities(user.getCompletedActivities() + 1);

                userDao.update(user);

                // sync activity to supabase
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
                        activity.getIsChallenge(),
                        new InsertActivity.ActivityCallback() {
                            @Override
                            public void onSuccess() { Log.d(TAG, "supabase activity synced"); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "supabase activity sync failed: " + error); }
                        }
                );

                // sync splits
                InsertSplits.insert(
                        context,
                        (int) user.getUserId(),
                        metrics.splits,
                        new InsertSplits.InsertCallback() {
                            @Override
                            public void onSuccess() { Log.d(TAG, "splits synced"); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "splits sync failed: " + error); }
                        }
                );

                // sync user stats
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
                            public void onSuccess() { Log.d(TAG, "supabase user stats updated"); }
                            @Override
                            public void onFailure(String error) { Log.e(TAG, "supabase user update failed: " + error); }
                        }
                );

                // upload gpx file
                UploadGpx gpxUploader = new UploadGpx();
                gpxUploader.upload(context, (int) user.getUserId(), fullPath, new UploadGpx.UploadCallback() {
                    @Override
                    public void onSuccess(String fileUrl) { Log.d(TAG, "gpx uploaded: " + fileUrl); }
                    @Override
                    public void onFailure(String error) { Log.e(TAG, "gpx upload failed: " + error); }
                });

                // upload trail map
                if (metrics.gpx != null && metrics.gpx.trailMapImage != null) {
                    UploadTrailMap.upload(
                            context,
                            (int) user.getUserId(),
                            activity.getActivityId(),
                            metrics.gpx.trailMapImage,
                            new UploadTrailMap.UploadCallback() {
                                @Override
                                public void onSuccess(String fileUrl) { Log.d(TAG, "trail map uploaded: " + fileUrl); }
                                @Override
                                public void onFailure(String error) { Log.e(TAG, "trail map upload failed: " + error); }
                            }
                    );
                }

            } else {
                Log.w(TAG, "no user found");
            }

        }).start();
    }
}
