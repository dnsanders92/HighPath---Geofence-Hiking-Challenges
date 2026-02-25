package com.example.highpath.Utilities.GPX;

import android.content.Context;

import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.LocalDatabase;

public class GpxFileName {

    private final UserDao userDao;

    public GpxFileName(Context context) {
        LocalDatabase db = LocalDatabase.getInstance(context.getApplicationContext());
        this.userDao = db.userDao();
    }

    public String generate() {
        int userId = userDao.getUserId();

        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return userId + "_" + timestamp + ".gpx";
    }
}
