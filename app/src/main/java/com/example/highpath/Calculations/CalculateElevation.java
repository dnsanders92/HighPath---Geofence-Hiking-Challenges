package com.example.highpath.Calculations;

import java.util.List;

public class CalculateElevation {

    public static double calcGain(double currentEle, double previousEle) {
        double diff = currentEle - previousEle;
        // Ignore descent and tiny noise (< 1m)
        return diff > 1.0 ? diff : 0.0;
    }

    public static double calcLoss(double previousEle, double currentEle) {
        double diff = previousEle - currentEle;
        // Ignore ascent and tiny noise (< 1m)
        return diff > 1.0 ? diff : 0.0;
    }

    public static double[] calcGainLoss(List<Double> altitudes) {

        // [0] = totalElevationGain, [1] = totalElevationLoss
        double[] elevations = new double[2];

        // Safety: Ensure list exists and isn't just one point
        if (altitudes == null || altitudes.size() < 2) {
            return elevations; // returns [0.0, 0.0]
        }

        for (int i = 1; i < altitudes.size(); i++) {
            double previousEle = altitudes.get(i - 1);
            double currentEle = altitudes.get(i);

            if (currentEle > previousEle) {
                elevations[0] += calcGain(currentEle, previousEle);
            } else {
                elevations[1] += calcLoss(previousEle, currentEle);
            }
        }

        // Round results to 2 decimal places
        elevations[0] = Math.round(elevations[0] * 100.0) / 100.0;
        elevations[1] = Math.round(elevations[1] * 100.0) / 100.0;

        return elevations;
    }
}