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
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.highpath.Database.DatabaseServices.ActivityBuilder;
import com.example.highpath.Runnables.GpxRunnable;
import com.example.highpath.Runnables.PaceRunnable;
import com.example.highpath.Runnables.TimeRunnable;
import com.example.highpath.Runnables.MetricsRunnable;
import com.example.highpath.Utilities.GPX.GpxBuilder;

import java.io.IOException;

public class RecordingService extends Service {

    private static final String TAG = "RecordingService";
    private static final String CHANNEL_ID = "record_channel";

    // Broadcast Action
    public static final String ACTION_RECORDING_STATUS = "com.example.highpath.RECORDING_STATUS";

    // Static flag for instant checking
    public static boolean isRecording = false;

    private static final String EXTRA_FILE_PATH = "extra_file_path";
    private static final String EXTRA_FILE_NAME = "extra_file_name";

    private String filePath, fileName;
    private GpxBuilder gpxBuilder;

    private LocationService locationService;
    private GpxRunnable gpxRunnable;
    private TimeRunnable timeRunnable;
    private MetricsRunnable metricsRunnable;
    private PaceRunnable paceRunnable;
    private final Handler handler = new Handler(Looper.getMainLooper());

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

                    locationService = new LocationService(this);
                    locationService.startLocationUpdates();

                    gpxRunnable = new GpxRunnable(handler, locationService, gpxBuilder);
                    timeRunnable = new TimeRunnable(this, handler);
                    metricsRunnable = new MetricsRunnable(this, handler, locationService);
                    paceRunnable = new PaceRunnable(this, handler, timeRunnable, metricsRunnable);

                    handler.post(gpxRunnable);
                    handler.post(timeRunnable);
                    handler.post(metricsRunnable);
                    handler.post(paceRunnable);

                    // Update status and broadcast
                    isRecording = true;
                    sendStatusBroadcast(true);

                } catch (Exception e) {
                    Log.e(TAG, "Error initializing Service", e);
                }
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording Route")
                .setContentText("Tracking metrics...")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void sendStatusBroadcast(boolean status) {
        Intent intent = new Intent(ACTION_RECORDING_STATUS);
        intent.putExtra("isRecording", status);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        isRecording = false;
        sendStatusBroadcast(false);

        if (handler != null) {
            if (gpxRunnable != null) handler.removeCallbacks(gpxRunnable);
            if (timeRunnable != null) handler.removeCallbacks(timeRunnable);
            if (metricsRunnable != null) handler.removeCallbacks(metricsRunnable);
            if (paceRunnable != null) handler.removeCallbacks(paceRunnable);
        }

        if (locationService != null) locationService.stopLocationUpdates();

        if (gpxBuilder != null) {
            try {
                gpxBuilder.gpxFooter();

                // Call the builder here
                new ActivityBuilder().insertActivity(getApplicationContext(), filePath);

            } catch (IOException e) {
                Log.e(TAG, "Error closing GPX", e);
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
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}