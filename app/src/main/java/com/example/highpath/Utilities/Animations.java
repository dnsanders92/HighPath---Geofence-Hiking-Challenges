package com.example.highpath.Utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.highpath.R;

public class Animations {

    /**
     * Replaces a fragment with a sliding animation and adds it to the backstack.
     * * @param fragmentManager The FragmentManager (usually getParentFragmentManager() or getSupportFragmentManager())
     * @param containerId     The ID of the layout container (e.g., R.id.container)
     * @param newFragment     The fragment instance you want to navigate to
     */
    public static void fragmentTransition(FragmentManager fragmentManager, int containerId, Fragment newFragment) {
        if (fragmentManager == null) return;

        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,  // Enter: New fragment slides in from right
                        R.anim.slide_out_left,  // Exit: Old fragment slides out to left
                        R.anim.slide_in_left,   // Pop Enter: Previous fragment slides back in from left
                        R.anim.slide_out_right  // Pop Exit: Current fragment slides out to right
                )
                .replace(containerId, newFragment)
                .addToBackStack(null)
                .commit();
    }
}