package com.example.highpath.Navigation;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.highpath.Fragments.NavigationFragments.ActivitiesFragment;
import com.example.highpath.Fragments.NavigationFragments.ChallengesFragment;
import com.example.highpath.Fragments.NavigationFragments.ExploreFragment;
import com.example.highpath.Fragments.NavigationFragments.ProfileFragment;
import com.example.highpath.Fragments.NavigationFragments.RecordFragment;
import com.example.highpath.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationControl {

    public NavigationControl(FragmentManager fragmentManager, BottomNavigationView navBar) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, new RecordFragment())
                .commit();

        navBar.setSelectedItemId(R.id.track);

        navBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment fragment = null;
                int id = item.getItemId();

                if (id == R.id.explore) fragment = new ExploreFragment();
                else if (id == R.id.activities) fragment = new ActivitiesFragment();
                else if (id == R.id.track) fragment = new RecordFragment();
                else if (id == R.id.challenges) fragment = new ChallengesFragment();
                else if (id == R.id.profile) fragment = new ProfileFragment();

                if (fragment != null) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}

