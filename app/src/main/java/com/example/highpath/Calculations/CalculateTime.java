package com.example.highpath.Calculations;

import java.time.Instant;
import java.util.List;

public class CalculateTime {

    // Literal threshold: 0.0005 kilometers is exactly 1 meters
    private static final double MOVEMENT_THRESHOLD_KILOMETERS = 0.001;

    public static long getTotalDurationSeconds(List<String> gpxTimestamps) {
        if (gpxTimestamps == null || gpxTimestamps.size() < 2) return 0;

        long firstPointSeconds = Instant.parse(gpxTimestamps.get(0)).getEpochSecond();
        long lastPointSeconds = Instant.parse(gpxTimestamps.get(gpxTimestamps.size() - 1)).getEpochSecond();

        return lastPointSeconds - firstPointSeconds;
    }

    public static long getMovingDurationSeconds(List<String> gpxTimestamps, List<Double> latitudes, List<Double> longitudes) {

        if (gpxTimestamps == null || gpxTimestamps.size() < 2) return 0;

        long accumulatedMovingSeconds = 0;

        for (int i = 1; i < gpxTimestamps.size(); i++) {

            // 1. Calculate how much distance was covered in this segment
            double segmentDistance = CalculateDistance.equirectangular(
                    latitudes.get(i - 1), longitudes.get(i - 1),
                    latitudes.get(i), longitudes.get(i)
            );

            // 2. Only process time if the distance is greater than our 0.5m threshold
            if (segmentDistance > MOVEMENT_THRESHOLD_KILOMETERS) {

                long segmentStartSeconds = Instant.parse(gpxTimestamps.get(i - 1)).getEpochSecond();
                long segmentEndSeconds = Instant.parse(gpxTimestamps.get(i)).getEpochSecond();

                long segmentDuration = segmentEndSeconds - segmentStartSeconds;

                accumulatedMovingSeconds += segmentDuration;
            }
        }

        return accumulatedMovingSeconds;
    }
}