package com.example.highpath.Fragments.OtherFragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.highpath.R;
import com.example.highpath.Services.RecordingService;
import com.example.highpath.Utilities.GetBroadcastReceivers;

public class RecordingPanelFragment extends Fragment {

    private Button btnStart, btnStop;
    private TextView distanceLabel, elevationLabel, paceLabel, timeLabel;

    private BroadcastReceiver metricsReceiver;
    private BroadcastReceiver paceReceiver;
    private BroadcastReceiver timeReceiver;

    public RecordingPanelFragment() {
        super(R.layout.fragment_recording_panel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);

        distanceLabel = view.findViewById(R.id.labelDistance);
        elevationLabel = view.findViewById(R.id.labelElevationGain);
        paceLabel = view.findViewById(R.id.labelPace);
        timeLabel = view.findViewById(R.id.labelTime);

        disableStopButton();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = btnStart.getText().toString();
                handleStartButtonClick(state);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Stop Recording")
                        .setMessage("Confirm YES to stop recording")
                        .setPositiveButton("Yes, Stop", (dialog, which) -> {
                            RecordingService.stop(requireContext());
                            resetUI();
                        })
                        .setNegativeButton("No, Continue", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        // Initialize receivers
        metricsReceiver = GetBroadcastReceivers.getMetricsReceiver(distanceLabel, elevationLabel);
        paceReceiver = GetBroadcastReceivers.getPaceReceiver(paceLabel);
        timeReceiver = GetBroadcastReceivers.getTimeReceiver(timeLabel);
    }

    private void handleStartButtonClick(String state) {
        switch (state) {
            case "Start":
            case "Resume":
                RecordingService.startOrResume(requireContext());
                btnStart.setText("Pause");
                disableStopButton();
                break;
            case "Pause":
                RecordingService.pause(requireContext());
                btnStart.setText("Resume");
                enableStopButton();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void resetUI() {
        btnStart.setText("Start");
        timeLabel.setText("00:00:00");
        distanceLabel.setText("0.00 km");
        elevationLabel.setText("0 m");
        paceLabel.setText("0.0");
        disableStopButton();
    }

    private void disableStopButton() {
        btnStop.setEnabled(false);
        btnStop.setAlpha(0.5f);
    }

    private void enableStopButton() {
        btnStop.setEnabled(true);
        btnStop.setAlpha(1.0f);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter timeFilter = new IntentFilter(RecordingService.ACTION_TIME_TICK);
        IntentFilter metricsFilter = new IntentFilter(RecordingService.ACTION_METRICS_UPDATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(timeReceiver, timeFilter, Context.RECEIVER_EXPORTED);
            requireActivity().registerReceiver(metricsReceiver, metricsFilter, Context.RECEIVER_EXPORTED);
            requireActivity().registerReceiver(paceReceiver, metricsFilter, Context.RECEIVER_EXPORTED);
        } else {
            requireActivity().registerReceiver(timeReceiver, timeFilter);
            requireActivity().registerReceiver(metricsReceiver, metricsFilter);
            requireActivity().registerReceiver(paceReceiver, metricsFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            requireActivity().unregisterReceiver(timeReceiver);
            requireActivity().unregisterReceiver(metricsReceiver);
            requireActivity().unregisterReceiver(paceReceiver);
        } catch (IllegalArgumentException e) {
            // Safe catch
        }
    }
}