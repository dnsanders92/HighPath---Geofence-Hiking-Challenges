package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Adapters.TrailsAdapter;
import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.TrailSummaryFragment;
import com.example.highpath.R;

import java.util.List;
import java.util.concurrent.Executors;

public class ExploreFragment extends Fragment {

    private ListView trailsList;
    private List<TrailEntity> allTrails;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        trailsList = view.findViewById(R.id.trails_list);

        trailsList.setOnItemClickListener((parent, view1, position, id) -> {
            if (allTrails != null && position < allTrails.size()) {
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
            allTrails = db.trailDao().getAllTrails();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && getContext() != null) {
                    TrailsAdapter adapter = new TrailsAdapter(getContext(), allTrails);
                    trailsList.setAdapter(adapter);
                }
            });
        });
    }

    private void navigateToMap(String trailId) {
        TrailSummaryFragment trailSummaryFragment = new TrailSummaryFragment();

        Bundle bundle = new Bundle();
        bundle.putString("TRAIL_ID", trailId);
        trailSummaryFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, trailSummaryFragment)
                .addToBackStack(null)
                .commit();
    }
}