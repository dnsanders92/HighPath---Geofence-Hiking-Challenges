package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;
import com.example.highpath.R;
import okhttp3.*;
import java.io.File;
import java.io.IOException;

public class UploadGpx {

    private static final String TAG = "SupabaseUpload";

    public interface UploadCallback {
        void onSuccess(String fileUrl);
        void onFailure(String error);
    }

    public void upload(Context context, int userId, String filePath, UploadCallback callback) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        File file = new File(filePath);
        if (!file.exists()) {
            callback.onFailure("File does not exist at path: " + filePath);
            return;
        }

        // Get the filename (e.g., "morning_hike.gpx")
        String fileName = file.getName();

        // Construct the path: bucket/userId/folder/filename
        String url = "https://" + projectId + ".supabase.co/storage/v1/object/user-data/"
                + userId + "/gpx_files/" + fileName;

        OkHttpClient client = new OkHttpClient();

        // GPX files are XML-based
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("application/gpx+xml"));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/gpx+xml")
                // Upsert: true allows you to overwrite the file if it exists
                .addHeader("x-upsert", "true")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Return the public URL or the path
                    callback.onSuccess(url);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    callback.onFailure("Error " + response.code() + ": " + errorBody);
                }
                response.close();
            }
        });
    }
}