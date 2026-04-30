package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;
import com.example.highpath.R;
import okhttp3.*;
import org.json.JSONArray;
import java.io.IOException;

public class SyncActivities {

    private static final String TAG = "SyncDebug";

    public interface ActivitySyncCallback {
        void onSuccess(JSONArray activities);
        void onFailure(String error);
    }

    public static void fetchUserActivities(Context context, int userId, ActivitySyncCallback callback) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // Filters by user_id to pull only this user's data from the cloud master table
        String url = "https://" + projectId + ".supabase.co/rest/v1/activities?user_id=eq." + userId + "&select=*";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network failure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        Log.d(TAG, "Cloud returned " + array.length() + " activities.");
                        callback.onSuccess(array);
                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parse Error: " + responseBody);
                        callback.onFailure("Failed to parse activities");
                    }
                } else {
                    Log.e(TAG, "Supabase Error " + response.code() + ": " + responseBody);
                    callback.onFailure("Server error: " + response.code());
                }
                response.close();
            }
        });
    }
}