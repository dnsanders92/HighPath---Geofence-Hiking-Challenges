package com.example.highpath.Utilities.GPX;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.highpath.Database.LocalDatabase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GpxFileName {

    public interface GpxNameCallback {
        void onNameGenerated(String fileName);
    }

    public static void generateAsync(Context context, GpxNameCallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {

            int userId = LocalDatabase.getInstance(context).userDao().getUserId();

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String finalName = userId + "_" + timestamp + ".gpx";

            mainHandler.post(() -> callback.onNameGenerated(finalName));
        }).start();
    }
}