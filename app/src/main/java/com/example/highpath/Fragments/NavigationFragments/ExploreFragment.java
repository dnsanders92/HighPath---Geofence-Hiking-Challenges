package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Adapters.TrailsAdapter;
import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.TrailSummaryFragment;
import com.example.highpath.R;
import com.example.highpath.Utilities.DynamicSearchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

public class ExploreFragment extends Fragment {

    private ListView trailsList;
    private EditText searchBox;
    private Spinner sortSpinner;
    private TrailsAdapter adapter;

    private List<TrailEntity> allTrails = new ArrayList<>();
    private List<TrailEntity> currentDisplayList = new ArrayList<>();
    private final DynamicSearchHelper<TrailEntity> searchHelper = new DynamicSearchHelper<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // connect ui
        trailsList = view.findViewById(R.id.trails_list);
        searchBox = view.findViewById(R.id.searchTrails);
        sortSpinner = view.findViewById(R.id.sortSpinner);

        // setup search + sort
        setupSearch();
        setupSortSpinner();

        trailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                TrailEntity clickedTrail = (TrailEntity) parent.getItemAtPosition(position);
                if (clickedTrail != null) {
                    navigateToMap(clickedTrail.getTrailId());
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTrails();
    }

    private void setupSearch() {
        if (searchBox == null) return;

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentDisplayList = searchHelper.filter(allTrails, s.toString(), TrailEntity::getName);
                applyCurrentSort();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSortSpinner() {
        // sort options
        String[] options = {
                "Shortest Distance",
                "Longest Distance",
                "Elevation (High to Low)",
                "Elevation (Low to High)"
        };

        // adapter
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return new View(getContext());
                    }
                };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        // sort listener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyCurrentSort();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyCurrentSort() {
        if (currentDisplayList == null || currentDisplayList.isEmpty()) return;

        int selection = sortSpinner.getSelectedItemPosition();

        // sort logic
        switch (selection) {
            case 0: // shortest
                Collections.sort(currentDisplayList,
                        Comparator.comparingDouble(TrailEntity::getDistanceKm));
                break;

            case 1: // longest
                Collections.sort(currentDisplayList,
                        (t1, t2) -> Double.compare(t2.getDistanceKm(), t1.getDistanceKm()));
                break;

            case 2: // elevation high → low
                Collections.sort(currentDisplayList,
                        (t1, t2) -> Integer.compare(t2.getAscentM(), t1.getAscentM()));
                break;

            case 3: // elevation low → high
                Collections.sort(currentDisplayList,
                        Comparator.comparingInt(TrailEntity::getAscentM));
                break;
        }

        // update adapter
        if (adapter != null) {
            adapter.updateList(new ArrayList<>(currentDisplayList));
        }
    }

    public void loadTrails() {
        // load from db
        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(getContext());
            allTrails = db.trailDao().getAllTrails();
            currentDisplayList = new ArrayList<>(allTrails);

            // update ui
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && getContext() != null) {
                    adapter = new TrailsAdapter(getContext(), new ArrayList<>(currentDisplayList));
                    trailsList.setAdapter(adapter);
                    applyCurrentSort();
                }
            });
        });
    }

    private void navigateToMap(String trailId) {
        // open summary fragment
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
