package com.example.highpath.Runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.example.highpath.Calculations.CalculateDistance;
import com.example.highpath.Calculations.CalculateElevation;
import com.example.highpath.Services.LocationService;

public class MetricsRunnable implements Runnable {

    public static final String ACTION_METRICS_UPDATE = "com.example.highpath.METRICS_UPDATE";

    private final Context context;
    private final Handler handler;
    private final LocationService locationService;

    private double previousLat, previousLon, previousElevation;
    public double totalDistance = 0.0;
    public double totalElevationGain = 0.0;
    private boolean isFirstRun = true;

    public MetricsRunnable(Context context, Handler handler, LocationService locationService) {
        this.context = context;
        this.handler = handler;
        this.locationService = locationService;

        this.previousLat = locationService.getLatitude();
        this.previousLon = locationService.getLongitude();
        this.previousElevation = locationService.getElevation();
    }

    @Override
    public void run() {
        double currentLat = locationService.getLatitude();
        double currentLon = locationService.getLongitude();
        double currentElevation = locationService.getElevation();

        if (currentLat != 0.0 && currentLon != 0.0) {
            if (isFirstRun) {
                previousLat = currentLat;
                previousLon = currentLon;
                previousElevation = currentElevation;
                isFirstRun = false;
            } else {
                double stepDistance = CalculateDistance.equirectangular(previousLat, previousLon, currentLat, currentLon);
                if (stepDistance > 0.002) {
                    totalDistance += stepDistance;
                }

                double stepGain = CalculateElevation.calcGain(currentElevation, previousElevation);
                totalElevationGain += stepGain;

                previousLat = currentLat;
                previousLon = currentLon;
                previousElevation = currentElevation;
            }
        }

        Intent intent = new Intent(ACTION_METRICS_UPDATE);
        intent.putExtra("distance", totalDistance);
        intent.putExtra("elevation", totalElevationGain);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);

        handler.postDelayed(this, 2000);
    }
}