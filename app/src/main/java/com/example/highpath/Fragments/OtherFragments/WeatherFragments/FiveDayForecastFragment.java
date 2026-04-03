package com.example.highpath.Fragments.OtherFragments.WeatherFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.highpath.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FiveDayForecastFragment extends Fragment {

    public FiveDayForecastFragment() {
    }

    public static FiveDayForecastFragment newInstance(String param1, String param2) {
        FiveDayForecastFragment fragment = new FiveDayForecastFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_five_day_forecast, container, false);

        TextView d1 = view.findViewById(R.id.dayLabel1);
        TextView d2 = view.findViewById(R.id.dayLabel2);
        TextView d3 = view.findViewById(R.id.dayLabel3);
        TextView d4 = view.findViewById(R.id.dayLabel4);
        TextView d5 = view.findViewById(R.id.dayLabel5);

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

        return view;
    }
}
