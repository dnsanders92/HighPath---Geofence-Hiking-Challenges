package com.example.highpath.Runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class TimeRunnable implements Runnable {

    public static final String ACTION_TIME_TICK = "com.example.highpath.TIME_TICK";

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int time = 0;

    public TimeRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        time++;

        Intent intent = new Intent(ACTION_TIME_TICK);
        intent.putExtra("time", time);
        context.sendBroadcast(intent);

        handler.postDelayed(this, 1000);
    }
}
