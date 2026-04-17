package com.example.highpath.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.highpath.Database.Entities.TrailEntity;
import com.example.highpath.R;

import java.util.List;
import java.util.Locale;

public class TrailsAdapter extends ArrayAdapter<TrailEntity> {

    public TrailsAdapter(Context context, List<TrailEntity> trails) {
        super(context, 0, trails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.trail_list_item, parent, false);
        }

        TrailEntity trail = getItem(position);

        ImageView trailImage     = convertView.findViewById(R.id.trailImage);
        TextView trailName       = convertView.findViewById(R.id.trailName);
        TextView trailRegion     = convertView.findViewById(R.id.trailRegion);
        TextView trailDistance   = convertView.findViewById(R.id.trailDistance);
        TextView trailAscent     = convertView.findViewById(R.id.trailAscent);
        TextView trailDifficulty = convertView.findViewById(R.id.trailDifficulty);

        if (trail != null) {
            // Load thumbnail from DB, fall back to drawable if null
            byte[] thumbnailBytes = trail.getPreviewImage();
            if (thumbnailBytes != null && thumbnailBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                trailImage.setImageBitmap(bitmap);
            } else {
                trailImage.setImageResource(R.drawable.errigal);
            }

            trailName.setText(trail.getName());
            trailRegion.setText(trail.getRegion());
            trailDistance.setText(String.format(Locale.UK, "%.1f km", trail.getDistanceKm()));
            trailAscent.setText(String.format(Locale.UK, "%d m", trail.getAscentM()));
            trailDifficulty.setText(trail.getDifficulty());
        }

        return convertView;
    }
}