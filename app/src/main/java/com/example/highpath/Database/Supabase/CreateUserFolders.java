package com.example.highpath.Database.Supabase;

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
        // We use .keep to ensure the folder is visible in Supabase UI
        String[] paths = {
                userId + "/gpx_files/.keep",
                userId + "/user_images/.keep"
        };

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (String path : paths) {
            String fullUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + path;

            // Empty body to "create" the file/folder
            RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("apikey", supabaseKey) // Some setups require the key header separately
                    .addHeader("Content-Type", "text/plain")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failureCount.incrementAndGet();
                    if (failureCount.get() == 1) {
                        listener.onFailure("Network error: " + e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        int finished = successCount.incrementAndGet();
                        if (finished == paths.length) {
                            listener.onSuccess();
                        }
                    } else {
                        // Handle cases like 403 Forbidden (RLS issues) or 409 Conflict
                        failureCount.incrementAndGet();
                        if (failureCount.get() == 1) {
                            listener.onFailure("Supabase error: " + response.code());
                        }
                    }
                    response.close();
                }
            });
        }
    }
}