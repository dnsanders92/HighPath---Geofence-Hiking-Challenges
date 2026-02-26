package com.example.highpath.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler; // Added
import android.os.IBinder;
import android.os.Looper;  // Added
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.highpath.Runnables.GpxRunnable; // Added
import com.example.highpath.Utilities.GPX.GpxBuilder;

import java.io.IOException;

public class RecordingService extends Service {

    private static final String TAG = "RecordingService";
    private static final String CHANNEL_ID = "record_channel";

    private static final String EXTRA_FILE_PATH = "extra_file_path";
    private static final String EXTRA_FILE_NAME = "extra_file_name";

    private String filePath, fileName;
    private GpxBuilder gpxBuilder;

    // --- Added for Encapsulation ---
    private LocationService locationService;
    private GpxRunnable gpxRunnable;
    private final Handler handler = new Handler(Looper.getMainLooper());
    // -------------------------------

    public static void start(Context context, String filePath, String fileName) {
        Intent intent = new Intent(context, RecordingService.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_FILE_NAME, fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, RecordingService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            filePath = intent.getStringExtra(EXTRA_FILE_PATH);
            fileName = intent.getStringExtra(EXTRA_FILE_NAME);

            if (filePath != null && fileName != null) {
                gpxBuilder = new GpxBuilder(filePath, fileName);

                try {
                    gpxBuilder.gpxHeader();
                    Log.d(TAG, "GPX Header written successfully.");

                    // --- IMPLEMENTATION START ---
                    // 1. Initialize GPS helper
                    locationService = new LocationService(this);
                    locationService.startLocationUpdates();

                    // 2. Initialize the encapsulated Runnable
                    gpxRunnable = new GpxRunnable(handler, locationService, gpxBuilder);

                    // 3. Kick off the 2-second loop
                    handler.post(gpxRunnable);
                    // --- IMPLEMENTATION END ---

                } catch (Exception e) {
                    Log.e(TAG, "Error initializing GPX header", e);
                }
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording Route")
                .setContentText("Saving to: " + fileName)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // --- Added Cleanup ---
        // 1. Stop the loop timer immediately
        if (handler != null && gpxRunnable != null) {
            handler.removeCallbacks(gpxRunnable);
        }

        // 2. Stop the GPS hardware to save battery
        if (locationService != null) {
            locationService.stopLocationUpdates();
        }

        // 3. Existing Footer Logic
        if (gpxBuilder != null) {
            try {
                gpxBuilder.gpxFooter();
                Log.d(TAG, "GPX Footer written. File closed.");
            } catch (IOException e) {
                Log.e(TAG, "Error writing GPX footer", e);
            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Recording Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) { manager.createNotificationChannel(channel); }
        }
    }
}