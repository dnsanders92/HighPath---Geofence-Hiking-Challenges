package com.example.highpath.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.highpath.Database.Entities.SplitEntity;
import com.example.highpath.R;

import java.util.List;
import java.util.Locale;

public class SplitsAdapter extends ArrayAdapter<SplitEntity> {

    public SplitsAdapter(Context context, List<SplitEntity> splits) {
        super(context, 0, splits);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.splits_list_item, parent, false);
        }

        SplitEntity split = getItem(position);

        TextView splitNumber = convertView.findViewById(R.id.splitNumber);
        TextView splitPace   = convertView.findViewById(R.id.splitPace);
        TextView splitDist   = convertView.findViewById(R.id.splitDistance);

        if (split != null) {
            splitNumber.setText(String.format(Locale.UK, "KM %d", split.getSplitIndex() + 1));
            splitPace.setText(formatPace(split.getPace()));
            splitDist.setText(String.format(Locale.UK, "%.2f km", split.getDistance()));
        }

        return convertView;
    }

    private String formatPace(Double pace) {
        if (pace == null || pace <= 0) return "0:00 /km";
        int minutes = (int) (pace / 60);
        int seconds = (int) (pace % 60);
        return String.format(Locale.UK, "%d:%02d /km", minutes, seconds);
    }
}