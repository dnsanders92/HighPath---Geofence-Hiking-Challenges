package com.example.highpath.Database.Supabase;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateUserFolders {

    private static final String TAG = "CreateUserFolders";

    private final OkHttpClient client;
    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucketName;

    public interface FolderCreationListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public CreateUserFolders(String supabaseUrl, String supabaseKey, String bucketName) {
        this.client = new OkHttpClient();
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
        this.bucketName = bucketName;
    }

    public void createFolders(String userId, FolderCreationListener listener) {

        // folder paths
        final String[] paths = {
                userId + "/gpx_files/.keep",
                userId + "/user_images/.keep",
                userId + "/trail_map_images/.keep"
        };

        final int totalPaths = paths.length;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // create each folder
        for (int i = 0; i < paths.length; i++) {

            String path = paths[i];
            String fullUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + path;

            RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Content-Type", "text/plain")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    // first failure triggers callback
                    Log.e(TAG, "network error creating folder: " + path + " - " + e.getMessage());
                    if (failureCount.incrementAndGet() == 1) {
                        listener.onFailure("network error: " + e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        // successful folder creation
                        Log.d(TAG, "folder created: " + path);
                        if (successCount.incrementAndGet() == totalPaths) {
                            listener.onSuccess();
                        }
                    } else {
                        // failure triggers callback
                        Log.e(TAG, "failed to create folder: " + path + " - " + response.code() + " - " + responseBody);
                        if (failureCount.incrementAndGet() == 1) {
                            listener.onFailure("failed to create folder: " + path + " - " + response.code());
                        }
                    }

                    response.close();
                }
            });
        }
    }
}
