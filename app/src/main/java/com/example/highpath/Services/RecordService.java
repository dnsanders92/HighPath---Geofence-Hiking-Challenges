package com.example.highpath.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.highpath.Runnables.GpxRunnable;
import com.example.highpath.Utilities.GPX.GPXBuilder;
import com.example.highpath.Utilities.GPX.GpxFileName;
import com.example.highpath.Utilities.StorageManager;

public class RecordService extends Service {

    private static final String CHANNEL_ID = "record_channel";

    private String filePath, fileName;

    GPXBuilder gpxBuilder;
    LocationService locationService;
    GpxRunnable gpxRunnable;

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Generate filename
        GpxFileName gpxFileName = new GpxFileName(this);
        fileName = gpxFileName.generate();

        // Resolve GPX path
        filePath = StorageManager.getGpxPath(this);

        // Start location updates
        locationService = new LocationService(this);
        locationService.startLocationUpdates();

        // Create GPX builder
        gpxBuilder = new GPXBuilder();

        try {
            gpxBuilder.gpxHeader(filePath, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create and start GPX runnable
        gpxRunnable = new GpxRunnable(
                locationService,
                gpxBuilder,
                filePath,
                fileName
        );

        handler.post(gpxRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording")
                .setContentText("Service is running…")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recording Service",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    public static void start(Context context) {
        Intent intent = new Intent(context, RecordService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private void stopRecording() {

        // 1. Stop runnable loop
        handler.removeCallbacks(gpxRunnable);

        // 2. Stop location updates
        if (locationService != null) {
            locationService.stopLocationUpdates();
        }

        // 3. Write GPX footer
        try {
            gpxBuilder.gpxFooter(filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Stop foreground + service
        stopForeground(true);
        stopSelf();
    }

}
