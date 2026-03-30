package com.example.highpath.Calculations;

import android.annotation.SuppressLint;

public class CalculatePace {

    /**
     * Calculates the raw pace in minutes per kilometer.
     * Example: 500 seconds over 1km = 8.33 minutes/km.
     */
    public static double pace(long movingDurationSeconds, double totalDistanceKM) {

        // Safety: Prevent division by zero or negative results
        if (totalDistanceKM <= 0.001 || movingDurationSeconds <= 0) {
            return 0.0;
        }

        // 1. Convert seconds to minutes (using 60.0 to maintain decimal precision)
        double totalMinutes = movingDurationSeconds / 60.0;

        // 2. Return Minutes divided by Kilometers
        return totalMinutes / totalDistanceKM;
    }

    /**
     * Converts a raw pace (e.g., 8.5) into a readable "MM:SS" string (e.g., "08:30").
     */
    @SuppressLint("DefaultLocale")
    public static String formatPaceToString(double rawPace) {

        if (rawPace <= 0) {
            return "00:00";
        }

        // The whole number is the minutes
        int minutes = (int) rawPace;

        // The decimal remainder multiplied by 60 gives the seconds
        int seconds = (int) ((rawPace - minutes) * 60);

        return String.format("%02d:%02d", minutes, seconds);
    }
}