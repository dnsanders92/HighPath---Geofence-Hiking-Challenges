package com.example.highpath.Services;

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

import com.example.highpath.Calculations.CalculateDistance;
import com.example.highpath.Calculations.CalculateElevation;
import com.example.highpath.R;
import com.example.highpath.Runnables.GpxRunnable;
import com.example.highpath.Runnables.TimeRunnable;
import com.example.highpath.Utilities.GPX.GpxFileName;
import com.example.highpath.Utilities.GPX.GPXBuilder;
import com.example.highpath.Utilities.StorageManager;

public class RecordingService extends Service {

    private String fileName, filePath;


    public static final String ACTION_METRICS_UPDATE = "com.example.highpath.RECORDING_METRICS_UPDATE";
    private static final String CHANNEL_ID = "RecordingChannel";

    private LocationService locationService;
    private GPXBuilder gpxBuilder;

    private int time = 0;
    private double totalDistance = 0, totalElevation = 0;
    private double prevLat = 0.0, prevLon = 0.0, prevEle = 0.0;
    private boolean hasPreviousPoint = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRecordingActive = false;

    private GpxRunnable gpxRunnable;
    private TimeRunnable timeRunnable;

    private final Runnable metricRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecordingActive && locationService != null) {
                double lat = locationService.getLatitude();
                double lon = locationService.getLongitude();
                double ele = locationService.getElevation();

                if (lat != 0.0 && lon != 0.0) {
                    if (hasPreviousPoint) {
                        double distance = CalculateDistance.equirectangular(prevLat, prevLon, lat, lon);
                        if (distance > 1.0) {
                            totalDistance += distance;
                            totalElevation += CalculateElevation.calcGain(ele, prevEle);
                            prevLat = lat; prevLon = lon; prevEle = ele;
                        }
                    } else {
                        prevLat = lat; prevLon = lon; prevEle = ele;
                        hasPreviousPoint = true;
                    }

                    Intent intent = new Intent(ACTION_METRICS_UPDATE);
                    intent.putExtra("distance", totalDistance);
                    intent.putExtra("elevation", totalElevation);
                    intent.putExtra("time", time);
                    intent.setPackage(getPackageName());
                    sendBroadcast(intent);
                }
            }
            handler.postDelayed(this, 2000);
        }
    };

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

        timeRunnable = new TimeRunnable(this);
        handler.post(timeRunnable);

        handler.post(metricRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hike Recording")
                .setContentText("Actively tracking your progress…")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "START_OR_RESUME":
                    isRecordingActive = true;
                    break;
                case "PAUSE":
                    isRecordingActive = false;
                    break;
            }
        } else {
            isRecordingActive = true;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(timeRunnable);
        handler.removeCallbacks(metricRunnable);
        handler.removeCallbacks(gpxRunnable);

        if (locationService != null)
            locationService.stopLocationUpdates();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Recording Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    public static void startOrResume(Context context) {
        Intent intent = new Intent(context, RecordingService.class);
        intent.setAction("START_OR_RESUME");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void pause(Context context) {
        Intent intent = new Intent(context, RecordingService.class);
        intent.setAction("PAUSE");
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, RecordingService.class));
    }
}
