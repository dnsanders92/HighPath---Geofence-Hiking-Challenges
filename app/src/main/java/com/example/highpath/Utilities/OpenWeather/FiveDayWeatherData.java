package com.example.highpath.Utilities.OpenWeather;

import java.util.ArrayList;
import java.util.List;

public class FiveDayWeatherData {

    public List<Double> HighTemps = new ArrayList<>();
    public List<Double> LowTemps = new ArrayList<>();
    public List<String> HighLowTemps = new ArrayList<>();

    public void buildHighLowStrings() {
        HighLowTemps.clear();
        for (int i = 0; i < HighTemps.size(); i++) {
            HighLowTemps.add(Math.round(HighTemps.get(i)) + "° / " + Math.round(LowTemps.get(i)) + "°");
        }
    }

}
