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
    public List<SplitEntity> splits = new ArrayList<>();

    public CalculateFinalMetrics() {}

    public void calculateMetrics(Context context, String filePath) {
        try {
            // 1. Parse the GPX file into the model
            GPXData gpx = new GPXParser().parse(filePath);

            if (gpx != null && gpx.latitudes.size() >= 2) {

                // 2. Fetch corrected elevations from API
                // We check for MainThread because API calls will crash the app there
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    CorrectElevations corrector = new CorrectElevations();
                    corrector.updateGPXElevations(gpx);
                } else {
                    Log.w("FinalMetrics", "Skipping API correction: Method called on Main Thread");
                }

                // 3. Calculate Distance
                totalDistanceKM = CalculateDistance.totalDistance(gpx.latitudes, gpx.longitudes);
                totalDistanceM = totalDistanceKM * 1000;

                // 4. Calculate Elevation Gain/Loss
                double[] elevations;
                if (gpx.correctedElevations != null && !gpx.correctedElevations.isEmpty()) {
                    // Use the API-corrected data
                    elevations = CalculateElevation.calcGainLoss(gpx.correctedElevations);
                    Log.d("FinalMetrics", "Using corrected elevations (" + gpx.correctedElevations.size() + " points)");
                } else {
                    // Fallback to raw GPS data if API failed or was skipped
                    elevations = CalculateElevation.calcGainLoss(gpx.elevations);
                    Log.w("FinalMetrics", "Using raw GPX elevations - correction unavailable");
                }

                totalElevationGainM = elevations[0];
                totalElevationLossM = elevations[1];

                // 5. Calculate Time and Pace
                totalTimeOutside = CalculateTime.getTotalDurationSeconds(gpx.timestamps);
                totalTimeMoving = CalculateTime.getMovingDurationSeconds(gpx.timestamps, gpx.latitudes, gpx.longitudes);
                averagePace = CalculatePace.pace(totalTimeOutside, totalDistanceKM);

                // 6. Generate Splits
                // activityId is not known yet — ActivityBuilder sets the ID after insert
                CalculateSplits calculateSplits = new CalculateSplits();
                splits = calculateSplits.getSplits("", gpx);

                // 7. Check Geofences
                CheckGeofence geofence = new CheckGeofence();
                geofence.checkGeofence(context, gpx);

                Log.d("FinalMetrics", "Calculation Success - " + splits.size() + " splits generated");
            }
        } catch (Exception e) {
            Log.e("FinalMetrics", "Error during metrics calculation", e);
        }
    }
}