package com.example.highpath.Runnables;

import android.os.Handler;
import android.util.Log;
import com.example.highpath.Services.LocationService;
import com.example.highpath.Utilities.GPX.GpxBuilder;

public class GpxRunnable implements Runnable {

    private final LocationService locationService;
    private final GpxBuilder gpxBuilder;
    private final Handler handler;

    // The Constructor: This is how the Service "hands over" the tools
    public GpxRunnable(Handler handler, LocationService locationService, GpxBuilder gpxBuilder) {
        this.handler = handler;
        this.locationService = locationService;
        this.gpxBuilder = gpxBuilder;
    }

    @Override
    public void run() {
        // Pull location from the helper
        double lat = locationService.getLatitude();
        double lon = locationService.getLongitude();
        double ele = locationService.getElevation();

        if (lat != 0.0 && lon != 0.0) {
            try {
                // Tell the builder to write the point
                gpxBuilder.trackPoint(lat, lon, ele);
                Log.d("GpxRunnable", "Point Saved: " + lat + ", " + lon);
            } catch (Exception e) {
                Log.e("GpxRunnable", "Write error: " + e.getMessage());
            }
        }

        // Keep the loop going every 2 seconds
        handler.postDelayed(this, 2000);
    }
}