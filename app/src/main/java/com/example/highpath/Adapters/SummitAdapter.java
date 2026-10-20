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

import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.R;

import java.util.List;

public class SummitAdapter extends BaseAdapter {

    private Context context;
    private List<SummitsEntity> summits;

    public SummitAdapter(Context context, List<SummitsEntity> summits) {
        this.context = context;
        this.summits = summits;
    }

    @Override
    public int getCount() { return summits.size(); }

    @Override
    public SummitsEntity getItem(int position) { return summits.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // connect views
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_summit, parent, false);
            holder = new ViewHolder();

            holder.thumbnail   = convertView.findViewById(R.id.img_summit_thumbnail);
            holder.name        = convertView.findViewById(R.id.text_summit_name);
            holder.details     = convertView.findViewById(R.id.text_summit_details);
            holder.statusBadge = convertView.findViewById(R.id.text_status_badge);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SummitsEntity summit = summits.get(position);

        // set text fields
        holder.name.setText(summit.getSummitName());

        String detailText = "Elev: " + summit.getElevation() + "m | Prom: " + summit.getProminence() + "m";
        holder.details.setText(detailText);

        // show completed badge
        holder.statusBadge.setVisibility(summit.isCompleted() ? View.VISIBLE : View.GONE);

        // load image from database blob
        byte[] thumbnailBytes = summit.getThumbnail();
        if (thumbnailBytes != null && thumbnailBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
            holder.thumbnail.setImageBitmap(bitmap);
        } else {
            holder.thumbnail.setImageResource(R.drawable.crough_patrick);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView details;
        TextView statusBadge;
    }
}
