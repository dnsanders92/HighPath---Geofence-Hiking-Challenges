package com.example.highpath.Utilities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.example.highpath.Calculations.CalculatePace;
import com.example.highpath.Utilities.Formatters.FormatTime;

public class GetBroadcastReceivers {


    public static BroadcastReceiver getTimeReceiver(final TextView labelTime) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Ensure key matches your Service: "time" vs "elapsedTime"
                int seconds = intent.getIntExtra("time", 0);
                labelTime.setText(FormatTime.formatHoursMinutesSeconds(seconds));
            }
        };
    }

    /**
     * Receiver for Distance and Elevation updates
     */
    public static BroadcastReceiver getMetricsReceiver(final TextView labelDistance, final TextView labelElevation) {
        return new BroadcastReceiver() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onReceive(Context context, Intent intent) {
                double distance = intent.getDoubleExtra("distance", 0.0);
                double elevation = intent.getDoubleExtra("elevation", 0.0);

                labelDistance.setText(String.format("%.2f km", distance / 1000.0));
                labelElevation.setText(String.format("%.0f m", elevation));
            }
        };
    }

    public static BroadcastReceiver getPaceReceiver(final TextView labelPace) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Note: These keys must be included in your RecordingService's
                // RECORDING_METRICS_UPDATE broadcast intent.
                int elapsedTime = intent.getIntExtra("time", 0);
                double distance = intent.getDoubleExtra("distance", 0.0);

                // Initial pace update at 10 seconds
                if (elapsedTime == 10) {
                    CalculatePace calc = new CalculatePace();
                    labelPace.setText(calc.pace(elapsedTime, distance));
                    return;
                }

                // Periodic update every 60 seconds
                if (elapsedTime > 0 && elapsedTime % 60 == 0) {
                    CalculatePace calc = new CalculatePace();
                    labelPace.setText(calc.pace(elapsedTime, distance));
                }
            }
        };
    }
}