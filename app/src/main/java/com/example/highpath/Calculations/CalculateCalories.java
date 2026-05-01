package com.example.highpath.Calculations;

public class CalculateCalories {

    // calculate calories burned
    public static double calculate(long totalTimeSeconds, double distanceKM, double elevationGainM) {

        // validation
        if (totalTimeSeconds <= 0 || distanceKM <= 0) return 0.0;

        // convert time to hours
        double timeHours = totalTimeSeconds / 3600.0;

        // simple calorie formula
        double calories = 6.0 * 70.0 * timeHours;

        // round to 2 decimals
        return Math.round(calories * 100.0) / 100.0;
    }
}
