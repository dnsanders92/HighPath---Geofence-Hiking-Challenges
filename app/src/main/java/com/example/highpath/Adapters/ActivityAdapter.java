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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends BaseAdapter {

    private final Context context;
    private List<ActivityEntity> activities;
    private final LayoutInflater inflater;

    public ActivityAdapter(Context context, List<ActivityEntity> activities) {
        this.context = context;
        this.activities = activities;
        this.inflater = LayoutInflater.from(context);
    }

    // Refresh list
    public void updateList(List<ActivityEntity> newList) {
        this.activities = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return activities.size(); }

    @Override
    public ActivityEntity getItem(int position) { return activities.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // connect views
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

        // set text fields
        holder.activityName.setText(a.getActivityName() != null ? a.getActivityName() : "Unnamed Activity");
        holder.activityDate.setText(formatDate(a.getTimestamp()));
        holder.activityDistance.setText(String.format(Locale.UK, "%.2f km", safeDouble(a.getDistance())));
        holder.activityElevation.setText(safeInt(a.getElevationGain()) + " m");
        holder.activityTime.setText(formatTime(a.getActualTime()));
        holder.activityPace.setText(formatPace(a.getAveragePace()));

        // Show trophy badge only for challenge activities
        holder.trophyBadge.setVisibility(a.getIsChallenge() != null && a.getIsChallenge() ? View.VISIBLE : View.GONE);

        // Load trail map image if available
        byte[] imageBytes = a.getTrailMapImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.image.setImageBitmap(bitmap);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Format date from timestamp
    private String formatDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) return "--";
        return new SimpleDateFormat("dd MMM yyyy", Locale.UK).format(new Date(timestamp));
    }

    // Format total time in seconds
    private String formatTime(Long seconds) {
        if (seconds == null || seconds <= 0) return "--";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
    }

    // Format pace (min/km)
    private String formatPace(Double pace) {
        if (pace == null || pace <= 0) return "--";
        int minutes = pace.intValue();
        int seconds = (int) Math.round((pace - minutes) * 60);
        return String.format(Locale.UK, "%d:%02d/km", minutes, seconds);
    }

    //null checks
    private double safeDouble(Double d) { return d != null ? d : 0.0; }
    private int safeInt(Number n) { return n != null ? n.intValue() : 0; }

    static class ViewHolder {
        TextView activityName, activityDate, activityDistance, activityElevation, activityTime, activityPace;
        ImageView image;
        View trophyBadge;
    }
}
