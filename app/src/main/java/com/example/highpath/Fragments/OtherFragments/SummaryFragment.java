package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.util.Locale;

public class SummaryFragment extends Fragment {

    private String activityId;
    private TextView nameView, distanceView, elevationView, actualTimeView, movingTimeView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the ID passed from ActivitiesFragment
        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
        }

        nameView = view.findViewById(R.id.activityNameSummary);
        distanceView = view.findViewById(R.id.distanceSummary);
        elevationView = view.findViewById(R.id.elevationSummary);
        actualTimeView = view.findViewById(R.id.actualTimeSummary);
        movingTimeView = view.findViewById(R.id.movingTimeSummary);

        loadActivityFromDb();
    }

    private void loadActivityFromDb() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext().getApplicationContext());
            final ActivityEntity activity = db.activityDao().getActivityById(activityId);

            if (getActivity() == null || !isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                if (activity != null && isAdded()) {
                    nameView.setText(activity.getActivityName());
                    distanceView.setText(formatDistance(activity.getDistance()));
                    elevationView.setText(formatElevation(activity.getElevationGain()));
                    movingTimeView.setText(formatDuration(activity.getMovingTime()));
                    actualTimeView.setText(formatDuration(activity.getActualTime()));
                }
            });
        }).start();
    }

    private String formatDistance(double distanceKm) {
        return String.format(Locale.UK, "%.2f km", distanceKm);
    }

    private String formatElevation(double elevationM) {
        return String.format(Locale.UK, "%.0f m", elevationM);
    }

    private String formatDuration(long seconds) {
        long hrs = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.UK, "%02d:%02d:%02d", hrs, mins, secs);
    }
}