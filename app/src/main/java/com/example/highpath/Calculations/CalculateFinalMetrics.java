package com.example.highpath.Calculations;

import android.util.Log;
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

    public void calculateMetrics(String filePath) {
        try {
            // 1. Parse the GPX file
            GPXData gpx = new GPXParser().parse(filePath);

            // 2. CHECK if gpx is null and has data BEFORE calling methods on it
            if (gpx != null && gpx.latitudes.size() >= 2) {

                // Calculate Distance
                totalDistanceKM = CalculateDistance.totalDistance(gpx.latitudes, gpx.longitudes);

                // Calculate Elevation. [0] = elevation gain, [1] = elevation loss.
                double[] elevations = CalculateElevation.calcGainLoss(gpx.elevations);
                totalElevationGainM = elevations[0];
                totalElevationLossM = elevations[1];

                totalTimeOutside = CalculateTime.getTotalDurationSeconds(gpx.timestamps);
                totalTimeMoving = CalculateTime.getMovingDurationSeconds(gpx.timestamps, gpx.latitudes, gpx.longitudes);

                totalDistanceM = totalDistanceKM * 1000;

                averagePace = CalculatePace.pace(totalTimeOutside, totalDistanceM);

                Log.d("FinalMetrics", "Calculation Success: " + totalDistanceKM + "km");
            } else {
                Log.w("FinalMetrics", "GPX data was null or too short.");
            }

        } catch (Exception e) {
            Log.e("FinalMetrics", "Error parsing GPX file for metrics", e);
            // Values remain 0.0 due to initializations
        }
    }
}