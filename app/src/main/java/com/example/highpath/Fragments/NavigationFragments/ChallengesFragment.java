package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.example.highpath.Adapters.ChallengesAdapter;
import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.OtherFragments.ChallengeSummaryFragment;
import com.example.highpath.R;
import com.example.highpath.Utilities.DynamicSearchHelper;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragment extends Fragment {

    private ListView challengesList;
    private EditText searchBox;
    private ChallengesAdapter adapter;

    // Original list from DB
    private List<ChallengesEntity> fullChallengeList = new ArrayList<>();
    private final DynamicSearchHelper<ChallengesEntity> searchHelper = new DynamicSearchHelper<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        challengesList = view.findViewById(R.id.challenges_list_view);
        searchBox = view.findViewById(R.id.searchChallenges);

        setupSearch();
        return view;
    }

    private void setupSearch() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list dynamically as the user types
                List<ChallengesEntity> filtered = searchHelper.filter(fullChallengeList, s.toString(),
                        item -> item.getChallengeName()); // Searches by name

                if (adapter != null) {
                    adapter.updateList(filtered);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
                        fullChallengeList = list;
                        // Use a copy for the adapter initially
                        adapter = new ChallengesAdapter(getContext(), new ArrayList<>(list));
                        challengesList.setAdapter(adapter);

                        challengesList.setOnItemClickListener((parent, view1, position, id) -> {
                            // Pull item from adapter (which might be filtered) rather than full list
                            ChallengesEntity clicked = (ChallengesEntity) adapter.getItem(position);
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