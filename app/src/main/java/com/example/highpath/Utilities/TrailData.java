package com.example.highpath.Utilities;

import java.util.ArrayList;
import java.util.List;

public class TrailData {

    // Making these static allows RecordFragment to access them via TrailData.latitudes
    public static List<Double> latitudes = new ArrayList<>();
    public static List<Double> longitudes = new ArrayList<>();
    public static List<Double> elevations = new ArrayList<>();

    public double minLat, maxLat, minLon, maxLon;

    public void loadTrailData(List<Double> trailLatitudes,
                              List<Double> trailLongitudes,
                              List<Double> trailElevations) {

        latitudes.addAll(trailLatitudes);
        longitudes.addAll(trailLongitudes);
        elevations.addAll(trailElevations);

    }

    // Helper to check if data is ready
    public static boolean hasData() {
        return !latitudes.isEmpty();
    }
}