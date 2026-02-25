package com.example.highpath.Runnables;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.highpath.Services.LocationService;
import com.example.highpath.Utilities.GPX.GPXBuilder;

public class GpxRunnable implements Runnable {

    private final String filePath;
    private final String fileName;

    private final LocationService locationService;
    private final GPXBuilder gpxBuilder;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public GpxRunnable(LocationService locationService,
                       GPXBuilder gpxBuilder,
                       String filePath,
                       String fileName) {

        this.locationService = locationService;
        this.gpxBuilder = gpxBuilder;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        double lat = locationService.getLatitude();
        double lon = locationService.getLongitude();
        double ele = locationService.getElevation();

        if (lat != 0.0 && lon != 0.0) {
            try {
                // FIXED: Correct parameter order
                gpxBuilder.trackPoint(lat, lon, ele, fileName, filePath);
            } catch (Exception e) {
                Log.e("GPX", "GPX Write Error: " + e.getMessage());
            }
        }

        handler.postDelayed(this, 2000);
    }
}
