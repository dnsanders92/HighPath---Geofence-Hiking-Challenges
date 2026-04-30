package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateLocalUser {

    private static final String TAG = "SyncDebug";

    public static void syncActivitiesToLocal(final Context context, JSONArray activitiesArray) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                Log.d(TAG, "Room worker started. Processing " + activitiesArray.length() + " items.");

                for (int i = 0; i < activitiesArray.length(); i++) {
                    JSONObject obj = activitiesArray.getJSONObject(i);
                    ActivityEntity activity = new ActivityEntity();

                    activity.setActivityId(obj.getString("activity_id"));
                    activity.setUserId(obj.getInt("user_id"));
                    activity.setActivityName(obj.optString("activity_name", "Activity"));
                    activity.setDistance(obj.optDouble("distance", 0.0));
                    activity.setElevationGain(obj.optDouble("elevation_gain", 0.0));
                    activity.setElevationLoss(obj.optDouble("elevation_loss", 0.0));
                    activity.setAveragePace(obj.optDouble("average_pace", 0.0));
                    activity.setMovingTime(obj.optLong("moving_time", 0L));
                    activity.setActualTime(obj.optLong("actual_time", 0L));
                    activity.setCalories(obj.optDouble("calories", 0.0));
                    activity.setGpxFilePath(obj.optString("gpx_file_path", ""));
                    activity.setTimestamp(obj.optLong("timestamp", System.currentTimeMillis()));
                    activity.setIsChallenge(obj.optBoolean("is_challenge", false));

                    db.activityDao().insert(activity);
                }
                Log.d(TAG, "Activity Sync Complete.");
            } catch (Exception e) {
                Log.e(TAG, "Error saving activities to Room: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void syncSplitsToLocal(final Context context, JSONArray splitsArray) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                Log.d(TAG, "Saving " + splitsArray.length() + " splits to Room.");

                List<SplitEntity> splitList = new ArrayList<>();

                for (int i = 0; i < splitsArray.length(); i++) {
                    JSONObject obj = splitsArray.getJSONObject(i);
                    SplitEntity split = new SplitEntity();
                    split.setActivityId(obj.getString("activity_id"));
                    split.setSplitIndex(obj.getInt("split_index"));
                    split.setPace(obj.getDouble("split_pace"));
                    split.setDistance(obj.getDouble("split_distance"));
                    splitList.add(split);
                }

                if (!splitList.isEmpty()) {
                    db.splitDao().insertSplits(splitList);
                }

                Log.d(TAG, "Splits Sync Complete.");
            } catch (Exception e) {
                Log.e(TAG, "Error saving splits to Room: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void syncCloudToLocal(final Context context, JSONObject json) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                UserEntity user = new UserEntity();

                user.userId = (int) json.getLong("user_id");
                user.firstName = json.getString("first_name");
                user.surname = json.getString("surname");
                user.monthOfBirth = json.getString("month_of_birth");
                user.yearOfBirth = json.getInt("year_of_birth");
                user.totalDistanceKm = json.optInt("total_distance_km", 0);
                user.totalElevationM = json.optInt("total_elevation_m", 0);
                user.completedActivities = json.optInt("completed_activities", 0);
                user.completedChallenges = json.optInt("completed_challenges", 0);
                user.totalTimeOutsideSeconds = json.optLong("total_time_outside_seconds", 0L);
                user.totalMovingTimeSeconds = json.optLong("total_moving_time_seconds", 0L);

                user.profileImage = drawableToBytes(context, R.drawable.default_profile_image);
                user.bannerImage = drawableToBytes(context, R.drawable.default_banner_image);

                db.userDao().insert(user);
                Log.d(TAG, "Profile saved locally.");
            } catch (Exception e) {
                Log.e(TAG, "Profile sync failed: " + e.getMessage());
            }
        }).start();
    }

    public static void addUser(final Context context, int userId, String firstName, String surname,
                               String month, int year) {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(context);
            UserEntity user = new UserEntity();
            user.userId = userId;
            user.firstName = firstName;
            user.surname = surname;
            user.monthOfBirth = month;
            user.yearOfBirth = year;
            user.totalDistanceKm = 0;
            user.totalElevationM = 0;
            user.completedActivities = 0;
            user.completedChallenges = 0;
            user.totalTimeOutsideSeconds = 0L;
            user.totalMovingTimeSeconds = 0L;
            user.profileImage = drawableToBytes(context, R.drawable.default_profile_image);
            user.bannerImage = drawableToBytes(context, R.drawable.default_banner_image);
            db.userDao().insert(user);
        }).start();
    }

    private static byte[] drawableToBytes(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}