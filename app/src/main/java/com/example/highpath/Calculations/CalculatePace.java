package com.example.highpath.Calculations;

import java.util.Locale;

public class CalculatePace {

    private static int minutes(int totalSeconds) {
        return totalSeconds / 60;
    }

    private static int remainingSeconds(int totalSeconds) {
        return totalSeconds % 60;
    }

    private static double kilometres(double metres) {
        return metres / 1000.0;
    }

    public static String pace(int totalSeconds, double metres) {
        if (metres <= 0 || totalSeconds <= 0) {
            return "0:00 min/km";
        }

        double km = kilometres(metres);
        double secondsPerKm = totalSeconds / km;

        int paceMinutes = minutes((int) secondsPerKm);
        int paceSeconds = remainingSeconds((int) secondsPerKm);

        if (paceMinutes >= 100) {
            return "99:59 min/km";
        }

        return String.format(Locale.getDefault(), "%d:%02d min/km", paceMinutes, paceSeconds);
    }
}