package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.highpath.Fragments.NavigationFragments.RecordFragment;
import com.example.highpath.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SplashScreenFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // Prevent crash if fragment is no longer attached
            if (!isAdded()) return;

            // Show navbar now that splash is done
            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.VISIBLE);

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.container, new RecordFragment())
                    .commit();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3500);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
