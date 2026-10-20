package com.example.highpath.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.example.highpath.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherAPIService {

    public interface WeatherCallback {
        void onSuccess(String json);
        void onError(Exception e);
    }

    public static void getFiveDayForecast(Context context, double lat, double lon, WeatherCallback callback) {
        String apiKey = context.getString(R.string.open_weather_api_key);

        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat="
                + lat + "&lon=" + lon + "&units=metric&appid=" + apiKey;

        new AsyncTask<String, Void, String>() {

            Exception error;

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    conn.disconnect();

                    return result.toString();

                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String json) {
                if (json != null) {
                    callback.onSuccess(json);
                } else {
                    callback.onError(error);
                }
            }
        }.execute(urlString);
    }
}
