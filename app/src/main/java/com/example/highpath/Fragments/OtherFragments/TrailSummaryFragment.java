package com.example.highpath.Fragments.OtherFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.WeatherFragments.FiveDayForecastFragment;
import com.example.highpath.R;

import java.util.concurrent.Executors;

public class TrailSummaryFragment extends Fragment {

    private String trailId;
    private ImageButton trailMap;
    private TextView txtName, txtRegion, txtDifficulty, txtDistance, txtAscent, txtDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trailId = getArguments().getString("TRAIL_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trail_summary, container, false);

        trailMap       = view.findViewById(R.id.trailMapImage);
        txtName        = view.findViewById(R.id.trailName);
        txtRegion      = view.findViewById(R.id.trailRegion);
        txtDifficulty  = view.findViewById(R.id.trailDifficulty);
        txtDistance    = view.findViewById(R.id.trailDistance);
        txtAscent      = view.findViewById(R.id.trailAscent);
        txtDescription = view.findViewById(R.id.trailDescription);

        trailMap.setOnClickListener(v -> {
            if (trailId != null) {
                navigateToMap(trailId);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTrailData();
    }

    private void navigateToMap(String id) {
        TrailMapFragment mapFragment = new TrailMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TRAIL_ID", id);
        mapFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, mapFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadTrailData() {
        if (trailId == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(getContext());
            TrailEntity trail = db.trailDao().getTrailById(trailId);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && trail != null) {
                    txtName.setText(trail.getName().toUpperCase());
                    txtRegion.setText(trail.getRegion().toUpperCase());
                    txtDistance.setText(trail.getDistanceKm() + " km");
                    txtAscent.setText(trail.getAscentM() + " m");
                    setDifficultyChip(trail.getDifficulty());

                    // Load preview image
                    byte[] previewBytes = trail.getThumbnail();
                    if (previewBytes != null && previewBytes.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(previewBytes, 0, previewBytes.length);
                        if (bitmap != null) {
                            trailMap.setImageBitmap(bitmap);
                        }
                    }

                    String description = trail.getDescription();
                    if (description != null && !description.isEmpty()) {
                        txtDescription.setVisibility(View.VISIBLE);
                        txtDescription.setText(description);
                    } else {
                        txtDescription.setVisibility(View.GONE);
                    }

                    loadWeatherFragment(trail.getCenterLat(), trail.getCenterLon());
                }
            });
        });
    }

    private void setDifficultyChip(String difficulty) {
        if (difficulty == null) return;

        String textColor;
        String bgColor;

        switch (difficulty.toLowerCase()) {
            case "easy":
                textColor = "#1D6B3A";
                bgColor   = "#E6F4EC";
                break;
            case "moderate":
                textColor = "#0F6E56";
                bgColor   = "#E1F5EE";
                break;
            case "hard":
                textColor = "#993C1D";
                bgColor   = "#FAECE7";
                break;
            case "severe":
                textColor = "#7A1F1F";
                bgColor   = "#FCEBEB";
                break;
            default:
                textColor = "#555555";
                bgColor   = "#F0F0F0";
                break;
        }

        txtDifficulty.setText(difficulty.toUpperCase());
        txtDifficulty.setTextColor(Color.parseColor(textColor));
        GradientDrawable bg = (GradientDrawable) txtDifficulty.getBackground();
        bg.setColor(Color.parseColor(bgColor));
    }

    private void loadWeatherFragment(double lat, double lon) {
        FiveDayForecastFragment fragment = FiveDayForecastFragment.newInstance(lat, lon);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.weatherContainer, fragment)
                .commit();
    }
}