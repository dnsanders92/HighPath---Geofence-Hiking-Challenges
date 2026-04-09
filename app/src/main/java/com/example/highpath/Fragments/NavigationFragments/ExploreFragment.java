package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.TrailMapFragment;
import com.example.highpath.Fragments.OtherFragments.TrailSummaryFragment;
import com.example.highpath.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ExploreFragment extends Fragment {

    private ListView trailsList;
    private List<TrailEntity> allTrails; // Store the full objects here

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        trailsList = view.findViewById(R.id.trails_list);

        // Handle the click event
        trailsList.setOnItemClickListener((parent, view1, position, id) -> {
            if (allTrails != null && position < allTrails.size()) {
                // Get the ID of the clicked trail
                String trailId = allTrails.get(position).getTrailId();
                navigateToMap(trailId);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTrails();
    }

    public void loadTrails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(getContext());
            allTrails = db.trailDao().getAllTrails(); // Save the full list

            List<String> trailNames = new ArrayList<>();
            for (TrailEntity trail : allTrails) {
                trailNames.add(trail.getName());
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            trailNames
                    );
                    trailsList.setAdapter(adapter);
                }
            });
        });
    }

    private void navigateToMap(String trailId) {
        // 1. Create the fragment instance
        TrailSummaryFragment trailSummaryFragment = new TrailSummaryFragment();

        // 2. Pass the trailId via a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("TRAIL_ID", trailId);
        trailSummaryFragment.setArguments(bundle);

        // 3. Perform the Fragment Transaction
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, trailSummaryFragment) // Use your actual container ID
                .addToBackStack(null) // Allows user to press "back" to return to the list
                .commit();
    }
}