package com.example.highpath.Utilities.OpenWeather;

import android.widget.ImageView;
import com.example.highpath.R;

public class WeatherIcons {

    public void updateFiveDayForecast(String[] conditions, ImageView[] icons) {
        for (int i = 0; i < conditions.length && i < icons.length; i++) {
            String condition = conditions[i].toLowerCase().trim();

            switch (condition) {
                case "clear":
                case "sunny":
                    icons[i].setImageResource(R.drawable.weather_icon_sun);
                    break;
                case "clouds":
                case "broken clouds":
                case "few clouds":
                    icons[i].setImageResource(R.drawable.weather_icon_partly_cloudy);
                    break;
                case "drizzle":
                    icons[i].setImageResource(R.drawable.weather_icon_drizzle);
                    break;
                case "rain":
                    icons[i].setImageResource(R.drawable.weather_icon_rain);
                    break;
                case "thunderstorm":
                    icons[i].setImageResource(R.drawable.weather_icon_thunderstorms);
                    break;
                case "snow":
                    icons[i].setImageResource(R.drawable.weather_icon_snow);
                    break;
                case "mist":
                case "fog":
                case "haze":
                    icons[i].setImageResource(R.drawable.weather_icon_mist);
                    break;
                default:
                    icons[i].setImageResource(R.drawable.weather_icon_sun);
                    break;
            }
        }
    }
}