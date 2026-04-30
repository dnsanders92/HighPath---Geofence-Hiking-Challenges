package com.example.highpath.Database.Supabase;

import android.content.Context;
import com.example.highpath.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class LoginUser {

    public interface LoginCallback {
        void onSuccess(JSONObject userData);
        void onFailure(String error);
    }

    public static void login(Context context, String email, String password, LoginCallback callback) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // 1. Authenticate via Supabase Auth
        String authUrl = "https://" + projectId + ".supabase.co/auth/v1/token?grant_type=password";

        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("email", email);
            loginJson.put("password", password);
        } catch (JSONException e) { return; }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(loginJson.toString(), MediaType.parse("application/json"));

        Request authRequest = new Request.Builder()
                .url(authUrl)
                .addHeader("apikey", apiKey)
                .post(body)
                .build();

        client.newCall(authRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 2. Success! Auth verified. Now fetch the profile data.
                    // In this simple setup, we query by email or first/last name
                    // Ideally, you'd query by the UUID returned in this response.
                    fetchUserProfile(context, email, callback);
                } else {
                    callback.onFailure("Invalid email or password");
                }
                response.close();
            }
        });
    }

    private static void fetchUserProfile(Context context, String email, LoginCallback callback) {
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // Assuming you add an 'email' column to your users table for easy lookups
        String url = "https://" + projectId + ".supabase.co/rest/v1/users?email=eq." + email + "&select=*";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    if (array.length() > 0) {
                        callback.onSuccess(array.getJSONObject(0));
                    } else {
                        callback.onFailure("User profile not found");
                    }
                } catch (Exception e) { callback.onFailure("Sync error"); }
                response.close();
            }
        });
    }
}