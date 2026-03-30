package com.example.highpath.Utilities;

import java.util.Calendar;

public class SetActivityName {

    String activityName() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 11) {
            return "Morning Hike";
        }
        else if (hour >= 11 && hour < 16) {
            return "Afternoon Hike";
        }
        else if (hour >= 16 && hour < 20) {
            return "Evening Hike";
        }
        else {
            return "Night Hike";
        }
    }
}
