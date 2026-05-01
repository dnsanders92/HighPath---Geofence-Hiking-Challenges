package com.example.highpath.Fragments.NavigationFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.DatabaseServices.GetUserAccount;
import com.example.highpath.R;

public class ProfileFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connect ui
        ImageView bannerImage  = view.findViewById(R.id.bannerImage);
        ImageView profileImage = view.findViewById(R.id.profileImage);

        TextView userName            = view.findViewById(R.id.labelUserName);
        TextView totalDistance       = view.findViewById(R.id.labelTotalDistance);
        TextView totalElevation      = view.findViewById(R.id.labelTotalElevation);
        TextView completedActivities = view.findViewById(R.id.labelCompletedActivities);
        TextView completedChallenges = view.findViewById(R.id.labelCompletedChallenges);
        TextView totalTimeOutside    = view.findViewById(R.id.labelTimeOutside);
        TextView totalTimeMoving     = view.findViewById(R.id.labelMovingTime);
        TextView longestDistance     = view.findViewById(R.id.labelLongestDistance);
        TextView mostElevationGain   = view.findViewById(R.id.labelMostElevationGain);

        // load user info
        GetUserAccount.getUser(
                requireContext(),
                bannerImage,
                profileImage,
                userName,
                totalDistance,
                totalElevation,
                completedActivities,
                completedChallenges,
                totalTimeOutside,
                totalTimeMoving
        );

        // load stats in background
        Thread statsThread = new Thread(() -> {

            com.example.highpath.Database.LocalDatabase db =
                    com.example.highpath.Database.LocalDatabase.getInstance(requireContext());

            // fetch stats
            Double longest = db.activityDao().getLongestDistance();
            Double highest = db.activityDao().getHighestElevationGain();

            if (longest == null) longest = 0.0;
            if (highest == null) highest = 0.0;

            Double finalLongest = longest;
            Double finalHighest = highest;

            // update ui
            requireActivity().runOnUiThread(() -> {
                longestDistance.setText(String.format("%.2f km", finalLongest));
                mostElevationGain.setText(String.format("%.0f m", finalHighest));
            });
        });

        statsThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
