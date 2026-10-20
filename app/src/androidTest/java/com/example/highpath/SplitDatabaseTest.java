package com.example.highpath;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.SplitDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Database.LocalDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SplitDatabaseTest {

    private LocalDatabase db;
    private SplitDao splitDao;
    private ActivityDao activityDao;

    private static final String TEST_ACTIVITY_ID = "test_split_activity_001";

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = LocalDatabase.getInstance(context);
        splitDao = db.splitDao();
        activityDao = db.activityDao();

        // Splits have a foreign key to activities so we need a parent activity first
        ActivityEntity activity = new ActivityEntity();
        activity.setActivityId(TEST_ACTIVITY_ID);
        activity.setActivityName("Split Test Activity");
        activity.setDistance(7.23);
        activity.setElevationGain(365.0);
        activity.setElevationLoss(341.0);
        activity.setAveragePace(6.56);
        activity.setMovingTime(11520L);
        activity.setActualTime(11520L);
        activity.setCalories(487.0);
        activity.setTimestamp(1744848000L);
        activity.setIsChallenge(false);
        activityDao.insertActivity(activity);
    }

    @Test
    public void testNoSplitsExistBeforeInsertion() {
        List<SplitEntity> splits = splitDao.getSplitsForActivity(TEST_ACTIVITY_ID);
        assertTrue("No splits should exist before insertion", splits.isEmpty());
    }

    @Test
    public void testInsertSplitsAndVerifyExist() {
        List<SplitEntity> splits = buildTestSplits();
        splitDao.insertSplits(splits);

        List<SplitEntity> retrieved = splitDao.getSplitsForActivity(TEST_ACTIVITY_ID);
        assertNotNull("Splits should not be null", retrieved);
        assertEquals("Should have 3 splits", 3, retrieved.size());
    }

    @Test
    public void testSplitsAreOrderedByIndex() {
        splitDao.insertSplits(buildTestSplits());

        List<SplitEntity> retrieved = splitDao.getSplitsForActivity(TEST_ACTIVITY_ID);
        assertEquals("First split index should be 0", Integer.valueOf(0), retrieved.get(0).getSplitIndex());
        assertEquals("Second split index should be 1", Integer.valueOf(1), retrieved.get(1).getSplitIndex());
        assertEquals("Third split index should be 2", Integer.valueOf(2), retrieved.get(2).getSplitIndex());
    }

    @Test
    public void testDeleteSplitsAndVerifyGone() {
        splitDao.insertSplits(buildTestSplits());

        List<SplitEntity> inserted = splitDao.getSplitsForActivity(TEST_ACTIVITY_ID);
        assertFalse("Splits should exist before deletion", inserted.isEmpty());

        splitDao.deleteSplitsByActivityId(TEST_ACTIVITY_ID);

        List<SplitEntity> deleted = splitDao.getSplitsForActivity(TEST_ACTIVITY_ID);
        assertTrue("Splits should be gone after deletion", deleted.isEmpty());
    }

    @Test
    public void testFastestKmSplit() {
        splitDao.insertSplits(buildTestSplits());

        Double fastest = splitDao.getFastestKmSplit();
        assertNotNull("Fastest split should not be null", fastest);
        assertEquals("Fastest split pace should be 5.5", 5.5, fastest, 0.01);
    }

    @After
    public void tearDown() {
        splitDao.deleteSplitsByActivityId(TEST_ACTIVITY_ID);
        ActivityEntity activity = activityDao.getActivityById(TEST_ACTIVITY_ID);
        if (activity != null) {
            activityDao.deleteActivity(activity);
        }
    }

    private List<SplitEntity> buildTestSplits() {
        SplitEntity split1 = new SplitEntity();
        split1.setActivityId(TEST_ACTIVITY_ID);
        split1.setSplitIndex(0);
        split1.setPace(6.5);
        split1.setDistance(1.0);

        SplitEntity split2 = new SplitEntity();
        split2.setActivityId(TEST_ACTIVITY_ID);
        split2.setSplitIndex(1);
        split2.setPace(5.5);
        split2.setDistance(1.0);

        SplitEntity split3 = new SplitEntity();
        split3.setActivityId(TEST_ACTIVITY_ID);
        split3.setSplitIndex(2);
        split3.setPace(7.0);
        split3.setDistance(0.23);

        return Arrays.asList(split1, split2, split3);
    }
}