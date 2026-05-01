package com.example.highpath.Fragments.OtherFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.WeatherFragments.FiveDayForecastFragment;
import com.example.highpath.R;

public class SummitDataFragment extends Fragment {

    private int summitId;

    private ImageView summitImage;
    private TextView summitName;
    private TextView elevation;
    private TextView prominence;
    private TextView latitude;
    private TextView longitude;
    private TextView description;

    public SummitDataFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            summitId = getArguments().getInt("summitId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summit_data, container, false);

        // connect ui
        summitImage = view.findViewById(R.id.summitImage);
        summitName = view.findViewById(R.id.summitName);
        elevation = view.findViewById(R.id.summitHeight);
        prominence = view.findViewById(R.id.summitProminence);
        latitude = view.findViewById(R.id.summitLatitude);
        longitude = view.findViewById(R.id.summitLongitude);
        description = view.findViewById(R.id.summitDescription);

        loadSummitDetails();

        return view;
    }

    // load summit details from db.
    private void loadSummitDetails() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            SummitsEntity summit = db.summitsDao().getSummitById(summitId);

            if (summit == null) return;

            requireActivity().runOnUiThread(() -> {
                summitName.setText(summit.getSummitName());
                elevation.setText(summit.getElevation() + "m");
                prominence.setText(summit.getProminence() + "m");
                latitude.setText(String.valueOf(summit.getLatitude()));
                longitude.setText(String.valueOf(summit.getLongitude()));
                description.setText(summit.getDescription());

                // --- NEW: Load summit image from DB ---
                byte[] imageBytes = summit.getSummitImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    summitImage.setImageBitmap(bitmap);
                } else {
                    summitImage.setImageResource(R.drawable.errigal); // fallback
                }

                loadWeatherFragment(summit.getLatitude(), summit.getLongitude());
            });
        }).start();
    }

    private void loadWeatherFragment(double lat, double lon) {
        FiveDayForecastFragment fragment = FiveDayForecastFragment.newInstance(lat, lon);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.weatherContainer, fragment)
                .commit();
    }
}
