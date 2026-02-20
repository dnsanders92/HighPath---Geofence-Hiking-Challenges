package com.example.highpath.MapBox;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;

public class LoadMapBox {

    private boolean hasCenteredOnce = false;

    public LoadMapBox() {
        // Explicit empty constructor
    }

    public void initializeMap(@NonNull Context context,
                              @NonNull final MapView mapView,
                              final boolean enablePuck,
                              @Nullable final Double centerLat,
                              @Nullable final Double centerLon) {

        mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        if (!hasCenteredOnce) {
                            centreMap(mapView, centerLat, centerLon);
                            hasCenteredOnce = true;
                        }

                        // Enables location puck if true.
                        if (enablePuck) {
                            enableLocationPuck(mapView);
                        }
                    }
                }
        );
    }

    public void centreMap(MapView mapView, Double centerLat, Double centerLon) {

        if (centerLat != null && centerLon != null) {
            Point centerPoint = Point.fromLngLat(centerLon, centerLat);

            mapView.getMapboxMap().setCamera(
                    new CameraOptions.Builder()
                            .center(centerPoint)
                            .zoom(12.0)
                            .build()
            );
        }
    }


    public void enableLocationPuck(@NonNull MapView mapView) {
        LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPuckBearingEnabled(true);
    }
}
