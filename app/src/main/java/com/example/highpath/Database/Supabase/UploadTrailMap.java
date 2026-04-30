package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;

import com.example.highpath.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadTrailMap {

    private static final String TAG = "UploadTrailMap";

    public interface UploadCallback {
        void onSuccess(String fileUrl);
        void onFailure(String error);
    }

    public static void upload(Context context, int userId, String activityId, byte[] imageBytes, UploadCallback callback) {
        new Thread(() -> {
            try {
                String supabaseKey = context.getString(R.string.supabase_api_key);
                String projectId = context.getString(R.string.supabase_project_id);

                String path = userId + "/trail_map_images/" + activityId + ".png";
                String urlStr = "https://" + projectId + ".supabase.co/storage/v1/object/user-data/" + path;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "image/png");
                conn.setRequestProperty("apikey", supabaseKey);
                conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(imageBytes);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == 200 || responseCode == 201) {
                    Log.d(TAG, "Trail map uploaded: " + urlStr);
                    callback.onSuccess(urlStr);
                } else {
                    callback.onFailure("Response code: " + responseCode);
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Failed to upload trail map", e);
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}