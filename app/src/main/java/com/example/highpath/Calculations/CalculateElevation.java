package com.example.highpath.Calculations;

public class CalculateElevation {

    public static double calcGain(double currentEle, double previousEle) {
        double diff = currentEle - previousEle;
        // Ignore descent and tiny noise (< 1m)
        return diff > 1.0 ? diff : 0.0;
    }

    public static double calcLoss(double previousEle, double currentEle) {
        double diff = previousEle - currentEle;
        // Ignore descent and tiny noise (< 1m)
        return diff > 1.0 ? diff : 0.0;
    }
}
