package com.example.highpath.MapBox;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.generated.CircleLayer;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap;
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class DrawLivePolyline {

    private static final String SOURCE_ID       = "live-track-source";
    private static final String LAYER_ID        = "live-track-layer";
    private static final String START_SOURCE_ID = "live-start-source";
    private static final String START_LAYER_ID  = "live-start-layer";

    private GeoJsonSource liveSource;
    private GeoJsonSource startSource;
    private boolean isInitialized = false;

    public void initialize(@NonNull Style style) {
        // Polyline source and layer
        liveSource = new GeoJsonSource.Builder(SOURCE_ID).build();
        liveSource.bindTo(style);

        LineLayer liveLayer = new LineLayer(LAYER_ID, SOURCE_ID)
                .lineColor(Color.parseColor("#1D9E75"))
                .lineWidth(5.0)
                .lineCap(LineCap.ROUND)
                .lineJoin(LineJoin.ROUND);
        liveLayer.bindTo(style);

        // Start point source and layer
        startSource = new GeoJsonSource.Builder(START_SOURCE_ID).build();
        startSource.bindTo(style);

        CircleLayer startCircle = new CircleLayer(START_LAYER_ID, START_SOURCE_ID)
                .circleRadius(8.0)
                .circleColor(Color.parseColor("#1D9E75"))
                .circleStrokeWidth(2.0)
                .circleStrokeColor(Color.WHITE);
        startCircle.bindTo(style);

        isInitialized = true;
    }

    public void update(@NonNull List<Double> latitudes, @NonNull List<Double> longitudes) {
        if (!isInitialized || liveSource == null) return;
        if (latitudes.size() < 2) return;

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < latitudes.size(); i++) {
            points.add(Point.fromLngLat(longitudes.get(i), latitudes.get(i)));
        }

        liveSource.geometry(LineString.fromLngLats(points));

        // Pin the start circle to the first point
        if (startSource != null) {
            startSource.geometry(points.get(0));
        }
    }
}