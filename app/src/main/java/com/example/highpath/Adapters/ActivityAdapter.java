package com.example.highpath.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.Entities.ActivityEntity;
import com.example.highpath.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends BaseAdapter {

    private final Context context;
    private List<ActivityEntity> activities; // Removed final
    private final LayoutInflater inflater;

    public ActivityAdapter(Context context, List<ActivityEntity> activities) {
        this.context = context;
        this.activities = activities;
        this.inflater = LayoutInflater.from(context);
    }

    // New method to refresh list after searching/sorting
    public void updateList(List<ActivityEntity> newList) {
        this.activities = newList;
        notifyDataSetChanged();
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

        holder.activityName.setText(a.getActivityName() != null ? a.getActivityName() : "Unnamed Activity");
        long timestamp = a.getTimestamp() != null ? a.getTimestamp() : 0L;
        holder.activityDate.setText(formatDate(timestamp));

        double distance = a.getDistance() != null ? a.getDistance() : 0.0;
        holder.activityDistance.setText(String.format(Locale.UK, "%.2f km", distance));

        int elevation = a.getElevationGain() != null ? a.getElevationGain().intValue() : 0;
        holder.activityElevation.setText(elevation + " m");

        long actualTime = a.getActualTime() != null ? a.getActualTime() : 0L;
        holder.activityTime.setText(formatTime(actualTime));

        double pace = a.getAveragePace() != null ? a.getAveragePace() : 0.0;
        holder.activityPace.setText(formatPace(pace));

        if (holder.trophyBadge != null) {
            holder.trophyBadge.setVisibility((a.getIsChallenge() != null && a.getIsChallenge()) ? View.VISIBLE : View.GONE);
        }

        if (holder.image != null) {
            byte[] imageBytes = a.getTrailMapImage();
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    holder.image.setImageBitmap(bitmap);
                    holder.image.setVisibility(View.VISIBLE);
                } else {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private String formatDate(long timestamp) {
        if (timestamp == 0) return "--";
        return new SimpleDateFormat("dd MMM yyyy", Locale.UK).format(new Date(timestamp));
    }

    private String formatTime(long seconds) {
        if (seconds <= 0) return "--";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
    }

    private String formatPace(double paceMinPerKm) {
        if (paceMinPerKm <= 0) return "--";
        int minutes = (int) paceMinPerKm;
        int seconds = (int) Math.round((paceMinPerKm - minutes) * 60);
        return String.format(Locale.UK, "%d:%02d/km", minutes, seconds);
    }

    static class ViewHolder {
        TextView activityName, activityDate, activityDistance, activityElevation, activityTime, activityPace;
        ImageView image;
        View trophyBadge;
    }
}