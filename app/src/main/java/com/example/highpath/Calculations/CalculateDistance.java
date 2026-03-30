package com.example.highpath.Calculations;

import java.util.List;

public class CalculateDistance {

    private static final double R = 6371.0;

    public static double equirectangular(double previousLat, double previousLon,
                                         double currentLat, double currentLon) {

        double lat1 = Math.toRadians(previousLat);
        double lat2 = Math.toRadians(currentLat);
        double lon1 = Math.toRadians(previousLon);
        double lon2 = Math.toRadians(currentLon);

        double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2.0);
        double y = (lat2 - lat1);

        return R * Math.sqrt(x * x + y * y);
    }

    public static double totalDistance(List<Double> latitudes, List<Double> longitudes) {
        double totalDistanceKM = 0;

        // Safety check: ensure lists aren't null and have at least 2 points
        if (latitudes == null || longitudes == null || latitudes.size() < 2) {
            return 0.0;
        }

        for (int i = 1; i < latitudes.size(); i++) {

            // Correctly reference the PREVIOUS point
            double prevLat = latitudes.get(i - 1);
            double prevLon = longitudes.get(i - 1);

            // Correctly reference the CURRENT point
            double currLat = latitudes.get(i);
            double currLon = longitudes.get(i);

            totalDistanceKM += equirectangular(prevLat, prevLon, currLat, currLon);
        }

        // Round to 2 decimal places before returning
        return Math.round(totalDistanceKM * 100.0) / 100.0;
    }
}