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
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;

public class LoadMapBox {

    private static final double DEFAULT_ZOOM    = 17.0;
    private static final double DEFAULT_PITCH   = 0.0;
    private static final double DEFAULT_BEARING = 0.0;

    // Errigal default coordinates
    private static final double ERRIGAL_LAT = 55.0343;
    private static final double ERRIGAL_LON = -8.113;

    public LoadMapBox() {}

    public void initializeMap(@NonNull Context context,
                              @NonNull final MapView mapView,
                              final boolean enablePuck,
                              @Nullable final Double centerLat,
                              @Nullable final Double centerLon) {

        mapView.getMapboxMap().loadStyleUri(
                Style.OUTDOORS,
                style -> {
                    // set camera only after style is ready
                    centreMap(mapView, centerLat, centerLon);

                    add3DTerrain(style);

                    if (enablePuck) {
                        enableLocationPuck(mapView);
                    }
                }
        );
    }

    public void centreMap(MapView mapView, Double centerLat, Double centerLon) {
        // fall back to Errigal if no coordinates provided
        double lat = (centerLat != null && centerLat != 0.0) ? centerLat : ERRIGAL_LAT;
        double lon = (centerLon != null && centerLon != 0.0) ? centerLon : ERRIGAL_LON;

        Point centerPoint = Point.fromLngLat(lon, lat);

        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder()
                        .center(centerPoint)
                        .zoom(DEFAULT_ZOOM)
                        .pitch(DEFAULT_PITCH)
                        .bearing(DEFAULT_BEARING)
                        .build()
        );
    }

    private void add3DTerrain(@NonNull Style style) {
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