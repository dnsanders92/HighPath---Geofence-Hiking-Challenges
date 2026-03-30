package com.example.highpath.Fragments.NavigationFragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.highpath.Adapters.ActivityAdapter;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.SummaryFragment;
import com.example.highpath.R;

import java.util.List;

public class ActivitiesFragment extends Fragment {

    private ListView activitiesList;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        activitiesList = view.findViewById(R.id.activities_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivities();
    }

    public void loadActivities() {
        new Thread(() -> {
            Context context = getContext();
            if (context == null) return;

            // Load from Room
            LocalDatabase db = LocalDatabase.getInstance(context);
            List<ActivityEntity> activities = db.activityDao().getAllActivities();

            // Update UI safely
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    if (getContext() != null && isAdded()) {
                        ActivityAdapter adapter = new ActivityAdapter(getContext(), activities);
                        activitiesList.setAdapter(adapter);

                        // Set Click Listener to open Summary
                        activitiesList.setOnItemClickListener((parent, view1, position, id) -> {
                            ActivityEntity clickedActivity = activities.get(position);
                            openSummary(clickedActivity.getActivityId());
                        });
                    }
                });
            }
        }).start();
    }

    private void openSummary(String activityId) {
        SummaryFragment summaryFragment = new SummaryFragment();

        // Pass the ID
        Bundle args = new Bundle();
        args.putString("activityId", activityId);
        summaryFragment.setArguments(args);

        // Swap Fragments
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, summaryFragment)
                .addToBackStack(null)
                .commit();
    }
}