package com.example.highpath.MapBox;

import android.graphics.Color;
import androidx.annotation.NonNull;

import com.mapbox.geojson.LineString;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap;
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;

public class LoadTrailPolyline {

    private static final String SOURCE_ID = "trail-source-id";
    private static final String LAYER_ID  = "trail-layer-id";

    // draw polyline on map
    public void draw(@NonNull Style style, @NonNull LineString lineString) {

        // source
        GeoJsonSource trailSource = new GeoJsonSource.Builder(SOURCE_ID)
                .geometry(lineString)
                .build();
        trailSource.bindTo(style);

        // layer (sky blue)
        LineLayer trailLayer = new LineLayer(LAYER_ID, SOURCE_ID)
                .lineColor(Color.parseColor("#87CEEB"))
                .lineWidth(6.0)
                .lineCap(LineCap.ROUND)
                .lineJoin(LineJoin.ROUND);

        trailLayer.bindTo(style);
    }
}
