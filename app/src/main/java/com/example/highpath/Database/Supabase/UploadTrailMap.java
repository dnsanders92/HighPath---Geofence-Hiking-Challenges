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
                // supabase connections
                String supabaseKey = context.getString(R.string.supabase_api_key);
                String projectId = context.getString(R.string.supabase_project_id);

                // file path
                String path = userId + "/trail_map_images/" + activityId + ".png";
                String urlStr = "https://" + projectId + ".supabase.co/storage/v1/object/user-data/" + path;

                // open connection
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "image/png");
                conn.setRequestProperty("apikey", supabaseKey);
                conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
                conn.setDoOutput(true);

                // send bytes
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(imageBytes);
                }

                // read response
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "response code: " + responseCode);

                if (responseCode == 200 || responseCode == 201) {
                    // success
                    Log.d(TAG, "trail map uploaded: " + urlStr);
                    callback.onSuccess(urlStr);
                } else {
                    // supabase returned error
                    callback.onFailure("response code: " + responseCode);
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "failed to upload trail map", e);
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}
