package com.example.highpath.Fragments.OtherFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.R;

import java.util.ArrayList;
import java.util.List;

public class ChallengeSummaryFragment extends Fragment {

    private int challengeId;
    private ImageView challengeImage;
    private TextView challengeName, challengeDescription, summitsCount;
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

        challengeImage = view.findViewById(R.id.challengeImage);
        challengeName = view.findViewById(R.id.challengeName);
        challengeDescription = view.findViewById(R.id.challengeDescription);
        summitsCount = view.findViewById(R.id.summitsCount);
        summitList = view.findViewById(R.id.summitList);

        loadChallengeDetails();
        return view;
    }

    private void loadChallengeDetails() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            ChallengesEntity challenge = db.challengesDao().getChallengeById(challengeId);

            if (challenge != null) {
                // Fetch summits using the matching region string
                List<SummitsEntity> summits = db.summitsDao().getSummitsByRegion(challenge.getRegion());

                Bitmap bitmap = null;
                if (challenge.getChallengeThumbnail() != null) {
                    byte[] bytes = challenge.getChallengeThumbnail();
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }

                Bitmap finalBitmap = bitmap;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            challengeName.setText(challenge.getChallengeName());
                            challengeDescription.setText(challenge.getLongDescription());

                            String progress = challenge.getCompletedSummits() + " / " + challenge.getTotalSummits() + " Summits";
                            if (summitsCount != null) summitsCount.setText(progress);

                            if (finalBitmap != null) challengeImage.setImageBitmap(finalBitmap);

                            setupListView(summits);
                        }
                    });
                }
            }
        }).start();
    }

    private void setupListView(List<SummitsEntity> summits) {
        if (summits == null || summits.isEmpty()) return;

        List<String> names = new ArrayList<>();
        for (SummitsEntity s : summits) {
            String status = s.isCompleted() ? " [DONE]" : "";
            names.add(s.getSummitName() + " (" + s.getElevation() + "m)" + status);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
        summitList.setAdapter(adapter);
    }
}