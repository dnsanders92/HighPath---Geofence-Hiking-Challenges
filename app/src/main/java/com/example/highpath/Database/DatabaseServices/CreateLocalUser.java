package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;
import java.io.ByteArrayOutputStream;

public class CreateLocalUser {

    public static void addUser(final Context context, int userId, String firstName, String surname,
                               String month, int year) {

        Runnable run = () -> {
            LocalDatabase db = LocalDatabase.getInstance(context);
            UserEntity user = new UserEntity();

            // Use the explicit ID provided by Supabase
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
        };

        new Thread(run).start();
    }

    private static byte[] drawableToBytes(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}