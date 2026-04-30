package com.example.highpath.Services;

import android.os.Handler;
import android.os.Looper;

import com.example.highpath.Models.GPXData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ElevationAPIService {

    public interface ElevationCallback {
        void onSuccess(List<Double> correctedElevations);
        void onFailure(Throwable t);
    }

    private static final String BASE_URL = "https://api.open-elevation.com/api/v1/lookup";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson = new Gson();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ElevationAPIService() {
        client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Public entry. Will call the callback on the main thread.
     * Expects gpxData.latitudes, longitudes and elevations to be same length.
     */
    public void getElevation(final GPXData gpxData, final ElevationCallback callback) {
        if (gpxData == null || gpxData.latitudes == null || gpxData.longitudes == null) {
            postFailure(callback, new IllegalArgumentException("GPXData or coordinate lists are null"));
            return;
        }
        final int total = gpxData.latitudes.size();
        if (gpxData.longitudes.size() != total) {
            postFailure(callback, new IllegalArgumentException("Latitude and longitude lists differ in size"));
            return;
        }

        final int packetSize = 100;
        final List<Double> correctedElevations = new ArrayList<>(total);

        // Process packets sequentially using recursion/chain of async calls
        processPacket(0, packetSize, gpxData, correctedElevations, callback);
    }

    private void processPacket(final int start,
                               final int packetSize,
                               final GPXData gpxData,
                               final List<Double> correctedElevations,
                               final ElevationCallback callback) {

        final int total = gpxData.latitudes.size();
        if (start >= total) {
            // done
            postSuccess(callback, correctedElevations);
            return;
        }

        final int end = Math.min(start + packetSize, total);

        // Build JSON body: {"locations":[{"latitude":..,"longitude":..}, ...]}
        JsonObject bodyJson = new JsonObject();
        JsonArray locations = new JsonArray();
        for (int i = start; i < end; i++) {
            JsonObject point = new JsonObject();
            point.addProperty("latitude", gpxData.latitudes.get(i));
            point.addProperty("longitude", gpxData.longitudes.get(i));
            locations.add(point);
        }
        bodyJson.add("locations", locations);

        RequestBody requestBody = RequestBody.create(gson.toJson(bodyJson), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // fallback: append original elevations for this packet and continue
                for (int i = start; i < end; i++) {
                    correctedElevations.add(gpxData.elevations.get(i));
                }
                // Optionally retry logic could be added here. Continue to next packet.
                processPacket(end, packetSize, gpxData, correctedElevations, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.body() == null) {
                        // fallback to original elevations for this packet
                        for (int i = start; i < end; i++) {
                            correctedElevations.add(gpxData.elevations.get(i));
                        }
                        processPacket(end, packetSize, gpxData, correctedElevations, callback);
                        return;
                    }

                    String bodyString = response.body().string();

                    // Defensive parsing with Gson
                    JsonElement rootEl = gson.fromJson(bodyString, JsonElement.class);
                    List<Double> packetElevations = new ArrayList<>();

                    if (rootEl != null && rootEl.isJsonObject()) {
                        JsonObject rootObj = rootEl.getAsJsonObject();
                        if (rootObj.has("results") && rootObj.get("results").isJsonArray()) {
                            JsonArray results = rootObj.getAsJsonArray("results");
                            for (JsonElement el : results) {
                                if (el.isJsonObject() && el.getAsJsonObject().has("elevation")) {
                                    packetElevations.add(el.getAsJsonObject().get("elevation").getAsDouble());
                                } else {
                                    packetElevations.add(Double.NaN);
                                }
                            }
                        } else {
                            // Unexpected shape: fallback
                            for (int i = start; i < end; i++) {
                                packetElevations.add(gpxData.elevations.get(i));
                            }
                        }
                    } else {
                        // Unexpected shape: fallback
                        for (int i = start; i < end; i++) {
                            packetElevations.add(gpxData.elevations.get(i));
                        }
                    }

                    // Ensure size matches packet length; if not, fallback per-index
                    int expected = end - start;
                    if (packetElevations.size() != expected) {
                        // fill missing with original values
                        List<Double> fixed = new ArrayList<>(expected);
                        for (int i = 0; i < expected; i++) {
                            if (i < packetElevations.size() && !packetElevations.get(i).isNaN()) {
                                fixed.add(packetElevations.get(i));
                            } else {
                                fixed.add(gpxData.elevations.get(start + i));
                            }
                        }
                        correctedElevations.addAll(fixed);
                    } else {
                        correctedElevations.addAll(packetElevations);
                    }

                    // Continue with next packet
                    processPacket(end, packetSize, gpxData, correctedElevations, callback);

                } catch (Exception ex) {
                    // On parse or other error, fallback and continue
                    for (int i = start; i < end; i++) {
                        correctedElevations.add(gpxData.elevations.get(i));
                    }
                    processPacket(end, packetSize, gpxData, correctedElevations, callback);
                } finally {
                    response.close();
                }
            }
        });
    }

    private void postSuccess(ElevationCallback callback, List<Double> correctedElevations) {
        mainHandler.post(() -> {
            if (callback != null) callback.onSuccess(correctedElevations);
        });
    }

    private void postFailure(ElevationCallback callback, Throwable t) {
        mainHandler.post(() -> {
            if (callback != null) callback.onFailure(t);
        });
    }
}
