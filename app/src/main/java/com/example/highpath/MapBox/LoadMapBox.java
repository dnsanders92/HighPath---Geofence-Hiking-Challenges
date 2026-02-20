package com.example.highpath.MapBox;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;

public class LoadMapBox {

    public LoadMapBox() {
        // Explicit empty constructor
    }

    public void initializeMap(@NonNull Context context, @NonNull final MapView mapView, boolean enablePuck) {

        mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Default camera position (example: Dublin)
                        mapView.getMapboxMap().setCamera(
                                new CameraOptions.Builder()
                                        .center(Point.fromLngLat(-6.2603, 53.3498))
                                        .zoom(12.0)
                                        .build()
                        );

                        // enable puck now that the style is loaded
                        if (enablePuck) {
                            enableLocationPuck(mapView);
                        }
                    }
                }
        );
    }

    /** Call-safe helper that enables the Mapbox location puck for the provided MapView. */
    public void enableLocationPuck(@NonNull MapView mapView) {
        LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPuckBearingEnabled(true);
    }
}
