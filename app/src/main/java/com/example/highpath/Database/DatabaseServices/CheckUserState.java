package com.example.highpath.Database.DatabaseServices;

import android.app.Activity;
import android.content.Intent;

import com.example.highpath.Activities.LoginActivity;
import com.example.highpath.Database.LocalDatabase;

public class CheckUserState {

    // check if user exists in local db
    public static void checkUser(final Activity activity) {

        new Thread(() -> {

            LocalDatabase db = LocalDatabase.getInstance(activity);
            boolean hasUser = db.userDao().hasUser();

            // no user → go to login
            if (!hasUser) {
                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                });
            }

        }).start();
    }
}
