package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;
import com.example.highpath.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateUser {

    private static final String TAG = "SupabaseDebug";

    public interface UserCallback {
        void onSuccess(long userId);
        void onFailure(String error);
    }

    /**
     * Creates a user in the database and then initializes their storage folders.
     */
    public static void addUser(
            Context context,
            String firstName,
            String surname,
            String monthOfBirth,
            Integer yearOfBirth,
            Integer totalDistanceKm,
            Integer totalElevationM,
            Integer completedActivities,
            Integer completedChallenges,
            Long totalTimeOutsideSeconds,
            Long totalMovingTimeSeconds,
            UserCallback callback
    ) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        String dbUrl = "https://" + projectId + ".supabase.co/rest/v1/users";
        String storageBaseUrl = "https://" + projectId + ".supabase.co/storage/v1/object/user-data/";

        // 1. Construct JSON for Database
        JSONObject json = new JSONObject();
        try {
            json.put("first_name", firstName);
            json.put("surname", surname);
            json.put("month_of_birth", monthOfBirth);
            json.put("year_of_birth", yearOfBirth);
            json.put("total_distance_km", totalDistanceKm);
            json.put("total_elevation_m", totalElevationM);
            json.put("completed_activities", completedActivities);
            json.put("completed_challenges", completedChallenges);
            json.put("total_time_outside_seconds", totalTimeOutsideSeconds);
            json.put("total_moving_time_seconds", totalMovingTimeSeconds);
        } catch (JSONException e) {
            callback.onFailure("JSON Construction Error: " + e.getMessage());
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request dbRequest = new Request.Builder()
                .url(dbUrl)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        // 2. Execute Database Insert
        client.newCall(dbRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network failure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        long generatedId = jsonArray.getJSONObject(0).getLong("user_id");

                        // 3. Database success! Now create the folders
                        createStorageFolders(client, storageBaseUrl, apiKey, generatedId, callback);

                    } catch (JSONException e) {
                        callback.onFailure("Parsing error: " + e.getMessage());
                    }
                } else {
                    String error = response.body() != null ? response.body().string() : "No error body";
                    callback.onFailure("Code: " + response.code() + " Message: " + error);
                }
                response.close();
            }
        });
    }

    private static void createStorageFolders(OkHttpClient client, String baseUrl, String apiKey, long userId, UserCallback callback) {
        // Define paths for the two sub-folders
        String[] folderPaths = {
                userId + "/gpx_files/.keep",
                userId + "/user_images/.keep"
        };

        AtomicInteger completedRequests = new AtomicInteger(0);
        int totalRequests = folderPaths.length;

        for (String path : folderPaths) {
            // Empty body is sufficient to "initialize" the path
            RequestBody emptyBody = RequestBody.create("", MediaType.parse("text/plain"));

            Request storageRequest = new Request.Builder()
                    .url(baseUrl + path)
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(emptyBody)
                    .build();

            client.newCall(storageRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to create folder path: " + path);
                    checkCompletion();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Storage error (" + response.code() + ") for path: " + path);
                    }
                    response.close();
                    checkCompletion();
                }

                private void checkCompletion() {
                    // Once both folder requests are attempted, we signal success for the overall user creation
                    if (completedRequests.incrementAndGet() == totalRequests) {
                        callback.onSuccess(userId);
                    }
                }
            });
        }
    }
}