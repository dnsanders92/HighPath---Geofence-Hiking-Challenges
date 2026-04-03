package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.highpath.Fragments.OtherFragments.WeatherFragments.FiveDayForecastFragment;
import com.example.highpath.R;

public class SummitDataFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public SummitDataFragment() {
    }

    public static SummitDataFragment newInstance(String param1, String param2) {
        SummitDataFragment fragment = new SummitDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summit_data, container, false);

        loadWeatherFragment();

        return view;
    }

    private void loadWeatherFragment() {
        FiveDayForecastFragment fragment = new FiveDayForecastFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.weatherContainer, fragment)
                .commit();
    }
}
