package com.example.highpath.Calculations;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Utilities.CheckGeofence;
import com.example.highpath.Models.GPXData;
import com.example.highpath.Utilities.CorrectElevations;
import com.example.highpath.Utilities.GPX.GPXParser;

import java.util.ArrayList;
import java.util.List;

public class CalculateFinalMetrics {

    public double totalDistanceKM = 0.0;
    public double totalDistanceM = 0.0;
    public double totalElevationGainM = 0.0;
    public double totalElevationLossM = 0.0;
    public long totalTimeOutside = 0;
    public long totalTimeMoving = 0;
    public double averagePace = 0.0;
    public double calories = 0.0;
    public List<SplitEntity> splits = new ArrayList<>();
    public GPXData gpx = null;

    public CalculateFinalMetrics() {}

    public void calculateMetrics(Context context, String filePath) {
        try {
            // parse gpx file
            gpx = new GPXParser().parse(filePath);

            if (gpx != null && gpx.latitudes.size() >= 2) {

                // correct elevations API
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    try {
                        CorrectElevations corrector = new CorrectElevations();
                        corrector.updateGPXElevations(gpx);
                    } catch (Exception e) {
                        Log.e("FinalMetrics", "Elevation correction failed", e);
                    }
                } else {
                    Log.w("FinalMetrics", "Skipping correction on main thread");
                }

                // calculate distance
                totalDistanceKM = CalculateDistance.totalDistance(gpx.latitudes, gpx.longitudes);
                totalDistanceM = totalDistanceKM * 1000;

                // calculate elevation gain/loss
                double[] elevations;
                if (gpx.correctedElevations != null && !gpx.correctedElevations.isEmpty()) {
                    elevations = CalculateElevation.calcGainLoss(gpx.correctedElevations);
                } else {
                    elevations = CalculateElevation.calcGainLoss(gpx.elevations);
                }

                totalElevationGainM = elevations[0];
                totalElevationLossM = elevations[1];

                // calculate time + pace
                totalTimeOutside = CalculateTime.getTotalDurationSeconds(gpx.timestamps);
                totalTimeMoving = CalculateTime.getMovingDurationSeconds(gpx.timestamps, gpx.latitudes, gpx.longitudes);
                averagePace = CalculatePace.pace(totalTimeOutside, totalDistanceKM);

                // generate splits
                CalculateSplits calculateSplits = new CalculateSplits();
                splits = calculateSplits.getSplits("", gpx);

                // calculate calories
                calories = CalculateCalories.calculate(totalTimeOutside, totalDistanceKM, totalElevationGainM);

                // check geofences
                CheckGeofence geofence = new CheckGeofence();
                geofence.checkGeofence(context, gpx);

                Log.d("FinalMetrics", "Calculation success - " + splits.size() + " splits generated");
            }
        } catch (Exception e) {
            Log.e("FinalMetrics", "Error during metrics calculation", e);
        }
    }
}
