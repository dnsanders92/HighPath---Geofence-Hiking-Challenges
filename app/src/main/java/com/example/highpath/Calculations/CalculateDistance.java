package com.example.highpath.Calculations;

public class CalculateDistance {

    private static final double R = 6371000;

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
}
