package com.example.highpath.Database.DatabaseServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;

import java.util.concurrent.Executors;
import android.os.Handler;

public class GetUserAccount {

    public static void getUser(final Context context,
                               ImageView bannerImage, ImageView profileImage,
                               TextView userName,
                               TextView totalDistance, TextView totalElevation,
                               TextView completedActivities, TextView completedChallenges,
                               TextView totalTimeOutside, TextView totalTimeMoving) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                LocalDatabase db = LocalDatabase.getInstance(context);
                UserEntity user = db.userDao().getUser();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        if (user == null) return;

                        // Name - String concatenation handles nulls by showing "null",
                        // but a ternary check is cleaner
                        String first = user.firstName != null ? user.firstName : "";
                        String last = user.surname != null ? user.surname : "";
                        userName.setText(first + " " + last);

                        // Stats - Using helper to prevent NullPointerException and handle the display
                        totalDistance.setText(getSafeInt(user.totalDistanceKm) + " km");
                        totalElevation.setText(getSafeInt(user.totalElevationM) + " m");

                        // Fixed: Removed String.valueOf() wrap around the emoji which was causing redundant calls
                        completedActivities.setText(getSafeInt(user.completedActivities) + "  ⛰️");
                        completedChallenges.setText(getSafeInt(user.completedChallenges) + "  \uD83C\uDFC6");

                        // Time - Using the updated formatTime which now accepts 'Long' correctly
                        totalTimeOutside.setText(formatTime(user.totalTimeOutsideSeconds));
                        totalTimeMoving.setText(formatTime(user.totalMovingTimeSeconds));

                        if (user.bannerImage != null) {
                            bannerImage.setImageBitmap(
                                    BitmapFactory.decodeByteArray(user.bannerImage, 0, user.bannerImage.length)
                            );
                        }

                        if (user.profileImage != null) {
                            profileImage.setImageBitmap(
                                    BitmapFactory.decodeByteArray(user.profileImage, 0, user.profileImage.length)
                            );
                        }
                    }
                });
            }
        });
    }

    // Helper to safely handle Integer objects from the database
    private static int getSafeInt(Integer value) {
        return value != null ? value : 0;
    }

    // Updated to accept Long (wrapper) or long (primitive)
    private static String formatTime(Long totalSeconds) {
        if (totalSeconds == null || totalSeconds <= 0) return "0m";

        long seconds = totalSeconds;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
}