package com.example.highpath.Utilities.GPX;

import java.util.ArrayList;
import java.util.List;

public class LiveTrackManager {

    private static LiveTrackManager instance;

    private final List<Double> latitudes = new ArrayList<>();
    private final List<Double> longitudes = new ArrayList<>();

    private LiveTrackManager() {}

    public static synchronized LiveTrackManager getInstance() {
        if (instance == null) {
            instance = new LiveTrackManager();
        }
        return instance;
    }

    public synchronized void addPoint(double lat, double lon) {
        latitudes.add(lat);
        longitudes.add(lon);
    }

    public synchronized List<Double> getLatitudes() {
        return new ArrayList<>(latitudes);
    }

    public synchronized List<Double> getLongitudes() {
        return new ArrayList<>(longitudes);
    }

    public synchronized boolean hasPoints() {
        return latitudes.size() >= 2;
    }

    public synchronized void clear() {
        latitudes.clear();
        longitudes.clear();
    }
}