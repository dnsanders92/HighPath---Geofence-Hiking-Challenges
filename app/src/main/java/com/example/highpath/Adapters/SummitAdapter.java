package com.example.highpath.Adapters;

import android.content.Context;
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
    public Object getItem(int position) { return summits.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_summit, parent, false);
            holder = new ViewHolder();
            holder.thumbnail = convertView.findViewById(R.id.img_summit_thumbnail);
            holder.name = convertView.findViewById(R.id.text_summit_name);
            holder.details = convertView.findViewById(R.id.text_summit_details);
            holder.statusBadge = convertView.findViewById(R.id.text_status_badge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SummitsEntity summit = summits.get(position);

        holder.name.setText(summit.getSummitName());

        // Combine Elevation and Prominence
        String detailText = "Elev: " + summit.getElevation() + "m | Prom: " + summit.getProminence() + "m";
        holder.details.setText(detailText);

        // Toggle badge visibility
        holder.statusBadge.setVisibility(summit.isCompleted() ? View.VISIBLE : View.GONE);

        // Ensure R.drawable.crough_patrick exists in your drawable folder!
        holder.thumbnail.setImageResource(R.drawable.crough_patrick);

        return convertView;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView details;
        TextView statusBadge;
    }
}