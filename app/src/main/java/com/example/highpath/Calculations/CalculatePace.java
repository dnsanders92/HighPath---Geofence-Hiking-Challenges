package com.example.highpath.Calculations;

import android.annotation.SuppressLint;

public class CalculatePace {

    // calculate raw pace (min per km)
    public static double pace(long movingDurationSeconds, double totalDistanceKM) {
        if (totalDistanceKM <= 0.0 || movingDurationSeconds <= 0) return 0.0;

        double totalMinutes = movingDurationSeconds / 60.0;
        return totalMinutes / totalDistanceKM;
    }

    // format pace as mm:ss
    @SuppressLint("DefaultLocale")
    public static String formatPaceToString(double rawPace) {
        if (rawPace <= 0) return "00:00";

        int minutes = (int) Math.floor(rawPace);
        int seconds = (int) Math.round((rawPace - minutes) * 60.0);

        // handle rounding overflow
        if (seconds == 60) {
            minutes += 1;
            seconds = 0;
        }

        return String.format("%02d:%02d", minutes, seconds);
    }
}
