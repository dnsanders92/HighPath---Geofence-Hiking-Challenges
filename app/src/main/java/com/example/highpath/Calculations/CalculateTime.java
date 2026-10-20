package com.example.highpath.Calculations;

import java.time.Instant;
import java.util.List;

public class CalculateTime {

    private static final double MOVEMENT_THRESHOLD_KILOMETERS = 0.001;

    // total time from first to last timestamp
    public static long getTotalDurationSeconds(List<String> gpxTimestamps) {
        if (gpxTimestamps == null || gpxTimestamps.size() < 2) return 0;

        long firstPointSeconds = Instant.parse(gpxTimestamps.get(0)).getEpochSecond();
        long lastPointSeconds  = Instant.parse(gpxTimestamps.get(gpxTimestamps.size() - 1)).getEpochSecond();

        return lastPointSeconds - firstPointSeconds;
    }

    // moving time only, ignores segments under threshold
    public static long getMovingDurationSeconds(List<String> gpxTimestamps,
                                                List<Double> latitudes,
                                                List<Double> longitudes) {

        if (gpxTimestamps == null || gpxTimestamps.size() < 2) return 0;

        long accumulatedMovingSeconds = 0;

        // loop through all segments
        for (int i = 1; i < gpxTimestamps.size(); i++) {

            // distance for this segment
            double segmentDistance = CalculateDistance.equirectangular(
                    latitudes.get(i - 1), longitudes.get(i - 1),
                    latitudes.get(i),     longitudes.get(i)
            );

            // only count time if movement is above threshold
            if (segmentDistance > MOVEMENT_THRESHOLD_KILOMETERS) {

                long segmentStartSeconds = Instant.parse(gpxTimestamps.get(i - 1)).getEpochSecond();
                long segmentEndSeconds   = Instant.parse(gpxTimestamps.get(i)).getEpochSecond();

                long segmentDuration = segmentEndSeconds - segmentStartSeconds;

                accumulatedMovingSeconds += segmentDuration;
            }
        }

        return accumulatedMovingSeconds;
    }
}
