package com.example.highpath.Calculations;

import android.annotation.SuppressLint;

public class CalculatePace {

    public static double pace(long movingDurationSeconds, double totalDistanceKM) {
        // Use a tiny epsilon or require strictly positive distance.
        if (totalDistanceKM <= 0.0 || movingDurationSeconds <= 0) {
            return 0.0;
        }
        double totalMinutes = movingDurationSeconds / 60.0;
        return totalMinutes / totalDistanceKM;
    }

    @SuppressLint("DefaultLocale")
    public static String formatPaceToString(double rawPace) {
        if (rawPace <= 0) {
            return "00:00";
        }

        int minutes = (int) Math.floor(rawPace);
        // Round seconds to nearest integer
        int seconds = (int) Math.round((rawPace - minutes) * 60.0);

        // Normalize if rounding produced 60 seconds
        if (seconds == 60) {
            minutes += 1;
            seconds = 0;
        }

        return String.format("%02d:%02d", minutes, seconds);
    }
}
