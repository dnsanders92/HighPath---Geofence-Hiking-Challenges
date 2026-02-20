package com.example.highpath.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.androidhikingapplication.Services.LocationService;
import com.example.highpath.Database.DatabaseServices.CheckUserState;
import com.example.highpath.Fragments.OtherFragments.SplashScreenFragment;
import com.example.highpath.Navigation.NavigationControl;
import com.example.highpath.R;
import com.example.highpath.Utilities.StorageManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LocationService locationService;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialises system folders on first launch.
        StorageManager.initialiseFolders(this);

        // Checks if user table is empty. Local db will only ever have one user.
        CheckUserState.checkUser(this);

        BottomNavigationView navBar = findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();

        new NavigationControl(fragmentManager, navBar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SplashScreenFragment())
                .commit();


        locationService = new LocationService(this);
        checkAndRequestPermissions();

    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            locationService.startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.startLocationUpdates();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationService != null) locationService.stopLocationUpdates();
    }

    public Double getLatitude() {
        return (locationService != null) ? locationService.getLatitude() : null;
    }

    public Double getLongitude() {
        return (locationService != null) ? locationService.getLongitude() : null;
    }

    public Double getElevation() {
        return (locationService != null) ? locationService.getElevation() : null;
    }
}
