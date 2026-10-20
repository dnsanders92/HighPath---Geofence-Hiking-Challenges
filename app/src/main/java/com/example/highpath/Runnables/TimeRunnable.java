package com.example.highpath.Runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class TimeRunnable implements Runnable {

    public static final String ACTION_TIME_TICK = "com.example.highpath.TIME_TICK";

    private final Context context;
    private final Handler handler;
    public int time = 0;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TimeRunnable(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        time++;
        Intent intent = new Intent(ACTION_TIME_TICK);
        intent.putExtra("time", time);

        intent.setPackage(context.getPackageName());

        context.sendBroadcast(intent);
        handler.postDelayed(this, 1000);
    }
}