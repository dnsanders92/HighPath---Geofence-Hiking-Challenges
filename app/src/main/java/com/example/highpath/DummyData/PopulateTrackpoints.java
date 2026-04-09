package com.example.highpath.DummyData;

import com.example.highpath.Database.Daos.TrailDao;
import com.example.highpath.Database.Entities.TrackPointEntity;

import java.util.ArrayList;
import java.util.List;

public class PopulateTrackpoints {

    public static void insertDummyPoints(TrailDao trailDao) {
        List<TrackPointEntity> allPoints = new ArrayList<>();

        // Trail ID, Center Lat, Center Lon, Base Elevation
        allPoints.addAll(generatePoints("1", 57.507, -6.174, 100));  // Old Storr
        allPoints.addAll(generatePoints("2", 56.796, -5.003, 50));   // Ben Nevis
        allPoints.addAll(generatePoints("3", 54.569, -3.172, 120));  // Catbells
        allPoints.addAll(generatePoints("4", 57.643, -6.273, 200));  // Quiraing
        allPoints.addAll(generatePoints("5", 53.068, -4.076, 300));  // Snowdon
        allPoints.addAll(generatePoints("6", 55.944, -3.161, 40));   // Arthur's Seat
        allPoints.addAll(generatePoints("7", 51.884, -3.437, 250));  // Pen y Fan
        allPoints.addAll(generatePoints("8", 52.103, -2.339, 110));  // Malvern Hills
        allPoints.addAll(generatePoints("9", 53.349, -1.809, 180));  // Mam Tor
        allPoints.addAll(generatePoints("10", 56.450, -4.710, 10));  // West Highland Way

        // Insert the entire batch of 100 points in one transaction
        trailDao.insertTrackPoints(allPoints);
    }

    private static List<TrackPointEntity> generatePoints(String trailId, double startLat, double startLon, double baseElev) {
        List<TrackPointEntity> points = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            TrackPointEntity point = new TrackPointEntity();
            point.setTrailId(trailId);
            point.setPointIndex(i);

            // Create a "path" by shifting the coordinates slightly for each index
            double lat = startLat + (i * 0.001);
            double lon = startLon + (i * 0.001);
            point.setCoordinate(lat + "," + lon);

            // Simulate rising elevation
            point.setElevation(baseElev + (i * 10));

            points.add(point);
        }
        return points;
    }
}