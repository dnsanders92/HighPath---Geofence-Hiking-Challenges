package com.example.highpath.Fragments.NavigationFragments;

import android.content.Context;
import android.os.Bundle;
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

import com.example.highpath.Adapters.ActivityAdapter;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.SummaryFragment;
import com.example.highpath.R;
import com.example.highpath.Utilities.DynamicSearchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivitiesFragment extends Fragment {

    private ListView activitiesList;
    private EditText searchBox;
    private Spinner sortSpinner;
    private ActivityAdapter adapter;

    private List<ActivityEntity> allActivities = new ArrayList<>();
    private List<ActivityEntity> currentFilteredList = new ArrayList<>();
    private final DynamicSearchHelper<ActivityEntity> searchHelper = new DynamicSearchHelper<>();

    public ActivitiesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        // connect ui
        activitiesList = view.findViewById(R.id.activities_list);
        searchBox = view.findViewById(R.id.searchActivities);
        sortSpinner = view.findViewById(R.id.sortSpinner);

        setupSearch();
        setupSortSpinner();

        return view;
    }

    private void setupSearch() {
        // search listener
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // filter list
                currentFilteredList = searchHelper.filter(allActivities, s.toString(), ActivityEntity::getActivityName);
                applySortAndRefresh();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSortSpinner() {
        // sort options
        String[] options = {"Newest First", "Oldest First", "Longest Distance", "Highest Elevation"};

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, options) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        // hide default text (custom icon used)
                        return new View(getContext());
                    }
                };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        // sort listener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySortAndRefresh();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applySortAndRefresh() {
        if (currentFilteredList == null || currentFilteredList.isEmpty()) return;

        int position = sortSpinner.getSelectedItemPosition();

        // sort logic
        switch (position) {
            case 0: // newest
                Collections.sort(currentFilteredList,
                        (a1, a2) -> Long.compare(a2.getTimestamp(), a1.getTimestamp()));
                break;

            case 1: // oldest
                Collections.sort(currentFilteredList,
                        Comparator.comparingLong(ActivityEntity::getTimestamp));
                break;

            case 2: // distance
                Collections.sort(currentFilteredList,
                        (a1, a2) -> Double.compare(a2.getDistance(), a1.getDistance()));
                break;

            case 3: // elevation
                Collections.sort(currentFilteredList,
                        (a1, a2) -> Double.compare(a2.getElevationGain(), a1.getElevationGain()));
                break;
        }

        // update adapter
        if (adapter != null) {
            adapter.updateList(new ArrayList<>(currentFilteredList));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivities();
    }

    public void loadActivities() {
        // load from db
        new Thread(() -> {
            Context context = getContext();
            if (context == null) return;

            LocalDatabase db = LocalDatabase.getInstance(context);
            allActivities = db.activityDao().getAllActivities();
            currentFilteredList = new ArrayList<>(allActivities);

            // update ui
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    if (getContext() != null && isAdded()) {

                        // set adapter
                        adapter = new ActivityAdapter(getContext(), currentFilteredList);
                        activitiesList.setAdapter(adapter);

                        // initial sort
                        applySortAndRefresh();

                        // item click
                        activitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                                ActivityEntity clicked = (ActivityEntity) adapter.getItem(position);
                                if (clicked != null) {
                                    openSummary(clicked.getActivityId());
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void openSummary(String activityId) {
        // open summary fragment
        SummaryFragment summaryFragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString("activityId", activityId);
        summaryFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, summaryFragment)
                .addToBackStack(null)
                .commit();
    }
}
