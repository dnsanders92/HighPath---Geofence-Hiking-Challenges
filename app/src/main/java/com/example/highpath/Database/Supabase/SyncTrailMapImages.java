package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SyncTrailMapImages {

    private static final String TAG = "SyncTrailMapImages";

    public static void sync(Context context, int userId) {
        new Thread(() -> {
            try {
                String supabaseKey = context.getString(R.string.supabase_api_key);
                String projectId = context.getString(R.string.supabase_project_id);

                // 1. List all files in the user's trail_map_images folder
                String listUrl = "https://" + projectId + ".supabase.co/storage/v1/object/list/user-data";
                String body = "{\"prefix\":\"" + userId + "/trail_map_images/\",\"limit\":100}";

                URL url = new URL(listUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", supabaseKey);
                conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
                conn.setDoOutput(true);
                conn.getOutputStream().write(body.getBytes("utf-8"));

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "List response code: " + responseCode);

                if (responseCode != 200) {
                    Log.e(TAG, "Failed to list images");
                    conn.disconnect();
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                conn.disconnect();

                JSONArray files = new JSONArray(sb.toString());
                Log.d(TAG, "Found " + files.length() + " trail map images");

                LocalDatabase db = LocalDatabase.getInstance(context);

                // 2. For each file download and save to DB
                for (int i = 0; i < files.length(); i++) {
                    JSONObject file = files.getJSONObject(i);
                    String fileName = file.getString("name");

                    if (fileName.equals(".keep")) continue;

                    String activityId = fileName.replace(".png", "");
                    Log.d(TAG, "Downloading image for activity: " + activityId);

                    String imageUrl = "https://" + projectId + ".supabase.co/storage/v1/object/user-data/"
                            + userId + "/trail_map_images/" + fileName;

                    byte[] imageBytes = attemptDownload(imageUrl, supabaseKey);

                    if (imageBytes != null) {
                        db.activityDao().updateTrailMapImage(activityId, imageBytes);
                        Log.d(TAG, "Saved image for activity: " + activityId + " (" + imageBytes.length + " bytes)");
                    } else {
                        // Retry once
                        Log.w(TAG, "First attempt failed for " + activityId + " — retrying...");
                        imageBytes = attemptDownload(imageUrl, supabaseKey);
                        if (imageBytes != null) {
                            db.activityDao().updateTrailMapImage(activityId, imageBytes);
                            Log.d(TAG, "Retry succeeded for activity: " + activityId);
                        } else {
                            Log.e(TAG, "Retry failed for activity: " + activityId);
                        }
                    }
                }

                Log.d(TAG, "Trail map image sync complete.");

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync trail map images", e);
            }
        }).start();
    }

    private static byte[] attemptDownload(String imageUrl, String supabaseKey) {
        try {
            URL imgUrl = new URL(imageUrl);
            HttpURLConnection imgConn = (HttpURLConnection) imgUrl.openConnection();
            imgConn.setRequestMethod("GET");
            imgConn.setRequestProperty("apikey", supabaseKey);
            imgConn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            imgConn.setConnectTimeout(15000);
            imgConn.setReadTimeout(15000);

            int imgResponseCode = imgConn.getResponseCode();
            Log.d(TAG, "Image download response: " + imgResponseCode);

            if (imgResponseCode == 200) {
                InputStream is = imgConn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                is.close();
                imgConn.disconnect();
                return baos.toByteArray();
            }

            imgConn.disconnect();
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Download attempt failed: " + e.getMessage());
            return null;
        }
    }
}