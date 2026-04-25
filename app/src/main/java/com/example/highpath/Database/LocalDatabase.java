package com.example.highpath.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.highpath.Database.Daos.ActivityDao;
import com.example.highpath.Database.Daos.ChallengeItemDao;
import com.example.highpath.Database.Daos.ChallengesDao;
import com.example.highpath.Database.Daos.SplitDao;
import com.example.highpath.Database.Daos.SummitsDao;
import com.example.highpath.Database.Daos.TrackPointDao;
import com.example.highpath.Database.Daos.TrailDao;
import com.example.highpath.Database.Daos.UserDao;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.ChallengeItemEntity;
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
                ChallengeItemEntity.class,
                SummitsEntity.class,
                TrailEntity.class,
                TrackPointEntity.class,
                SplitEntity.class
        },
        version = 5,
        exportSchema = false
)
public abstract class LocalDatabase extends RoomDatabase {

    private static volatile LocalDatabase INSTANCE;

    public abstract ActivityDao activityDao();
    public abstract UserDao userDao();
    public abstract ChallengesDao challengesDao();
    public abstract ChallengeItemDao challengeItemDao();
    public abstract SummitsDao summitsDao();
    public abstract TrailDao trailDao();
    public abstract TrackPointDao trackPointDao();
    public abstract SplitDao splitDao();

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `challenge_items` (" +
                            "`item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`challenge_id` INTEGER NOT NULL, " +
                            "`summit_id` INTEGER, " +
                            "`item_name` TEXT, " +
                            "`latitude` REAL, " +
                            "`longitude` REAL, " +
                            "`is_completed` INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(`challenge_id`) REFERENCES `challenges`(`challenge_id`) ON DELETE CASCADE, " +
                            "FOREIGN KEY(`summit_id`) REFERENCES `summits`(`summit_id`) ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_challenge_items_challenge_id` ON `challenge_items`(`challenge_id`)"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_challenge_items_summit_id` ON `challenge_items`(`summit_id`)"
            );
        }
    };

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
                            .addMigrations(MIGRATION_4_5)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}