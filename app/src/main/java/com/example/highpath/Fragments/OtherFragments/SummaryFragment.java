package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Adapters.SplitsAdapter;
import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.util.List;
import java.util.Locale;

public class SummaryFragment extends Fragment {

    private String activityId;
    private TextView nameView, distanceView, elevationView, actualTimeView, movingTimeView;
    private ListView splitsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
        }

        nameView       = view.findViewById(R.id.activityNameSummary);
        distanceView   = view.findViewById(R.id.distanceSummary);
        elevationView  = view.findViewById(R.id.elevationSummary);
        actualTimeView = view.findViewById(R.id.actualTimeSummary);
        movingTimeView = view.findViewById(R.id.movingTimeSummary);
        splitsList     = view.findViewById(R.id.splitsListViewSummary);

        loadActivityFromDb();
    }

    private void loadActivityFromDb() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext().getApplicationContext());
            final ActivityEntity activity = db.activityDao().getActivityById(activityId);
            final List<SplitEntity> splits = db.splitDao().getSplitsForActivity(activityId);

            if (getActivity() == null || !isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                if (activity != null) {
                    nameView.setText(activity.getActivityName());
                    distanceView.setText(formatDistance(activity.getDistance()));
                    elevationView.setText(formatElevation(activity.getElevationGain()));
                    movingTimeView.setText(formatDuration(activity.getMovingTime()));
                    actualTimeView.setText(formatDuration(activity.getActualTime()));
                }

                if (splits != null && !splits.isEmpty()) {
                    SplitsAdapter adapter = new SplitsAdapter(requireContext(), splits);
                    splitsList.setAdapter(adapter);
                    setListViewHeightBasedOnItems(splitsList);
                }
            });
        }).start();
    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private String formatDistance(double distanceKm) {
        return String.format(Locale.UK, "%.2f km", distanceKm);
    }

    private String formatElevation(double elevationM) {
        return String.format(Locale.UK, "%.0f m", elevationM);
    }

    private String formatDuration(long seconds) {
        long hrs  = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.UK, "%02d:%02d:%02d", hrs, mins, secs);
    }
}