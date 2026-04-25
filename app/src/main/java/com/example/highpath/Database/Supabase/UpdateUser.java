package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;
import com.example.highpath.R;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class UpdateUser {

    private static final String TAG = "UpdateUser";

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateStatistics(
            Context context,
            long userId,
            double totalDistance,
            double totalElevation,
            int completedActivities,
            int completedChallenges,
            long totalMovingTime,
            long totalTimeOutside,
            UpdateCallback callback
    ) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // --- HARDCODED TEST ---
        // Using 11 as requested.
        // I am using 'user_id' here. If this fails, check your Supabase Table Editor
        // for the EXACT name of your primary key column.
        String url = "https://" + projectId + ".supabase.co/rest/v1/users?user_id=eq." + userId;

        Log.d(TAG, "Targeting URL: " + url);

        JSONObject json = new JSONObject();
        try {
            json.put("total_distance_km", totalDistance);
            json.put("total_elevation_m", totalElevation);
            json.put("completed_activities", completedActivities);
            json.put("completed_challenges", completedChallenges);
            json.put("total_moving_time_seconds", totalMovingTime);
            json.put("total_time_outside_seconds", totalTimeOutside);
        } catch (JSONException e) {
            callback.onFailure("JSON Construction Error: " + e.getMessage());
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network failure: " + e.getMessage());
                callback.onFailure("Network failure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Success! Response code: " + response.code());
                    callback.onSuccess();
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Failed! Code: " + response.code() + " Response: " + errorBody);
                    callback.onFailure("Code: " + response.code() + " Message: " + errorBody);
                }
                response.close();
            }
        });
    }
}