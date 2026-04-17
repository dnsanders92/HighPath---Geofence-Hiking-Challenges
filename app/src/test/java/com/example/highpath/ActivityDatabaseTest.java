package com.example.highpath;

import android.content.Context;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;

import org.junit.runner.manipulation.Ordering;

public class ActivityDatabaseTest {

    Context context;

    LocalDatabase db = LocalDatabase.getInstance(null);
    ActivityDao activityDao = db.activityDao();
    ActivityEntity activity = new ActivityEntity();

}
