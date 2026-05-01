package com.example.highpath.Fragments.OtherFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.MapBox.LoadMapBox;
import com.example.highpath.MapBox.LoadTrailPolyline;
import com.example.highpath.Models.GPXData;
import com.example.highpath.R;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CoordinateBounds;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.ArrayList;
import java.util.List;

public class TrailMapFragment extends Fragment {

    private MapView mapView;
    private GPXData gpxData;

    public TrailMapFragment() {}

    public static TrailMapFragment newInstance(GPXData data) {
        TrailMapFragment fragment = new TrailMapFragment();
        fragment.gpxData = data;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trail_map, container, false);
        mapView = view.findViewById(R.id.trailMapView);

        if (gpxData != null && !gpxData.latitudes.isEmpty()) {
            double startLat = gpxData.latitudes.get(0);
            double startLon = gpxData.longitudes.get(0);

            LoadMapBox loadMapBox = new LoadMapBox();

            // FIXED: Added Style.OUTDOORS as the 3rd argument to match the new method signature
            loadMapBox.initializeMap(
                    requireContext(),
                    mapView,
                    Style.OUTDOORS,
                    true,
                    startLat,
                    startLon
            );

            mapView.getMapboxMap().getStyle(style -> {
                drawTrail(style);
                zoomToFit();
            });
        }

        return view;
    }

    private void drawTrail(@NonNull Style style) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < gpxData.latitudes.size(); i++) {
            points.add(Point.fromLngLat(gpxData.longitudes.get(i), gpxData.latitudes.get(i)));
        }

        if (points.size() >= 2) {
            LineString lineString = LineString.fromLngLats(points);
            LoadTrailPolyline polylineLoader = new LoadTrailPolyline();
            polylineLoader.draw(style, lineString);
        }
    }

    private void zoomToFit() {
        if (gpxData.latitudes.isEmpty()) return;

        double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

        for (int i = 0; i < gpxData.latitudes.size(); i++) {
            double lat = gpxData.latitudes.get(i);
            double lon = gpxData.longitudes.get(i);
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }

        CoordinateBounds bounds = new CoordinateBounds(
                Point.fromLngLat(minLon, minLat),
                Point.fromLngLat(maxLon, maxLat),
                false
        );

        EdgeInsets padding = new EdgeInsets(100.0, 100.0, 100.0, 100.0);
        CameraOptions camera = mapView.getMapboxMap().cameraForCoordinateBounds(
                bounds, padding, 0.0, 0.0, null, null
        );

        mapView.getMapboxMap().setCamera(camera);
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
    }
}