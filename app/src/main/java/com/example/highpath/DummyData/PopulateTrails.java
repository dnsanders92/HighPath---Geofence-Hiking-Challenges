package com.example.highpath.DummyData;

import com.example.highpath.Database.Daos.TrailDao;
import com.example.highpath.Database.Entities.TrailEntity;

import java.util.ArrayList;
import java.util.List;

public class PopulateTrails {

    public static void insertDummyTrails(TrailDao trailDao) {
        List<TrailEntity> trails = new ArrayList<>();

        trails.add(createTrail("1", "Old Storr Path", "Isle of Skye", 3.8, 288, "Moderate", 57.507, -6.174));
        trails.add(createTrail("2", "Ben Nevis Mountain Track", "Fort William", 17.0, 1352, "Hard", 56.796, -5.003));
        trails.add(createTrail("3", "Catbells Ridge", "Lake District", 5.6, 451, "Moderate", 54.569, -3.172));
        trails.add(createTrail("4", "The Quiraing", "Isle of Skye", 6.8, 340, "Moderate", 57.643, -6.273));
        trails.add(createTrail("5", "Snowdon via Pyg Track", "Snowdonia", 11.2, 723, "Hard", 53.068, -4.076));
        trails.add(createTrail("6", "Arthur's Seat", "Edinburgh", 3.9, 251, "Easy", 55.944, -3.161));
        trails.add(createTrail("7", "Pen y Fan Trail", "Brecon Beacons", 6.4, 445, "Moderate", 51.884, -3.437));
        trails.add(createTrail("8", "Malvern Hills Ridge", "Worcestershire", 14.5, 650, "Moderate", 52.103, -2.339));
        trails.add(createTrail("9", "Mam Tor Circular", "Peak District", 4.8, 210, "Easy", 53.349, -1.809));
        trails.add(createTrail("10", "West Highland Way (Full)", "Scottish Highlands", 154.0, 4312, "Extreme", 56.450, -4.710));

        for (TrailEntity trail : trails) {
            trailDao.insertTrail(trail);
        }
    }

    private static TrailEntity createTrail(String id, String name, String region, double dist, int ascent, String diff, double lat, double lon) {
        TrailEntity trail = new TrailEntity();
        trail.setTrailId(id);
        trail.setName(name);
        trail.setRegion(region);
        trail.setDistanceKm(dist);
        trail.setAscentM(ascent);
        trail.setDifficulty(diff);

        // Coordinates and Bounding Box (Simplified)
        trail.setCenterLat(lat);
        trail.setCenterLon(lon);
        trail.setMinLat(lat - 0.05);
        trail.setMaxLat(lat + 0.05);
        trail.setMinLon(lon - 0.05);
        trail.setMaxLon(lon + 0.05);

        // Images set to null as requested
        trail.setThumbnail(null);
        trail.setPreviewImage(null);

        return trail;
    }
}