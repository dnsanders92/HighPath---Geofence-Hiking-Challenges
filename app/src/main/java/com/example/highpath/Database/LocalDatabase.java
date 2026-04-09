package com.example.highpath.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.SummitsDao;
import com.example.highpath.Database.Daos.TrackPointDao;
import com.example.highpath.Database.Daos.TrailDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Daos.ChallengesDao; // 1. Import New DAO
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.Entities.TrackPointEntity;
import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.Entities.UserEntity;
import com.example.highpath.Database.Entities.ChallengesEntity; // 2. Import New Entity

@Database(
        entities = {
                ActivityEntity.class,
                UserEntity.class,
                ChallengesEntity.class,
                SummitsEntity.class,
                TrailEntity.class,
                TrackPointEntity.class
        },
        version = 3, // 4. Increment Version (from 1 to 2)
        exportSchema = false
)
public abstract class LocalDatabase extends RoomDatabase {

    private static volatile LocalDatabase INSTANCE;

    public abstract ActivityDao activityDao();
    public abstract UserDao userDao();
    public abstract ChallengesDao challengesDao();
    public abstract SummitsDao summitsDao();
    public abstract TrailDao trailDao();
    public abstract TrackPointDao trackPointDao();

    public static LocalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocalDatabase.class,
                            "local_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}