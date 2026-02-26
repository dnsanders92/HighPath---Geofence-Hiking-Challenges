package com.example.highpath.Fragments.OtherFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.highpath.Services.RecordingService;
import com.example.highpath.R;
import com.example.highpath.Utilities.StorageManager;
import com.example.highpath.Utilities.GPX.GpxFileName;

public class RecordingPanelFragment extends Fragment {

    private Button btnStart, btnStop;
    private String filePath, fileName;

    public RecordingPanelFragment() {
        super(R.layout.fragment_recording_panel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);

        enableStartButton();
        disableStopButton();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Get the directory path immediately
                filePath = StorageManager.getGpxPath(requireContext());

                // 2. Request the name (Database work happens in background)
                GpxFileName.generateAsync(requireContext(), new GpxFileName.GpxNameCallback() {
                    @Override
                    public void onNameGenerated(String generatedName) {
                        fileName = generatedName;

                        // 3. Logic only proceeds once the name is ready
                        disableStartButton();
                        enableStopButton();

                        // Start Service
                        RecordingService.start(requireContext(), filePath, fileName);

                        // Show confirmation
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Recording Started")
                                .setMessage("File: " + fileName + "\nLocation: " + filePath)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Stop Recording")
                        .setMessage("Confirm YES to stop recording")
                        .setPositiveButton("Yes, Stop", (dialog, which) -> {
                            disableStopButton();
                            enableStartButton();
                            RecordingService.stop(requireContext());
                        })
                        .setNegativeButton("No, Continue", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    private void disableStartButton() {
        btnStart.setEnabled(false);
        btnStart.setAlpha(0.5f);
    }

    private void enableStartButton() {
        btnStart.setEnabled(true);
        btnStart.setAlpha(1.0f);
    }

    private void disableStopButton() {
        btnStop.setEnabled(false);
        btnStop.setAlpha(0.5f);
    }

    private void enableStopButton() {
        btnStop.setEnabled(true);
        btnStop.setAlpha(1.0f);
    }
}