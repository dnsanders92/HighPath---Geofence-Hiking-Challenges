package com.example.androidhikingapplication.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LocationService implements LocationListener {

    private final Context context;
    private final LocationManager locationManager;
    private volatile Location lastLocation;

    public LocationService(Context context) {
        this.context = context.getApplicationContext();
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    3,
                    this
            );
        }
    }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
    }

    public double getLatitude() {
        return (lastLocation != null) ? lastLocation.getLatitude() : 0.0;
    }

    public double getLongitude() {
        return (lastLocation != null) ? lastLocation.getLongitude() : 0.0;
    }

    public double getElevation() {
        return (lastLocation != null) ? lastLocation.getAltitude() : 0.0;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) { }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }
}
