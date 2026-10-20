package com.example.highpath.Runnables;

import android.os.Handler;
import android.util.Log;

import com.example.highpath.Services.LocationService;
import com.example.highpath.Utilities.GPX.GpxBuilder;
import com.example.highpath.Utilities.GPX.LiveTrackManager;

public class GpxRunnable implements Runnable {

    private final LocationService locationService;
    private final GpxBuilder gpxBuilder;
    private final Handler handler;

    public GpxRunnable(Handler handler, LocationService locationService, GpxBuilder gpxBuilder) {
        this.handler = handler;
        this.locationService = locationService;
        this.gpxBuilder = gpxBuilder;
    }

    @Override
    public void run() {
        double lat = locationService.getLatitude();
        double lon = locationService.getLongitude();
        double ele = locationService.getElevation();

        if (lat != 0.0 && lon != 0.0) {
            try {
                gpxBuilder.trackPoint(lat, lon, ele);
                LiveTrackManager.getInstance().addPoint(lat, lon);
                Log.d("GpxRunnable", "Point Saved: " + lat + ", " + lon);
            } catch (Exception e) {
                Log.e("GpxRunnable", "Write error: " + e.getMessage());
            }
        }

        handler.postDelayed(this, 2000);
    }
}