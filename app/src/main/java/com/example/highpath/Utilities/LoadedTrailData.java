package com.example.highpath.Utilities;

import android.content.Context;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Entities.TrackPointEntity;
import java.util.ArrayList;
import java.util.List;

public class LoadedTrailData {

    private final Context context;
    public List<Double> latitudes = new ArrayList<>();
    public List<Double> longitudes = new ArrayList<>();
    public List<Double> elevations = new ArrayList<>();

    public LoadedTrailData(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getTrailData(String trailId) {
        // clear lists before starting a new load
        latitudes.clear();
        longitudes.clear();
        elevations.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {

                LocalDatabase localDatabase = LocalDatabase.getInstance(context);

                List<TrackPointEntity> points = localDatabase.trackPointDao().getPointsForTrail(trailId);

                if (points != null) {
                    for (TrackPointEntity point : points) {
                        String coordString = point.getCoordinate();

                        // Parse "lat,lon" string into separate doubles
                        if (coordString != null && coordString.contains(",")) {
                            try {
                                String[] parts = coordString.split(",");
                                double lat = Double.parseDouble(parts[0].trim());
                                double lon = Double.parseDouble(parts[1].trim());

                                latitudes.add(lat);
                                longitudes.add(lon);
                                elevations.add(point.getElevation());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }
}