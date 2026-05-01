package com.example.highpath.Fragments.NavigationFragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highpath.Database.DatabaseServices.GetUserAccount;
import com.example.highpath.R;
import com.example.highpath.Utilities.UploadImage;

public class ProfileFragment extends Fragment {

    private ImageView bannerImage;
    private ImageView profileImage;
    private UploadImage uploadProfileImage;
    private UploadImage uploadBannerImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connect ui
        bannerImage  = view.findViewById(R.id.bannerImage);
        profileImage = view.findViewById(R.id.profileImage);

        TextView userName            = view.findViewById(R.id.labelUserName);
        TextView totalDistance       = view.findViewById(R.id.labelTotalDistance);
        TextView totalElevation      = view.findViewById(R.id.labelTotalElevation);
        TextView completedActivities = view.findViewById(R.id.labelCompletedActivities);
        TextView completedChallenges = view.findViewById(R.id.labelCompletedChallenges);
        TextView totalTimeOutside    = view.findViewById(R.id.labelTimeOutside);
        TextView totalTimeMoving     = view.findViewById(R.id.labelMovingTime);
        TextView longestDistance     = view.findViewById(R.id.labelLongestDistance);
        TextView mostElevationGain   = view.findViewById(R.id.labelMostElevationGain);
        ImageButton btnUploadProfile = view.findViewById(R.id.btnUploadProfile);

        // setup uploaders — must be registered before fragment is started
        uploadProfileImage = new UploadImage(this, UploadImage.TYPE_PROFILE, bytes -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() ->
                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
            }
        });

        uploadBannerImage = new UploadImage(this, UploadImage.TYPE_BANNER, bytes -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() ->
                        bannerImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
            }
        });

        // button listeners
        btnUploadProfile.setOnClickListener(v -> uploadProfileImage.launch());
        bannerImage.setOnClickListener(v -> uploadBannerImage.launch());

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

            Double longest = db.activityDao().getLongestDistance();
            Double highest = db.activityDao().getHighestElevationGain();

            if (longest == null) longest = 0.0;
            if (highest == null) highest = 0.0;

            Double finalLongest = longest;
            Double finalHighest = highest;

            requireActivity().runOnUiThread(() -> {
                longestDistance.setText(String.format("%.2f km", finalLongest));
                mostElevationGain.setText(String.format("%.0f m", finalHighest));
            });
        });

        statsThread.start();
    }
}