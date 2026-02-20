package com.example.highpath.Database.DatabaseServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.Daos.UserDao;
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

                        // Name
                        userName.setText(user.firstName + " " + user.surname);

                        // Stats
                        totalDistance.setText(user.totalDistanceKm + " km");
                        totalElevation.setText(user.totalElevationM + " m");
                        completedActivities.setText(String.valueOf(user.completedActivities + "  ⛰️"));
                        completedChallenges.setText(String.valueOf(user.completedChallenges + "  \uD83C\uDFC6"));
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
    private static String formatTime(int totalSeconds) {

        int minutes = totalSeconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        minutes = minutes % 60;
        hours = hours % 24;

        return days + "d " + hours + "h " + minutes + "m";
    }

}
