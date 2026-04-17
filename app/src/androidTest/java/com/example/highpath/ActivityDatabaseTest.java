package com.example.highpath;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityDatabaseTest {

    private LocalDatabase db;
    private ActivityDao activityDao;

    private static final String TEST_ACTIVITY_ID = "test_activity_001";

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = LocalDatabase.getInstance(context);
        activityDao = db.activityDao();
    }

    @Test
    public void testActivityIsNullBeforeInsertion() {
        // Make sure no activity with our test ID exists before we start
        ActivityEntity activity = activityDao.getActivityById(TEST_ACTIVITY_ID);
        assertNull("Activity should not exist before insertion", activity);
    }

    @Test
    public void testInsertActivityAndVerifyExists() {
        // Build and insert a test activity
        ActivityEntity activity = buildTestActivity();
        activityDao.insertActivity(activity);

        // Verify it was saved
        ActivityEntity retrieved = activityDao.getActivityById(TEST_ACTIVITY_ID);
        assertNotNull("Activity should exist after insertion", retrieved);
        assertEquals("Activity ID should match", TEST_ACTIVITY_ID, retrieved.getActivityId());
        assertEquals("Activity name should match", "Test Activity", retrieved.getActivityName());
        assertEquals("Distance should match", 7.23, retrieved.getDistance(), 0.01);
        assertEquals("Elevation should match", 365.0, retrieved.getElevationGain(), 0.01);
    }

    @Test
    public void testDeleteActivityAndVerifyGone() {
        // Insert first so we have something to delete
        ActivityEntity activity = buildTestActivity();
        activityDao.insertActivity(activity);

        // Confirm it exists
        ActivityEntity inserted = activityDao.getActivityById(TEST_ACTIVITY_ID);
        assertNotNull("Activity should exist before deletion", inserted);

        // Delete it
        activityDao.deleteActivity(inserted);

        // Confirm it is gone
        ActivityEntity deleted = activityDao.getActivityById(TEST_ACTIVITY_ID);
        assertNull("Activity should not exist after deletion", deleted);
    }

    @After
    public void tearDown() {
        // Clean up test data after each test so they don't interfere with each other
        ActivityEntity activity = activityDao.getActivityById(TEST_ACTIVITY_ID);
        if (activity != null) {
            activityDao.deleteActivity(activity);
        }
    }

    private ActivityEntity buildTestActivity() {
        ActivityEntity activity = new ActivityEntity();
        activity.setActivityId(TEST_ACTIVITY_ID);
        activity.setActivityName("Test Activity");
        activity.setDistance(7.23);
        activity.setElevationGain(365.0);
        activity.setElevationLoss(341.0);
        activity.setAveragePace(6.56);
        activity.setMovingTime(11520L);
        activity.setActualTime(11520L);
        activity.setCalories(487.0);
        activity.setTimestamp(1744848000L);
        activity.setIsChallenge(false);
        return activity;
    }
}