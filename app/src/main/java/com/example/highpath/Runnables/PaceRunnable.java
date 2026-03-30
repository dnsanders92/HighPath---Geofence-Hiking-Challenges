package com.example.highpath.Runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.example.highpath.Calculations.CalculatePace;

public class PaceRunnable implements Runnable {

    public static final String ACTION_PACE_UPDATE = MetricsRunnable.ACTION_METRICS_UPDATE;

    private final Context context;
    private final Handler handler;
    private final TimeRunnable timeRunnable;
    private final MetricsRunnable metricsRunnable;

    public PaceRunnable(Context context, Handler handler, TimeRunnable timeRunnable, MetricsRunnable metricsRunnable) {
        this.context = context;
        this.handler = handler;
        this.timeRunnable = timeRunnable;
        this.metricsRunnable = metricsRunnable;
    }

    @Override
    public void run() {
        // 1. Get raw values from other Runnables
        // Using 'long' for time to match our CalculatePace signature
        long currentSeconds = (long) timeRunnable.time;
        double currentDistanceKm = metricsRunnable.totalDistance;

        // 2. Calculate the raw pace (double)
        double rawPace = CalculatePace.pace(currentSeconds, currentDistanceKm);

        // 3. Convert that double into a formatted String (e.g., "05:30")
        String paceString = CalculatePace.formatPaceToString(rawPace);

        // 4. Broadcast the update
        Intent intent = new Intent(ACTION_PACE_UPDATE);
        intent.putExtra("distance", metricsRunnable.totalDistance);
        intent.putExtra("elevation", metricsRunnable.totalElevationGain);
        intent.putExtra("pace", paceString);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);

        // Run again in 10 seconds
        handler.postDelayed(this, 10000);
    }
}