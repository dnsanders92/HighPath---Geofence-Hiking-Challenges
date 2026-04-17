package com.example.highpath.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.ChallengesDao;
import com.example.highpath.Database.Daos.SplitDao;
import com.example.highpath.Database.Daos.SummitsDao;
import com.example.highpath.Database.Daos.TrackPointDao;
import com.example.highpath.Database.Daos.TrailDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.Entities.TrackPointEntity;
import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.Entities.UserEntity;

@Database(
        entities = {
                ActivityEntity.class,
                UserEntity.class,
                ChallengesEntity.class,
                SummitsEntity.class,
                TrailEntity.class,
                TrackPointEntity.class,
                SplitEntity.class
        },
        version = 4,
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
    public abstract SplitDao splitDao();

    public static LocalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LocalDatabase.class,
                                    "local_database"
                            )
                            .createFromAsset("database/highpath_database.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}