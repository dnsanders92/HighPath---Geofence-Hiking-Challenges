package com.example.highpath.Utilities;

import com.example.highpath.Models.GPXData;
import java.util.ArrayList;
import java.util.List;

public class LocationPackets {

    private GPXData gpxData;   // not initialized yet
    private final int packetSize = 100;

    public void setGPXData(GPXData gpxData) {
        this.gpxData = gpxData;
    }

    public List<List<double[]>> getPackets() {

        if (gpxData == null || gpxData.latitudes == null || gpxData.longitudes == null) {
            return new ArrayList<>(); // avoid crashes
        }

        List<Double> latitudes = gpxData.latitudes;
        List<Double> longitudes = gpxData.longitudes;

        List<List<double[]>> packetList = new ArrayList<>();
        int count = 0;

        while (count < latitudes.size()) {

            List<double[]> packet = new ArrayList<>();

            for (int i = 0; i < packetSize && count < latitudes.size(); i++) {

                packet.add(new double[]{
                        latitudes.get(count),
                        longitudes.get(count)
                });

                count++;
            }

            packetList.add(packet);
        }

        return packetList;
    }
}
