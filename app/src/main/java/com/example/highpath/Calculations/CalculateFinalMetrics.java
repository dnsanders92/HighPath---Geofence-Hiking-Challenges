package com.example.highpath.Calculations;

import android.content.Context;
import android.util.Log;
import com.example.highpath.Utilities.CheckGeofence;
import com.example.highpath.Utilities.GPX.GPXData;
import com.example.highpath.Utilities.GPX.GPXParser;

public class CalculateFinalMetrics {

    public double totalDistanceKM = 0.0;
    public double totalDistanceM = 0.0;
    public double totalElevationGainM = 0.0;
    public double totalElevationLossM = 0.0;
    public long totalTimeOutside = 0;
    public long totalTimeMoving = 0;
    public double averagePace = 0.0;

    // Empty constructor keeps other classes from breaking
    public CalculateFinalMetrics() {}

    public void calculateMetrics(Context context, String filePath) {
        try {
            GPXData gpx = new GPXParser().parse(filePath);

            if (gpx != null && gpx.latitudes.size() >= 2) {
                // 1. Core Calculations
                totalDistanceKM = CalculateDistance.totalDistance(gpx.latitudes, gpx.longitudes);
                totalDistanceM = totalDistanceKM * 1000;

                double[] elevations = CalculateElevation.calcGainLoss(gpx.elevations);
                totalElevationGainM = elevations[0];
                totalElevationLossM = elevations[1];

                totalTimeOutside = CalculateTime.getTotalDurationSeconds(gpx.timestamps);
                totalTimeMoving = CalculateTime.getMovingDurationSeconds(gpx.timestamps, gpx.latitudes, gpx.longitudes);
                averagePace = CalculatePace.pace(totalTimeOutside, totalDistanceM);

                CheckGeofence geofence = new CheckGeofence();
                geofence.checkGeofence(context, gpx);

                Log.d("FinalMetrics", "Calculation Success");
            }
        } catch (Exception e) {
            Log.e("FinalMetrics", "Error parsing GPX", e);
        }
    }
}