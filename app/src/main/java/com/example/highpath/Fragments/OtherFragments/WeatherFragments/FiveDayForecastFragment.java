package com.example.highpath.Fragments.OtherFragments.WeatherFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.highpath.R;
import com.example.highpath.Services.OpenWeatherAPIService;
import com.example.highpath.Utilities.OpenWeather.FiveDayWeatherData;
import com.example.highpath.Utilities.OpenWeather.ParseOpenWeather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FiveDayForecastFragment extends Fragment {

    private double lat;
    private double lon;

    private TextView d1, d2, d3, d4, d5;
    private TextView t1, t2, t3, t4, t5;

    public FiveDayForecastFragment() {
    }

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

        if (getArguments() != null) {
            lat = getArguments().getDouble("lat", 0);
            lon = getArguments().getDouble("lon", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_five_day_forecast, container, false);

        d1 = view.findViewById(R.id.dayLabel1);
        d2 = view.findViewById(R.id.dayLabel2);
        d3 = view.findViewById(R.id.dayLabel3);
        d4 = view.findViewById(R.id.dayLabel4);
        d5 = view.findViewById(R.id.dayLabel5);

        t1 = view.findViewById(R.id.tempDay1);
        t2 = view.findViewById(R.id.tempDay2);
        t3 = view.findViewById(R.id.tempDay3);
        t4 = view.findViewById(R.id.tempDay4);
        t5 = view.findViewById(R.id.tempDay5);

        setDayLabels();
        loadWeather();

        return view;
    }

    private void setDayLabels() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.UK);

        d1.setText("Today");

        cal.add(Calendar.DAY_OF_YEAR, 1);
        d2.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_YEAR, 1);
        d3.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_YEAR, 1);
        d4.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_YEAR, 1);
        d5.setText(sdf.format(cal.getTime()));
    }

    private void loadWeather() {
        OpenWeatherAPIService.getFiveDayForecast(requireContext(), lat, lon,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        if (!isAdded()) return;

                        FiveDayWeatherData data = ParseOpenWeather.parse(json);

                        if (data.HighLowTemps.size() >= 5) {
                            t1.setText(data.HighLowTemps.get(0));
                            t2.setText(data.HighLowTemps.get(1));
                            t3.setText(data.HighLowTemps.get(2));
                            t4.setText(data.HighLowTemps.get(3));
                            t5.setText(data.HighLowTemps.get(4));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
    }
}
