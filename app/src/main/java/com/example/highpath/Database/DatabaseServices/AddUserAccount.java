package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class AddUserAccount {

    public static void addUser(final Context context, String firstName, String surname,
                               String month, int year) {

        Runnable run = new Runnable() {
            @Override
            public void run() {

                LocalDatabase db = LocalDatabase.getInstance(context);

                UserEntity user = new UserEntity();

                // Basic user info
                user.firstName = firstName;
                user.surname = surname;
                user.monthOfBirth = month;
                user.yearOfBirth = year;

                // Default statistics for new user
                user.totalDistanceKm = 0;
                user.totalElevationM = 0;
                user.completedActivities = 0;
                user.completedChallenges = 0;
                user.totalTimeOutsideSeconds = 0;
                user.totalMovingTimeSeconds = 0;

                // Loads default images from drawables and converts to byte[], images store as blobs
                user.profileImage = drawableToBytes(context, R.drawable.default_profile_image);
                user.bannerImage = drawableToBytes(context, R.drawable.default_banner_image);

                // Inserts user into local database
                db.userDao().insert(user);
            }
        };

        // Starts background thread
        Thread thread = new Thread(run);
        thread.start();
    }

    // Converts a drawable resource into byte array.
    private static byte[] drawableToBytes(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
