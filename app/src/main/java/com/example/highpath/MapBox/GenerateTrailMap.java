package com.example.highpath.MapBox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.highpath.Models.GPXData;
import com.example.highpath.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GenerateTrailMap {

    private static final String TAG = "GenerateTrailMap";
    private static final int IMAGE_WIDTH = 1200;
    private static final int IMAGE_HEIGHT = 500;
    private static final String TRAIL_COLOR = "E84855";
    private static final int TRAIL_WIDTH = 4;
    private static final String STYLE = "mapbox/outdoors-v12";

    public interface TrailMapCallback {
        void onSuccess(Bitmap bitmap);
        void onFailure(String error);
    }

    public static void generateMap(Context context, GPXData gpxData, TrailMapCallback callback) {
        new Thread(() -> {
            try {
                String accessToken = context.getString(R.string.mapbox_access_token);

                String encodedPolyline = encodePolyline(gpxData.latitudes, gpxData.longitudes);
                String encodedPath = java.net.URLEncoder.encode(encodedPolyline, "UTF-8");

                String overlay = "path-" + TRAIL_WIDTH + "+" + TRAIL_COLOR + "(" + encodedPath + ")";

                String urlStr = "https://api.mapbox.com/styles/v1/"
                        + STYLE + "/static/"
                        + overlay + "/auto/"
                        + IMAGE_WIDTH + "x" + IMAGE_HEIGHT
                        + "?padding=60&access_token=" + accessToken;

                Log.d(TAG, "Requesting static map");

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    // Convert bitmap to bytes and store in GPXData
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    gpxData.trailMapImage = baos.toByteArray();
                    Log.d(TAG, "Trail map image stored in GPXData: " + gpxData.trailMapImage.length + " bytes");

                    callback.onSuccess(bitmap);
                } else {
                    callback.onFailure("Response code: " + responseCode);
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Failed to generate trail map", e);
                callback.onFailure(e.getMessage());
            }
        }).start();
    }

    private static String encodePolyline(List<Double> latitudes, List<Double> longitudes) {
        StringBuilder result = new StringBuilder();
        int prevLat = 0;
        int prevLng = 0;

        for (int i = 0; i < latitudes.size(); i++) {
            int lat = (int) Math.round(latitudes.get(i) * 1e5);
            int lng = (int) Math.round(longitudes.get(i) * 1e5);

            result.append(encodeValue(lat - prevLat));
            result.append(encodeValue(lng - prevLng));

            prevLat = lat;
            prevLng = lng;
        }

        return result.toString();
    }

    private static String encodeValue(int value) {
        value = value < 0 ? ~(value << 1) : (value << 1);
        StringBuilder chunk = new StringBuilder();

        while (value >= 0x20) {
            chunk.append((char) ((0x20 | (value & 0x1f)) + 63));
            value >>= 5;
        }
        chunk.append((char) (value + 63));

        return chunk.toString();
    }
}