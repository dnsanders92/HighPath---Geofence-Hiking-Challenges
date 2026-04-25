package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;
import com.example.highpath.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class CreateUser {

    private static final String TAG = "SupabaseDebug";

    // Callback interface to return the ID once Supabase responds
    public interface UserCallback {
        void onSuccess(long userId);
        void onFailure(String error);
    }

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
        String url = "https://" + projectId + ".supabase.co/rest/v1/users";

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

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                // return=representation asks Supabase to return the created row
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network failure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    try {
                        // Supabase returns an array of the object created
                        JSONArray jsonArray = new JSONArray(responseBody);
                        long generatedId = jsonArray.getJSONObject(0).getLong("user_id");
                        callback.onSuccess(generatedId);
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
}