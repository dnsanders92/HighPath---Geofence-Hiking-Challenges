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

        long currentSeconds = (long) timeRunnable.time;
        double currentDistanceKm = metricsRunnable.totalDistance;

        double rawPace = CalculatePace.pace(currentSeconds, currentDistanceKm);

        String paceString = CalculatePace.formatPaceToString(rawPace);

        // broadcast the update
        Intent intent = new Intent(ACTION_PACE_UPDATE);
        intent.putExtra("distance", metricsRunnable.totalDistance);
        intent.putExtra("elevation", metricsRunnable.totalElevationGain);
        intent.putExtra("pace", paceString);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);

        // run again in 10 seconds
        handler.postDelayed(this, 10000);
    }
}