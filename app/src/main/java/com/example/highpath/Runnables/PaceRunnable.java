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
        // Access public variables from the other runnables
        int currentSeconds = timeRunnable.time;
        double currentDistanceMetres = metricsRunnable.totalDistance * 1000;

        String paceString = CalculatePace.pace(currentSeconds, currentDistanceMetres);

        Intent intent = new Intent(ACTION_PACE_UPDATE);
        intent.putExtra("distance", metricsRunnable.totalDistance);
        intent.putExtra("elevation", metricsRunnable.totalElevationGain);
        intent.putExtra("pace", paceString);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);

        handler.postDelayed(this, 10000);
    }
}