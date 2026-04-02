package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.example.highpath.Adapters.ChallengesAdapter;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.ChallengeSummaryFragment;
import com.example.highpath.R;
import java.util.List;

public class ChallengesFragment extends Fragment {

    private ListView challengesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);
        challengesList = view.findViewById(R.id.challenges_list_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChallenges();
    }

    private void loadChallenges() {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(requireContext());
            List<ChallengesEntity> list = db.challengesDao().getAllChallenges();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (isAdded() && list != null) {
                        ChallengesAdapter adapter = new ChallengesAdapter(getContext(), list);
                        challengesList.setAdapter(adapter);

                        challengesList.setOnItemClickListener((parent, view1, position, id) -> {
                            ChallengesEntity clicked = (ChallengesEntity) parent.getItemAtPosition(position);
                            openSummary(clicked.getChallengeId());
                        });
                    }
                });
            }
        }).start();
    }

    private void openSummary(int challengeId) {
        ChallengeSummaryFragment summaryFragment = new ChallengeSummaryFragment();
        Bundle args = new Bundle();
        args.putInt("challengeId", challengeId);
        summaryFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, summaryFragment)
                .addToBackStack(null)
                .commit();
    }
}