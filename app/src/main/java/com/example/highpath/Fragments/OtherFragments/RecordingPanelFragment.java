package com.example.highpath.Fragments.OtherFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.highpath.Runnables.TimeRunnable;
import com.example.highpath.Runnables.MetricsRunnable;
import com.example.highpath.Services.RecordingService;
import com.example.highpath.R;
import com.example.highpath.Utilities.StorageManager;
import com.example.highpath.Utilities.GPX.GpxFileName;

import java.util.Locale;

public class RecordingPanelFragment extends Fragment {

    private Button btnStart, btnStop;
    private TextView txtTimer, txtDistance, txtElevationGain, txtPace;
    private String filePath, fileName;

    // receiver for time + metrics + status
    private final BroadcastReceiver tRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;

            if (TimeRunnable.ACTION_TIME_TICK.equals(intent.getAction())) {
                int seconds = intent.getIntExtra("time", 0);
                updateTimerUI(seconds);
            }
            else if (MetricsRunnable.ACTION_METRICS_UPDATE.equals(intent.getAction())) {
                double totalKm = intent.getDoubleExtra("distance", 0.0);
                double totalGain = intent.getDoubleExtra("elevation", 0.0);
                String pace = intent.getStringExtra("pace");
                updateMetricsUI(totalKm, totalGain, pace);
            }
            else if (RecordingService.ACTION_RECORDING_STATUS.equals(intent.getAction())) {
                boolean isRecording = intent.getBooleanExtra("isRecording", false);
                buttonState(isRecording);
            }
        }
    };

    public RecordingPanelFragment() {
        super(R.layout.fragment_recording_panel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connect ui
        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        txtTimer = view.findViewById(R.id.labelTime);
        txtDistance = view.findViewById(R.id.labelDistance);
        txtElevationGain = view.findViewById(R.id.labelElevationGain);
        txtPace = view.findViewById(R.id.labelPace);

        // sync button state on load
        buttonState(RecordingService.isRecording);

        // start recording
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filePath = StorageManager.getGpxPath(requireContext());

                // generate file name async
                GpxFileName.generateAsync(requireContext(), generatedName -> {
                    fileName = generatedName;

                    disableStartButton();
                    enableStopButton();

                    RecordingService.start(requireContext(), filePath, fileName);

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Started")
                            .setMessage("Recording path: " + fileName)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });

        // stop recording
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(requireContext())
                        .setTitle("Stop")
                        .setMessage("Stop recording?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            disableStopButton();
                            enableStartButton();

                            RecordingService.stop(requireContext());

                            // reset ui
                            txtTimer.setText("00:00:00");
                            txtDistance.setText("0.00 km");
                            txtElevationGain.setText("0 m");
                            txtPace.setText("0:00 min/km");

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimeRunnable.ACTION_TIME_TICK);
        filter.addAction(MetricsRunnable.ACTION_METRICS_UPDATE);
        filter.addAction(RecordingService.ACTION_RECORDING_STATUS);

        ContextCompat.registerReceiver(
                requireContext(),
                tRec,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        requireContext().unregisterReceiver(tRec);
    }

    private void updateTimerUI(int totalSeconds) {
        // format time
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                hours, minutes, seconds);

        if (txtTimer != null) txtTimer.setText(timeString);
    }

    private void updateMetricsUI(double distance, double elevation, String pace) {
        if (txtDistance != null)
            txtDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance));

        if (txtElevationGain != null)
            txtElevationGain.setText(String.format(Locale.getDefault(), "%.0f m", elevation));

        if (txtPace != null && pace != null)
            txtPace.setText(pace);
    }

    // button helpers
    private void disableStartButton() { btnStart.setEnabled(false); btnStart.setAlpha(0.5f); }
    private void enableStartButton() { btnStart.setEnabled(true); btnStart.setAlpha(1.0f); }
    private void disableStopButton() { btnStop.setEnabled(false); btnStop.setAlpha(0.5f); }
    private void enableStopButton() { btnStop.setEnabled(true); btnStop.setAlpha(1.0f); }

    private void buttonState(boolean isRecording) {
        if (isRecording) {
            disableStartButton();
            enableStopButton();
        } else {
            enableStartButton();
            disableStopButton();
        }
    }
}
