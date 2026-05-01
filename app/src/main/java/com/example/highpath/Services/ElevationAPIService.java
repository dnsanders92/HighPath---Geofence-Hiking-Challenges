package com.example.highpath.Services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ElevationAPIService {

    private static final String BASE_URL = "https://api.opentopodata.org/v1/srtm30m?locations=";

    public List<Double> getBatchElevations(double[][] coordinates) {

        List<Double> elevations = new ArrayList<>();

        try {

            StringBuilder locBuilder = new StringBuilder();
            for (int i = 0; i < coordinates.length; i++) {
                locBuilder.append(coordinates[i][0])
                        .append(",")
                        .append(coordinates[i][1]);
                if (i < coordinates.length - 1) locBuilder.append("|");
            }

            URL url = new URL(BASE_URL + locBuilder);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            Log.d("ElevationAPI", "Response code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                JSONObject json = new JSONObject(sb.toString());
                String status = json.getString("status");
                Log.d("ElevationAPI", "API status: " + status);

                if (status.equals("OK")) {
                    JSONArray results = json.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);

                        if (result.isNull("elevation")) {
                            Log.w("ElevationAPI", "Null elevation at index " + i);
                            elevations.add(0.0);
                        } else {
                            elevations.add(result.getDouble("elevation"));
                        }
                    }
                    Log.d("ElevationAPI", "Got " + elevations.size() + " elevations for " + coordinates.length + " points");
                }

            } else {
                BufferedReader errReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()));
                StringBuilder errBody = new StringBuilder();
                String errLine;
                while ((errLine = errReader.readLine()) != null) errBody.append(errLine);
                Log.e("ElevationAPI", "Error: " + errBody);
            }

            conn.disconnect();

        } catch (Exception e) {
            Log.e("ElevationAPI", "Failed to get elevations", e);
        }

        return elevations;
    }
}