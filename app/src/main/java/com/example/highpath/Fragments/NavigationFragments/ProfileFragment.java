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

import com.example.highpath.Database.DatabaseServices.AddUserAccount;
import com.example.highpath.Database.DatabaseServices.GetUserAccount;
import com.example.highpath.R;

public class ProfileFragment extends Fragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView bannerImage = view.findViewById(R.id.bannerImage);
        ImageView profileImage = view.findViewById(R.id.profileImage);

        TextView userName = view.findViewById(R.id.labelUserName);
        TextView totalDistance = view.findViewById(R.id.labelTotalDistance);
        TextView totalElevation = view.findViewById(R.id.labelTotalElevation);
        TextView completedActivities = view.findViewById(R.id.labelCompletedActivities);
        TextView completedChallenges = view.findViewById(R.id.labelCompletedChallenges);
        TextView totalTimeOutside = view.findViewById(R.id.labelTimeOutside);
        TextView totalTimeMoving = view.findViewById(R.id.labelMovingTime);

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}