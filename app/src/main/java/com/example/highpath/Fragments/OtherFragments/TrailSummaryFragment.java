package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.TrackPointEntity;
import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TrailSummaryFragment extends Fragment {

    private String trailId;
    private ImageButton trailMap;
    private TextView txtName, txtRegion, txtDistance, txtAscent;
    private ListView pointsList;

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

        trailMap = view.findViewById(R.id.trailMapImage);
        txtName = view.findViewById(R.id.trailName);
        txtRegion = view.findViewById(R.id.trailRegion);
        txtDistance = view.findViewById(R.id.trailDistance);
        txtAscent = view.findViewById(R.id.trailAscent);
        pointsList = view.findViewById(R.id.trailPointsList);

        // --- NAVIGATION LOGIC ADDED HERE ---
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
        loadTrailAndPoints();
    }

    /**
     * Handles navigation to the TrailMapFragment with the trail ID
     */
    private void navigateToMap(String id) {
        TrailMapFragment mapFragment = new TrailMapFragment();

        // Pass the ID forward
        Bundle bundle = new Bundle();
        bundle.putString("TRAIL_ID", id);
        mapFragment.setArguments(bundle);

        // Perform Transaction
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, mapFragment)
                .addToBackStack(null) // Return to summary on back press
                .commit();
    }

    private void loadTrailAndPoints() {
        if (trailId == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(getContext());

            TrailEntity trail = db.trailDao().getTrailById(trailId);
            List<TrackPointEntity> points = db.trailDao().getPointsForTrail(trailId);

            List<String> pointStrings = new ArrayList<>();
            for (TrackPointEntity p : points) {
                pointStrings.add("Point " + p.getPointIndex() + ": " + p.getCoordinate() + " (" + p.getElevation() + "m)");
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded()) {
                    if (trail != null) {
                        txtName.setText(trail.getName());
                        txtRegion.setText(trail.getRegion());
                        txtDistance.setText("Distance: " + trail.getDistanceKm() + " km");
                        txtAscent.setText("Ascent: " + trail.getAscentM() + " m");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            pointStrings
                    );
                    pointsList.setAdapter(adapter);
                }
            });
        });
    }
}