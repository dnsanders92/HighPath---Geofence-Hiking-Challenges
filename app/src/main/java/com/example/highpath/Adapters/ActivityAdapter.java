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
    public int getCount() { return activities.size(); }

    @Override
    public Object getItem(int position) { return activities.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activities_list_item, parent, false);

            holder = new ViewHolder();
            holder.activityName      = convertView.findViewById(R.id.activityName);
            holder.activityDate      = convertView.findViewById(R.id.activityDate);
            holder.activityDistance  = convertView.findViewById(R.id.activityDistance);
            holder.activityElevation = convertView.findViewById(R.id.activityElevation);
            holder.activityTime      = convertView.findViewById(R.id.activityTime);
            holder.activityPace      = convertView.findViewById(R.id.activityPace);
            holder.image             = convertView.findViewById(R.id.section1Image);
            holder.trophyBadge       = convertView.findViewById(R.id.trophyBadgeContainer);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActivityEntity a = activities.get(position);

        holder.activityName.setText(a.getActivityName());
        holder.activityDate.setText(formatDate(a.getTimestamp()));
        holder.activityDistance.setText(String.format(Locale.UK, "%.2f km", a.getDistance()));
        holder.activityElevation.setText((int)a.getElevationGain() + " m");
        holder.activityTime.setText(formatTime(a.getActualTime()));
        holder.activityPace.setText(formatPace(a.getAveragePace()));

        if (holder.trophyBadge != null) {
            if (a.isChallenge()) {
                holder.trophyBadge.setVisibility(View.VISIBLE);
            } else {
                holder.trophyBadge.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        return sdf.format(new Date(timestamp));
    }

    private String formatTime(long seconds) {
        long hours   = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    private String formatPace(double paceMinPerKm) {
        if (paceMinPerKm <= 0) return "--";
        int minutes = (int) paceMinPerKm;
        int seconds = (int) Math.round((paceMinPerKm - minutes) * 60);
        return String.format(Locale.UK, "%d:%02d/km", minutes, seconds);
    }

    static class ViewHolder {
        TextView activityName, activityDate, activityDistance,
                activityElevation, activityTime, activityPace;
        ImageView image;
        View trophyBadge;
    }
}