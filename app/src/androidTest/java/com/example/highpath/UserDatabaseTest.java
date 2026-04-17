package com.example.highpath;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.LocalDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {

    private LocalDatabase db;
    private UserDao userDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        // In-memory database — fresh and isolated for every single test
        db = Room.inMemoryDatabaseBuilder(context, LocalDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @Test
    public void testNoUserExistsBeforeInsertion() {
        assertFalse("No user should exist before insertion", userDao.hasUser());
    }

    @Test
    public void testInsertUserAndVerifyExists() {
        UserEntity user = buildTestUser();
        userDao.insert(user);

        assertTrue("User should exist after insertion", userDao.hasUser());

        UserEntity retrieved = userDao.getSingleUser();
        assertNotNull("Retrieved user should not be null", retrieved);
        assertEquals("First name should match", "Test", retrieved.getFirstName());
        assertEquals("Surname should match", "User", retrieved.getSurname());
        assertEquals("Year of birth should match", Integer.valueOf(1990), retrieved.getYearOfBirth());
    }

    @Test
    public void testUpdateUser() {
        UserEntity user = buildTestUser();
        userDao.insert(user);

        UserEntity inserted = userDao.getSingleUser();
        assertNotNull(inserted);

        inserted.setFirstName("Updated");
        inserted.setTotalDistanceKm(100);
        userDao.update(inserted);

        UserEntity updated = userDao.getSingleUser();
        assertNotNull(updated);
        assertEquals("First name should be updated", "Updated", updated.getFirstName());
        assertEquals("Distance should be updated", Integer.valueOf(100), updated.getTotalDistanceKm());
    }

    @After
    public void tearDown() {
        db.close();
    }

    private UserEntity buildTestUser() {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setSurname("User");
        user.setMonthOfBirth("January");
        user.setYearOfBirth(1990);
        user.setTotalDistanceKm(0);
        user.setTotalElevationM(0);
        user.setCompletedActivities(0);
        user.setCompletedChallenges(0);
        user.setTotalTimeOutsideSeconds(0L);
        user.setTotalMovingTimeSeconds(0L);
        return user;
    }
}