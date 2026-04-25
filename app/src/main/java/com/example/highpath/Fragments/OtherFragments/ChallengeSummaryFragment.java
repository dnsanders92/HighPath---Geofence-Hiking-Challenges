package com.example.highpath.Fragments.OtherFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.highpath.Adapters.SummitAdapter;
import com.example.highpath.Database.Entities.ChallengeItemEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.R;

import java.util.ArrayList;
import java.util.List;

public class ChallengeSummaryFragment extends Fragment {

    private int challengeId;
    private ImageView challengeImage;
    private TextView challengeName, challengeDescription, summitsCount, progressPercent;
    private ProgressBar summitProgressBar;
    private ListView summitList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getInt("challengeId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_summary, container, false);

        challengeImage       = view.findViewById(R.id.challengeImage);
        challengeName        = view.findViewById(R.id.challengeName);
        challengeDescription = view.findViewById(R.id.challengeDescription);
        summitsCount         = view.findViewById(R.id.summitsCount);
        progressPercent      = view.findViewById(R.id.progressPercent);
        summitProgressBar    = view.findViewById(R.id.summitProgressBar);
        summitList           = view.findViewById(R.id.summitList);

        loadChallengeDetails();
        return view;
    }

    private void loadChallengeDetails() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            ChallengesEntity challenge = db.challengesDao().getChallengeById(challengeId);

            if (challenge != null) {

                // Load items from challenge_items table
                List<ChallengeItemEntity> items = db.challengeItemDao().getItemsForChallenge(challengeId);

                // For each item that has a summit_id, load the SummitsEntity
                List<SummitsEntity> summits = new ArrayList<>();
                for (ChallengeItemEntity item : items) {
                    if (item.getSummitId() != null) {
                        SummitsEntity summit = db.summitsDao().getSummitById(item.getSummitId());
                        if (summit != null) {
                            // Override the summit's completed flag with the challenge item's flag
                            // so completion is per-challenge not global
                            summit.setCompleted(item.isCompleted());
                            summits.add(summit);
                        }
                    }
                }

                // Calculate progress from challenge_items directly
                int total = items.size();
                int completed = db.challengeItemDao().getCompletedCountForChallenge(challengeId);
                int percent = (total > 0) ? (int) ((completed / (float) total) * 100) : 0;

                Bitmap bitmap = null;
                if (challenge.getChallengeThumbnail() != null) {
                    byte[] bytes = challenge.getChallengeThumbnail();
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }

                final Bitmap finalBitmap = bitmap;
                final List<SummitsEntity> finalSummits = summits;
                final int finalCompleted = completed;
                final int finalTotal = total;
                final int finalPercent = percent;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            challengeName.setText(challenge.getChallengeName());
                            challengeDescription.setText(challenge.getLongDescription());

                            summitsCount.setText(finalCompleted + " / " + finalTotal + " SUMMITS");
                            summitProgressBar.setProgress(finalPercent);
                            progressPercent.setText(finalPercent + "%");

                            if (finalBitmap != null) {
                                challengeImage.setImageBitmap(finalBitmap);
                            }

                            setupListView(finalSummits);
                        }
                    });
                }
            }
        }).start();
    }

    private void setupListView(List<SummitsEntity> summits) {
        if (summits == null || getContext() == null) return;

        SummitAdapter adapter = new SummitAdapter(requireContext(), summits);
        summitList.setAdapter(adapter);

        summitList.setOnItemClickListener((parent, view, position, id) -> {
            SummitsEntity selected = (SummitsEntity) parent.getItemAtPosition(position);
            if (selected != null) {
                openSummary(selected.getSummitId());
            }
        });
    }

    private void openSummary(int summitId) {
        SummitDataFragment summitDataFragment = new SummitDataFragment();
        Bundle args = new Bundle();
        args.putInt("summitId", summitId);
        summitDataFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, summitDataFragment)
                .addToBackStack(null)
                .commit();
    }
}