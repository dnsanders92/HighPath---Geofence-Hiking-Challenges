package com.example.highpath.Fragments.OtherFragments.WeatherFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.highpath.R;
import com.example.highpath.Services.OpenWeatherAPIService;
import com.example.highpath.Utilities.OpenWeather.FiveDayWeatherData;
import com.example.highpath.Utilities.OpenWeather.ParseOpenWeather;
import com.example.highpath.Utilities.OpenWeather.WeatherIcons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FiveDayForecastFragment extends Fragment {

    private double lat;
    private double lon;

    private TextView day1, day2, day3, day4, day5;
    private TextView temp1, temp2, temp3, temp4, temp5;
    private ImageView image1, image2, image3, image4, image5;
    private WeatherIcons iconHelper;

    public FiveDayForecastFragment() {}

    public static FiveDayForecastFragment newInstance(double lat, double lon) {
        FiveDayForecastFragment fragment = new FiveDayForecastFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconHelper = new WeatherIcons();
        if (getArguments() != null) {
            lat = getArguments().getDouble("lat", 0);
            lon = getArguments().getDouble("lon", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_five_day_forecast, container, false);

        day1 = view.findViewById(R.id.dayLabel1);
        day2 = view.findViewById(R.id.dayLabel2);
        day3 = view.findViewById(R.id.dayLabel3);
        day4 = view.findViewById(R.id.dayLabel4);
        day5 = view.findViewById(R.id.dayLabel5);

        temp1 = view.findViewById(R.id.tempDay1);
        temp2 = view.findViewById(R.id.tempDay2);
        temp3 = view.findViewById(R.id.tempDay3);
        temp4 = view.findViewById(R.id.tempDay4);
        temp5 = view.findViewById(R.id.tempDay5);

        image1 = view.findViewById(R.id.day1);
        image2 = view.findViewById(R.id.day2);
        image3 = view.findViewById(R.id.day3);
        image4 = view.findViewById(R.id.day4);
        image5 = view.findViewById(R.id.day5);

        setDayLabels();
        loadWeather();
        return view;
    }

    private void setDayLabels() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.UK);
        day1.setText("Today");
        cal.add(Calendar.DAY_OF_YEAR, 1);
        day2.setText(sdf.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        day3.setText(sdf.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        day4.setText(sdf.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        day5.setText(sdf.format(cal.getTime()));
    }

    private void loadWeather() {
        OpenWeatherAPIService.getFiveDayForecast(requireContext(), lat, lon,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        if (!isAdded()) return;

                        FiveDayWeatherData data = ParseOpenWeather.parse(json);

                        if (data.HighLowTemps.size() >= 5) {
                            temp1.setText(data.HighLowTemps.get(0));
                            temp2.setText(data.HighLowTemps.get(1));
                            temp3.setText(data.HighLowTemps.get(2));
                            temp4.setText(data.HighLowTemps.get(3));
                            temp5.setText(data.HighLowTemps.get(4));

                            if (data.Conditions != null && data.Conditions.size() >= 5) {
                                String[] condArray = data.Conditions.toArray(new String[0]);
                                ImageView[] imageViews = {image1, image2, image3, image4, image5};

                                iconHelper.updateFiveDayForecast(condArray, imageViews);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {}
                });
    }
}