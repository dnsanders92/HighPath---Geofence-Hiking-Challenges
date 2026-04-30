package com.example.highpath.Utilities;

import android.util.Log;

import com.example.highpath.Models.GPXData;
import com.example.highpath.Services.ElevationAPIService;

import java.util.ArrayList;
import java.util.List;

public class CorrectElevations {

    private final ElevationAPIService elevationAPIService = new ElevationAPIService();
    private final LocationPackets locationPackets = new LocationPackets();

    public void updateGPXElevations(GPXData gpxData) {
        if (gpxData == null) return;

        // Log the raw elevations before correction
        if (gpxData.elevations != null && gpxData.elevations.size() > 0) {
            Log.d("CorrectElevations", "RAW elevations - First: " + gpxData.elevations.get(0)
                    + " | Last: " + gpxData.elevations.get(gpxData.elevations.size() - 1)
                    + " | Count: " + gpxData.elevations.size());
        }

        locationPackets.setGPXData(gpxData);
        List<List<double[]>> packets = locationPackets.getPackets();
        Log.d("CorrectElevations", "Total packets to process: " + packets.size());

        List<Double> correctedList = new ArrayList<>();

        for (int i = 0; i < packets.size(); i++) {

            // Rate limit: max 1 call per second
            if (i > 0) {
                try { Thread.sleep(1100); } catch (InterruptedException ignored) {}
            }

            List<double[]> currentPacket = packets.get(i);
            double[][] coordsArray = new double[currentPacket.size()][2];
            for (int j = 0; j < currentPacket.size(); j++) {
                coordsArray[j] = currentPacket.get(j);
            }

            Log.d("CorrectElevations", "Sending packet " + (i + 1) + "/" + packets.size()
                    + " (" + currentPacket.size() + " points)");

            List<Double> packetResults = elevationAPIService.getBatchElevations(coordsArray);

            if (packetResults != null) {
                correctedList.addAll(packetResults);
                Log.d("CorrectElevations", "Packet " + (i + 1) + " returned " + packetResults.size() + " results");
            } else {
                Log.e("CorrectElevations", "Packet " + (i + 1) + " returned null");
            }
        }

        // Size validation before assigning
        if (correctedList.size() != gpxData.latitudes.size()) {
            Log.e("CorrectElevations", "Size mismatch! Expected: " + gpxData.latitudes.size()
                    + " | Got: " + correctedList.size() + " — discarding corrected data");
            return;
        }

        gpxData.correctedElevations = correctedList;

        // Log corrected elevations after
        Log.d("CorrectElevations", "CORRECTED elevations - First: " + correctedList.get(0)
                + " | Last: " + correctedList.get(correctedList.size() - 1)
                + " | Count: " + correctedList.size());
    }
}