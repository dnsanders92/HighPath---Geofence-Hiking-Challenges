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

import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.R;

import java.util.List;

public class ChallengesAdapter extends BaseAdapter {

    private List<ChallengesEntity> challenges;
    private final LayoutInflater inflater;

    public ChallengesAdapter(Context context, List<ChallengesEntity> challenges) {
        this.challenges = challenges;
        this.inflater = LayoutInflater.from(context);
    }

    // refresh list
    public void updateList(List<ChallengesEntity> newList) {
        this.challenges = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return challenges.size(); }

    @Override
    public ChallengesEntity getItem(int position) { return challenges.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // connect views
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.challenges_list_item, parent, false);
            holder = new ViewHolder();

            holder.challengeName        = convertView.findViewById(R.id.challengeName);
            holder.challengeDescription = convertView.findViewById(R.id.challengeDescription);
            holder.challengeProgressText = convertView.findViewById(R.id.challengeProgressText);
            holder.challengeImage       = convertView.findViewById(R.id.challengeImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChallengesEntity challengesEntity = challenges.get(position);

        // set text fields
        holder.challengeName.setText(challengesEntity.getChallengeName());
        holder.challengeDescription.setText(challengesEntity.getShortDescription());

        String progress = challengesEntity.getCompletedSummits() + " / " + challengesEntity.getTotalSummits();
        holder.challengeProgressText.setText(progress);

        // load image from database blob
        byte[] imageBytes = challengesEntity.getChallengeThumbnail();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.challengeImage.setImageBitmap(bitmap);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView challengeName, challengeDescription, challengeProgressText;
        ImageView challengeImage;
    }
}
