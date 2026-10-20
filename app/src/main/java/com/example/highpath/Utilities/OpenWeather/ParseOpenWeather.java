package com.example.highpath.Utilities.OpenWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap; // Added for day sorting

public class ParseOpenWeather {

    public static FiveDayWeatherData parse(String json) {
        FiveDayWeatherData data = new FiveDayWeatherData();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray list = root.getJSONArray("list");

            // Using keeps days in order (e.g., Monday, Tuesday...)
            Map<String, Double> minMap = new TreeMap<>();
            Map<String, Double> maxMap = new TreeMap<>();
            Map<String, String> conditionMap = new HashMap<>();

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");

                double min = main.getDouble("temp_min");
                double max = main.getDouble("temp_max");

                // get weather conditions
                String condition = item.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("main");

                String dt = item.getString("dt_txt");
                String day = dt.substring(0, 10);

                if (!minMap.containsKey(day)) {
                    minMap.put(day, min);
                    maxMap.put(day, max);

                    conditionMap.put(day, condition);
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
                data.Conditions.add(conditionMap.get(d));
            }

            data.buildHighLowStrings();

        } catch (Exception e) {

        }

        return data;
    }
}