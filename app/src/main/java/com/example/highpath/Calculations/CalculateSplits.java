package com.example.highpath.Calculations;

import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Models.GPXData;

import java.util.ArrayList;
import java.util.List;

public class CalculateSplits {

    private static final double SPLIT_DISTANCE_KM = 1.0;

    // generate splits
    public List<SplitEntity> getSplits(String activityId, GPXData gpx) {
        List<SplitEntity> splits = new ArrayList<>();

        // validation
        if (gpx == null || gpx.latitudes == null || gpx.latitudes.size() < 2) {
            return splits;
        }

        int splitIndex = 0;
        double splitDistanceAccumulated = 0.0;
        long splitTimeAccumulated = 0;

        // loop through all segments
        for (int i = 1; i < gpx.latitudes.size(); i++) {

            // distance segment
            double segmentDistance = CalculateDistance.equirectangular(
                    gpx.latitudes.get(i - 1), gpx.longitudes.get(i - 1),
                    gpx.latitudes.get(i),     gpx.longitudes.get(i)
            );

            // time segment
            long segmentTime = CalculateTime.getMovingDurationSeconds(
                    gpx.timestamps.subList(i - 1, i + 1),
                    gpx.latitudes.subList(i - 1, i + 1),
                    gpx.longitudes.subList(i - 1, i + 1)
            );

            splitDistanceAccumulated += segmentDistance;
            splitTimeAccumulated += segmentTime;

            // full km reached
            if (splitDistanceAccumulated >= SPLIT_DISTANCE_KM) {

                SplitEntity split = new SplitEntity();
                split.setActivityId(activityId);
                split.setSplitIndex(splitIndex);
                split.setDistance(splitDistanceAccumulated);
                split.setPace(CalculatePace.pace(splitTimeAccumulated, splitDistanceAccumulated));

                splits.add(split);

                // reset for next split
                splitIndex++;
                splitDistanceAccumulated = 0.0;
                splitTimeAccumulated = 0;
            }
        }

        // final partial split
        if (splitDistanceAccumulated > 0.01) {
            SplitEntity split = new SplitEntity();
            split.setActivityId(activityId);
            split.setSplitIndex(splitIndex);
            split.setDistance(splitDistanceAccumulated);
            split.setPace(CalculatePace.pace(splitTimeAccumulated, splitDistanceAccumulated));
            splits.add(split);
        }

        return splits;
    }
}
