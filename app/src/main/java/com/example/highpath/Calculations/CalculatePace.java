package com.example.highpath.Calculations;

public class CalculatePace {

    private int minutes(int totalSeconds) {
        return totalSeconds / 60;
    }

    private int remainingSeconds(int totalSeconds) {
        return totalSeconds % 60;
    }

    private double kilometres(double metres) {
        return metres / 1000;
    }

    // Returns pace as "mm:ss min/km"
    public String pace(int totalSeconds, double metres) {

        if (metres <= 0) {
            return "0:00";
        }

        double km = kilometres(metres);

        // seconds per kilometre
        double secondsPerKm = totalSeconds / km;

        int paceMinutes = minutes((int) secondsPerKm);
        int paceSeconds = remainingSeconds((int) secondsPerKm);

        // format seconds to always be 2 digits
        return String.format("%d:%02d min/km", paceMinutes, paceSeconds);
    }
}

