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
        // supabase connection
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        String url = "https://" + projectId + ".supabase.co/rest/v1/users?user_id=eq." + userId;
        Log.d(TAG, "targeting url: " + url);

        // build json
        JSONObject json = new JSONObject();
        try {
            json.put("total_distance_km", totalDistance);
            json.put("total_elevation_m", totalElevation);
            json.put("completed_activities", completedActivities);
            json.put("completed_challenges", completedChallenges);
            json.put("total_moving_time_seconds", totalMovingTime);
            json.put("total_time_outside_seconds", totalTimeOutside);
        } catch (JSONException e) {
            callback.onFailure("json error: " + e.getMessage());
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // request
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // patch request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        // send request
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "network failure: " + e.getMessage());
                callback.onFailure("network failure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    Log.d(TAG, "success, code: " + response.code());
                    callback.onSuccess();
                } else {
                    // supabase returned error
                    String errorBody = response.body() != null ? response.body().string() : "no error body";
                    Log.e(TAG, "failed, code: " + response.code() + " response: " + errorBody);
                    callback.onFailure("code: " + response.code() + " message: " + errorBody);
                }

                response.close();
            }
        });
    }
}
