package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.util.Locale;
import java.util.concurrent.Executors;

public class ModifyActivityFragment extends Fragment {

    private String activityId;

    private EditText editActivityName;
    private TextView tvModifyDistance, tvModifyElevation, tvModifyDuration, tvModifyCalories;
    private Button btnSaveActivity, btnDeleteActivity;

    private ActivityEntity currentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityId = getArguments().getString("activity_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_activity, container, false);

        editActivityName   = view.findViewById(R.id.editActivityName);
        tvModifyDistance   = view.findViewById(R.id.tvModifyDistance);
        tvModifyElevation  = view.findViewById(R.id.tvModifyElevation);
        tvModifyDuration   = view.findViewById(R.id.tvModifyDuration);
        tvModifyCalories   = view.findViewById(R.id.tvModifyCalories);
        btnSaveActivity    = view.findViewById(R.id.btnSaveActivity);
        btnDeleteActivity  = view.findViewById(R.id.btnDeleteActivity);

        loadActivity();
        setupButtons();

        return view;
    }

    private void loadActivity() {
        if (activityId == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            ActivityEntity activity = db.activityDao().getActivityById(activityId);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && activity != null) {
                    currentActivity = activity;
                    populateUI(activity);
                }
            });
        });
    }

    private void populateUI(ActivityEntity activity) {
        editActivityName.setText(activity.getActivityName());

        tvModifyDistance.setText(String.format(Locale.getDefault(), "%.2f km",
                activity.getDistance() != null ? activity.getDistance() : 0.0));

        tvModifyElevation.setText(String.format(Locale.getDefault(), "%d m",
                activity.getElevationGain() != null ? activity.getElevationGain().intValue() : 0));

        tvModifyDuration.setText(formatDuration(activity.getMovingTime()));

        tvModifyCalories.setText(String.format(Locale.getDefault(), "%d kcal",
                activity.getCalories() != null ? activity.getCalories().intValue() : 0));
    }

    private void setupButtons() {
        btnSaveActivity.setOnClickListener(v -> saveChanges());
        btnDeleteActivity.setOnClickListener(v -> confirmDelete());
    }

    private void saveChanges() {
        if (currentActivity == null) return;

        String newName = editActivityName.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentActivity.setActivityName(newName);

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            db.activityDao().update(currentActivity);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Activity updated", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            });
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Activity")
                .setMessage("Are you sure you want to delete this activity? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteActivity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteActivity() {
        if (currentActivity == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            db.activityDao().deleteActivity(currentActivity);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Activity deleted", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            });
        });
    }

    private String formatDuration(Long seconds) {
        if (seconds == null) return "0:00";
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s);
        return String.format(Locale.getDefault(), "%d:%02d", m, s);
    }
}