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

import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.R;

import java.util.List;
import java.util.Locale;

public class TrailsAdapter extends BaseAdapter {

    private final Context context;
    private List<TrailEntity> trails;
    private final LayoutInflater inflater;

    public TrailsAdapter(Context context, List<TrailEntity> trails) {
        this.context = context;
        this.trails = trails;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateList(List<TrailEntity> newList) {
        this.trails = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return trails.size(); }

    @Override
    public Object getItem(int position) { return trails.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.trail_list_item, parent, false);
            holder = new ViewHolder();
            holder.trailImage = convertView.findViewById(R.id.trailImage);
            holder.trailName = convertView.findViewById(R.id.trailName);
            holder.trailRegion = convertView.findViewById(R.id.trailRegion);
            holder.trailDistance = convertView.findViewById(R.id.trailDistance);
            holder.trailAscent = convertView.findViewById(R.id.trailAscent);
            holder.trailDifficulty = convertView.findViewById(R.id.trailDifficulty);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrailEntity trail = trails.get(position);
        if (trail != null) {
            byte[] thumbnailBytes = trail.getPreviewImage();
            if (thumbnailBytes != null && thumbnailBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                holder.trailImage.setImageBitmap(bitmap);
            } else {
                holder.trailImage.setImageResource(R.drawable.errigal);
            }

            holder.trailName.setText(trail.getName());
            holder.trailRegion.setText(trail.getRegion());
            holder.trailDistance.setText(String.format(Locale.UK, "%.1f km", trail.getDistanceKm()));
            holder.trailAscent.setText(String.format(Locale.UK, "%d m", trail.getAscentM()));
            holder.trailDifficulty.setText(trail.getDifficulty());
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView trailImage;
        TextView trailName, trailRegion, trailDistance, trailAscent, trailDifficulty;
    }
}