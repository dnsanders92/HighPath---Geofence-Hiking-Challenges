package com.example.highpath.Fragments.OtherFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Database.Entities.TrackPointEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Fragments.NavigationFragments.RecordFragment;
import com.example.highpath.MapBox.LoadMapBox;
import com.example.highpath.MapBox.LoadTrailPolyline;
import com.example.highpath.R;
import com.example.highpath.Models.TrailData;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CoordinateBounds;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TrailMapFragment extends Fragment {

    private String trailId;
    private MapView mapView;
    private Button btnLoadTrail;

    private final List<Double> latitudeList = new ArrayList<>();
    private final List<Double> longitudeList = new ArrayList<>();
    private final List<Double> elevationList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trailId = getArguments().getString("TRAIL_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trail_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        btnLoadTrail = view.findViewById(R.id.btnLoadMap);

        loadTrailFromDatabase();

        btnLoadTrail.setOnClickListener(v -> {
            TrailData trailDataUtility = new TrailData();
            trailDataUtility.loadTrailData(latitudeList, longitudeList, elevationList);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new RecordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadTrailFromDatabase() {
        if (trailId == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            LocalDatabase db = LocalDatabase.getInstance(getContext());
            List<TrackPointEntity> points = db.trailDao().getPointsForTrail(trailId);

            if (points != null && !points.isEmpty()) {
                List<Point> mapboxPoints = new ArrayList<>();
                double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

                for (TrackPointEntity pt : points) {
                    elevationList.add(pt.getElevation());
                    String[] coords = pt.getCoordinate().split(",");

                    try {
                        double lat = Double.parseDouble(coords[0]);
                        double lon = Double.parseDouble(coords[1]);
                        latitudeList.add(lat);
                        longitudeList.add(lon);
                        mapboxPoints.add(Point.fromLngLat(lon, lat));

                        if (lat < minLat) minLat = lat;
                        if (lat > maxLat) maxLat = lat;
                        if (lon < minLon) minLon = lon;
                        if (lon > maxLon) maxLon = lon;
                    } catch (Exception e) { e.printStackTrace(); }
                }

                final double fMinLat = minLat, fMaxLat = maxLat, fMinLon = minLon, fMaxLon = maxLon;
                final LineString lineString = LineString.fromLngLats(mapboxPoints);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isAdded()) {
                        double startLat = latitudeList.get(0);
                        double startLon = longitudeList.get(0);

                        // 1. Initialize Map
                        LoadMapBox loadMapBox = new LoadMapBox();
                        loadMapBox.initializeMap(requireContext(), mapView, true, startLat, startLon);

                        // 2. Draw polyline then zoom — both inside getStyle to ensure timing
                        mapView.getMapboxMap().getStyle(style -> {
                            LoadTrailPolyline polylineLoader = new LoadTrailPolyline();
                            polylineLoader.draw(style, lineString);
                            zoomToFit(fMinLat, fMaxLat, fMinLon, fMaxLon);
                        });
                    }
                });
            }
        });
    }

    private void zoomToFit(double minLat, double maxLat, double minLon, double maxLon) {
        CoordinateBounds bounds = new CoordinateBounds(
                Point.fromLngLat(minLon, minLat),
                Point.fromLngLat(maxLon, maxLat),
                false
        );
        EdgeInsets padding = new EdgeInsets(150.0, 150.0, 150.0, 150.0);
        CameraOptions camera = mapView.getMapboxMap().cameraForCoordinateBounds(
                bounds, padding, 0.0, 0.0, null, null
        );
        mapView.getMapboxMap().setCamera(camera);
    }

    @Override
    public void onStart() { super.onStart(); if (mapView != null) mapView.onStart(); }
    @Override
    public void onStop() { super.onStop(); if (mapView != null) mapView.onStop(); }
    @Override
    public void onDestroyView() { super.onDestroyView(); if (mapView != null) mapView.onDestroy(); }
}