package com.example.highpath.MapBox;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.bindgen.Expected;
import com.mapbox.bindgen.Value;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;

public class LoadMapBox {

    private boolean hasCenteredOnce = false;

    private static final double DEFAULT_ZOOM    = 13.0;
    private static final double DEFAULT_PITCH   = 0.0;   // flat top-down view
    private static final double DEFAULT_BEARING = 0.0;

    public LoadMapBox() {}

    public void initializeMap(@NonNull Context context,
                              @NonNull final MapView mapView,
                              final boolean enablePuck,
                              @Nullable final Double centerLat,
                              @Nullable final Double centerLon) {

        mapView.getMapboxMap().loadStyleUri(
                Style.OUTDOORS,
                style -> {
                    if (!hasCenteredOnce) {
                        centreMap(mapView, centerLat, centerLon);
                        hasCenteredOnce = true;
                    }

                    add3DTerrain(style);

                    if (enablePuck) {
                        enableLocationPuck(mapView);
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
                            .zoom(DEFAULT_ZOOM)
                            .pitch(DEFAULT_PITCH)
                            .bearing(DEFAULT_BEARING)
                            .build()
            );
        }
    }

    private void add3DTerrain(@NonNull Style style) {
        // 1. Add the DEM raster source
        Expected<String, Value> sourceResult = Value.fromJson(
                "{" +
                        "  \"type\": \"raster-dem\"," +
                        "  \"url\": \"mapbox://mapbox.mapbox-terrain-dem-v1\"," +
                        "  \"tileSize\": 512," +
                        "  \"maxzoom\": 14" +
                        "}"
        );

        if (sourceResult.isValue() && sourceResult.getValue() != null) {
            style.addStyleSource("mapbox-dem", sourceResult.getValue());
        }

        // 2. Enable terrain and point it at the source
        Expected<String, Value> terrainResult = Value.fromJson(
                "{" +
                        "  \"source\": \"mapbox-dem\"," +
                        "  \"exaggeration\": 1.5" +
                        "}"
        );

        if (terrainResult.isValue() && terrainResult.getValue() != null) {
            style.setStyleTerrain(terrainResult.getValue());
        }
    }

    public void enableLocationPuck(@NonNull MapView mapView) {
        LocationComponentPlugin locationComponentPlugin =
                LocationComponentUtils.getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPuckBearingEnabled(true);
    }
}