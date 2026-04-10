package com.example.highpath.Database.DatabaseServices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.highpath.Activities.LoginActivity;
import com.example.highpath.Database.LocalDatabase;

public class CheckUserState {

    public static void checkUser(final Activity activity) {

        Runnable run = new Runnable() {
            @Override
            public void run() {

                LocalDatabase db = LocalDatabase.getInstance(activity);
                final boolean hasUser = db.userDao().hasUser();

                if (!hasUser) {

                    new AddChallenges().insertChallenges(activity);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
                }
            }
        };

        Thread thread = new Thread(run);
        thread.start();
    }
}
