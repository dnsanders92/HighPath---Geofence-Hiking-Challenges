package com.example.highpath.Utilities.OpenWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseOpenWeather {

    public static FiveDayWeatherData parse(String json) {
        FiveDayWeatherData data = new FiveDayWeatherData();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray list = root.getJSONArray("list");

            Map<String, Double> minMap = new HashMap<>();
            Map<String, Double> maxMap = new HashMap<>();

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");

                double min = main.getDouble("temp_min");
                double max = main.getDouble("temp_max");

                String dt = item.getString("dt_txt");
                String day = dt.substring(0, 10);

                if (!minMap.containsKey(day)) {
                    minMap.put(day, min);
                    maxMap.put(day, max);
                } else {
                    minMap.put(day, Math.min(minMap.get(day), min));
                    maxMap.put(day, Math.max(maxMap.get(day), max));
                }
            }

            List<String> days = new ArrayList<>(minMap.keySet());

            for (int i = 0; i < 5 && i < days.size(); i++) {
                String d = days.get(i);
                data.LowTemps.add(minMap.get(d));
                data.HighTemps.add(maxMap.get(d));
            }

            data.buildHighLowStrings();

        } catch (Exception e) {
        }

        return data;
    }
}
