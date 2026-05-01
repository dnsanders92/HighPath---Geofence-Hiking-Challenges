package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.ChallengesEntity;
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

    public interface SyncCallback {
        void onComplete();
    }

    // save activities from cloud to local db.
    public static void syncActivitiesToLocal(final Context context, JSONArray activitiesArray, SyncCallback callback) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                Log.d(TAG, "processing " + activitiesArray.length() + " activities");

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

                Log.d(TAG, "activity sync complete");
                if (callback != null) callback.onComplete();

            } catch (Exception e) {
                Log.e(TAG, "error saving activities: " + e.getMessage());
            }
        }).start();
    }

    // save splits from cloud to local db.
    public static void syncSplitsToLocal(final Context context, JSONArray splitsArray) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                Log.d(TAG, "processing " + splitsArray.length() + " splits");

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

                Log.d(TAG, "split sync complete");

            } catch (Exception e) {
                Log.e(TAG, "error saving splits: " + e.getMessage());
            }
        }).start();
    }

    // save completed challenge items from cloud to local db.
    public static void syncChallengeItemsToLocal(final Context context, JSONArray itemsArray) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);
                Log.d(TAG, "processing " + itemsArray.length() + " challenge items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject obj = itemsArray.getJSONObject(i);

                    int summitId = obj.getInt("summit_id");
                    int challengeId = obj.getInt("challenge_id");

                    // mark summit completed
                    db.challengeItemDao().markSummitCompleted(summitId);
                    db.summitsDao().markSummitCompleted(summitId);

                    // update challenge progress
                    ChallengesEntity challenge = db.challengesDao().getChallengeById(challengeId);

                    if (challenge != null) {
                        challenge.setCompletedSummits(challenge.getCompletedSummits() + 1);

                        if (challenge.getCompletedSummits() >= challenge.getTotalSummits()) {
                            challenge.setCompleted(true);
                        }

                        db.challengesDao().updateChallenge(challenge);
                    }
                }

                Log.d(TAG, "challenge item sync complete");

            } catch (Exception e) {
                Log.e(TAG, "error saving challenge items: " + e.getMessage());
            }
        }).start();
    }

    // save cloud user to local db.
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

                // default images
                user.profileImage = drawableToBytes(context, R.drawable.default_profile_image);
                user.bannerImage = drawableToBytes(context, R.drawable.default_banner_image);

                db.userDao().insert(user);

                Log.d(TAG, "profile saved locally");

            } catch (Exception e) {
                Log.e(TAG, "profile sync failed: " + e.getMessage());
            }
        }).start();
    }

    // create new local user
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

    // convert drawable to byte[]
    private static byte[] drawableToBytes(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}
