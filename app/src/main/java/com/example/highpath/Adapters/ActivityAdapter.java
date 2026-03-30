package com.example.highpath.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends BaseAdapter {

    private final Context context;
    private final List<ActivityEntity> activities;
    private final LayoutInflater inflater;

    public ActivityAdapter(Context context, List<ActivityEntity> activities) {
        this.context = context;
        this.activities = activities;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return activities.size();
    }

    @Override
    public Object getItem(int position) {
        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activities_list_item, parent, false);

            holder = new ViewHolder();
            holder.activityName = convertView.findViewById(R.id.activityName);
            holder.activityDistance = convertView.findViewById(R.id.activityDistance);
            holder.activityElevation = convertView.findViewById(R.id.activityElevation);
            holder.activityTime = convertView.findViewById(R.id.activityTime);
            holder.activityDate = convertView.findViewById(R.id.activityDate);
            holder.image = convertView.findViewById(R.id.section1Image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActivityEntity a = activities.get(position);

        // Name
        holder.activityName.setText(a.getActivityName());

        // Distance (already rounded in ActivityBuilder)
        holder.activityDistance.setText(a.getDistance() + " km");

        // Elevation gain (rounded to 1 decimal place)
        holder.activityElevation.setText(a.getElevationGain() + " m");

        // Actual time (you can format this later)
        holder.activityTime.setText(formatTime(a.getActualTime()));

        // Date (formatted: 23 Feb 2026)
        holder.activityDate.setText(formatDate(a.getTimestamp()));

        return convertView;
    }

    // Helper Methods

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        return sdf.format(new Date(timestamp));
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    static class ViewHolder {
        TextView activityName, activityDistance, activityElevation, activityTime, activityDate;
        ImageView image;
    }
}
