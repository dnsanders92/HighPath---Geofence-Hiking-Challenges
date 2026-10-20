package com.example.highpath.Fragments.OtherFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageView trailMapImage;
    private ImageButton btnEditActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get id
        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
        }

        // connect ui
        nameView        = view.findViewById(R.id.activityNameSummary);
        distanceView    = view.findViewById(R.id.distanceSummary);
        elevationView   = view.findViewById(R.id.elevationSummary);
        actualTimeView  = view.findViewById(R.id.actualTimeSummary);
        movingTimeView  = view.findViewById(R.id.movingTimeSummary);
        splitsList      = view.findViewById(R.id.splitsListViewSummary);
        trailMapImage   = view.findViewById(R.id.gpxTrailMap);
        btnEditActivity = view.findViewById(R.id.btnEditActivity);

        // edit button
        btnEditActivity.setOnClickListener(v -> {
            ModifyActivityFragment fragment = new ModifyActivityFragment();
            Bundle args = new Bundle();
            args.putString("activity_id", activityId);
            fragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // load data
        loadActivityFromDb();
    }

    // load activity + splits
    private void loadActivityFromDb() {
        new Thread(() -> {

            LocalDatabase db = LocalDatabase.getInstance(requireContext().getApplicationContext());

            // fetch data
            ActivityEntity activity = db.activityDao().getActivityById(activityId);
            List<SplitEntity> splits = db.splitDao().getSplitsForActivity(activityId);

            if (getActivity() == null || !isAdded()) return;

            // update ui
            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                // fill activity info
                if (activity != null) {
                    nameView.setText(activity.getActivityName());
                    distanceView.setText(formatDistance(activity.getDistance()));
                    elevationView.setText(formatElevation(activity.getElevationGain()));
                    movingTimeView.setText(formatDuration(activity.getMovingTime()));
                    actualTimeView.setText(formatDuration(activity.getActualTime()));

                    // load trail map image
                    if (trailMapImage != null) {
                        byte[] imageBytes = activity.getTrailMapImage();

                        if (imageBytes != null && imageBytes.length > 0) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            if (bitmap != null) {
                                trailMapImage.setImageBitmap(bitmap);
                                trailMapImage.setVisibility(View.VISIBLE);
                            } else {
                                trailMapImage.setVisibility(View.GONE);
                            }
                        } else {
                            trailMapImage.setVisibility(View.GONE);
                        }
                    }
                }

                // fill splits list
                if (splits != null && !splits.isEmpty()) {
                    SplitsAdapter adapter = new SplitsAdapter(requireContext(), splits);
                    splitsList.setAdapter(adapter);
                    setListViewHeightBasedOnItems(splitsList);
                }
            });
        }).start();
    }

    // expand listview to fit all items
    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;

        // measure each row
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);

            item.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            totalHeight += item.getMeasuredHeight();
        }

        // apply height
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // format helpers
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