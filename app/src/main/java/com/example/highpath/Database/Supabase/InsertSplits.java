package com.example.highpath.Database.Supabase;

import android.content.Context;
import android.util.Log;

import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class InsertSplits {

    private static final String SUPABASE_URL = "https://uqyjsxpeyvttrhbafwtr.supabase.co/rest/v1/splits";

    public interface InsertCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void insert(Context context, int userId, List<SplitEntity> splits, InsertCallback callback) {
        new Thread(() -> {
            try {
                // supabase key
                String supabaseKey = context.getString(R.string.supabase_api_key);

                // build json array
                JSONArray jsonArray = new JSONArray();

                // add each split
                for (int i = 0; i < splits.size(); i++) {
                    SplitEntity split = splits.get(i);

                    JSONObject obj = new JSONObject();
                    obj.put("activity_id", split.getActivityId());
                    obj.put("user_id", userId);
                    obj.put("split_index", split.getSplitIndex());
                    obj.put("split_pace", split.getPace());
                    obj.put("split_distance", split.getDistance());

                    jsonArray.put(obj);
                }

                // open connection
                URL url = new URL(SUPABASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", supabaseKey);
                conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
                conn.setDoOutput(true);

                // send body
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonArray.toString().getBytes("utf-8"));
                }

                // read response
                int responseCode = conn.getResponseCode();
                Log.d("InsertSplits", "response code: " + responseCode);

                if (responseCode == 201) {
                    // success
                    callback.onSuccess();
                } else {
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errBody = new StringBuilder();
                    String errLine;

                    while ((errLine = errReader.readLine()) != null) {
                        errBody.append(errLine);
                    }

                    Log.e("InsertSplits", "error body: " + errBody);
                    callback.onFailure("response code: " + responseCode + " - " + errBody);
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e("InsertSplits", "failed to insert splits", e);
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}
