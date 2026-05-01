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

        // supabase connections
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // auth endpoint
        String authUrl = "https://" + projectId + ".supabase.co/auth/v1/token?grant_type=password";

        // build login json
        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("email", email);
            loginJson.put("password", password);
        } catch (JSONException e) {
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // request body
        RequestBody body = RequestBody.create(
                loginJson.toString(),
                MediaType.parse("application/json")
        );

        // auth request
        Request authRequest = new Request.Builder()
                .url(authUrl)
                .addHeader("apikey", apiKey)
                .post(body)
                .build();

        // send auth request
        client.newCall(authRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // network error
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    // auth ok → fetch profile
                    fetchUserProfile(context, email, callback);
                } else {
                    // invalid login
                    callback.onFailure("invalid email or password");
                }

                response.close();
            }
        });
    }

    private static void fetchUserProfile(Context context, String email, LoginCallback callback) {

        // supabase connection
        String projectId = context.getString(R.string.supabase_project_id);
        String apiKey = context.getString(R.string.supabase_api_key);

        // query user by email
        String url = "https://" + projectId + ".supabase.co/rest/v1/users?email=eq." + email + "&select=*";

        OkHttpClient client = new OkHttpClient();

        // request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        // send request
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // network error
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    // parse array
                    JSONArray array = new JSONArray(response.body().string());

                    if (array.length() > 0) {
                        // return first user
                        callback.onSuccess(array.getJSONObject(0));
                    } else {
                        callback.onFailure("user profile not found");
                    }

                } catch (Exception e) {
                    callback.onFailure("sync error");
                }

                response.close();
            }
        });
    }
}
