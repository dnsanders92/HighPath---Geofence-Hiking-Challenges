package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Database.Entities.ChallengeItemEntity;
import com.example.highpath.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertChallengeItem {

    private static final String TAG = "InsertChallengeItem";
    private static final String SUPABASE_URL = "https://uqyjsxpeyvttrhbafwtr.supabase.co/rest/v1/challenge_items";

    public interface InsertCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void insert(Context context, int userId, ChallengeItemEntity item, InsertCallback callback) {
        new Thread(() -> {
            try {
                String supabaseKey = context.getString(R.string.supabase_api_key);

                JSONObject json = new JSONObject();
                json.put("user_id", userId);
                json.put("challenge_id", item.getChallengeId());
                json.put("summit_id", item.getSummitId());
                json.put("item_name", item.getItemName());
                json.put("latitude", item.getLatitude());
                json.put("longitude", item.getLongitude());
                json.put("is_completed", true);

                URL url = new URL(SUPABASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", supabaseKey);
                conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes("utf-8"));
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == 201) {
                    callback.onSuccess();
                } else {
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errBody = new StringBuilder();
                    String errLine;
                    while ((errLine = errReader.readLine()) != null) errBody.append(errLine);
                    Log.e(TAG, "Error: " + errBody);
                    callback.onFailure("Response code: " + responseCode + " - " + errBody);
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Failed to insert challenge item", e);
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}