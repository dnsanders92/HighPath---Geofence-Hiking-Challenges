package com.example.highpath.Calculations;

public class CalculateCalories {

    public static double calculate(long totalTimeSeconds, double distanceKM, double elevationGainM) {
        if (totalTimeSeconds <= 0 || distanceKM <= 0) return 0.0;

        double timeHours = totalTimeSeconds / 3600.0;
        double calories = 6.0 * 70.0 * timeHours;

        return Math.round(calories * 100.0) / 100.0;
    }
}