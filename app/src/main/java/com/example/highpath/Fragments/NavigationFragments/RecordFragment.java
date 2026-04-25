package com.example.highpath.Fragments.NavigationFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Fragments.OtherFragments.RecordingPanelFragment;
import com.example.highpath.MapBox.DrawLivePolyline;
import com.example.highpath.MapBox.LoadMapBox;
import com.example.highpath.MapBox.LoadTrailPolyline;
import com.example.highpath.R;
import com.example.highpath.Services.RecordingService;
import com.example.highpath.Utilities.GPX.LiveTrackManager;
import com.example.highpath.Models.TrailData;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CoordinateBounds;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private MapView mapView;
    private DrawLivePolyline drawLivePolyline;
    private final Handler liveHandler = new Handler(Looper.getMainLooper());

    private final Runnable liveUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || mapView == null) return;

            // Update polyline
            LiveTrackManager manager = LiveTrackManager.getInstance();
            if (drawLivePolyline != null && manager.hasPoints()) {
                drawLivePolyline.update(manager.getLatitudes(), manager.getLongitudes());
            }

            // Follow user puck
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                double currentLat = main.getLatitude();
                double currentLon = main.getLongitude();
                if (currentLat != 0.0 && currentLon != 0.0) {
                    new LoadMapBox().centreMap(mapView, currentLat, currentLon);
                }
            }

            liveHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        mapView = view.findViewById(R.id.RecordMapView);
        LoadMapBox mapLoader = new LoadMapBox();
        drawLivePolyline = new DrawLivePolyline();

        Double lat = null;
        Double lon = null;

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            lat = main.getLatitude();
            lon = main.getLongitude();
        }

        if (TrailData.hasData()) {
            double startLat = TrailData.latitudes.get(0);
            double startLon = TrailData.longitudes.get(0);
            mapLoader.initializeMap(requireContext(), mapView, true, startLat, startLon);
            drawLoadedTrail();
        } else {
            mapLoader.initializeMap(requireContext(), mapView, true, lat, lon);
        }

        mapView.getMapboxMap().getStyle(style -> {
            if (style != null) {
                drawLivePolyline.initialize(style);

                LiveTrackManager manager = LiveTrackManager.getInstance();
                if (manager.hasPoints()) {
                    drawLivePolyline.update(manager.getLatitudes(), manager.getLongitudes());
                }

                if (RecordingService.isRecording) {
                    liveHandler.post(liveUpdateRunnable);
                }
            }
        });

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_fragment_container, new RecordingPanelFragment())
                .commit();

        return view;
    }

    private void drawLoadedTrail() {
        if (mapView == null) return;

        List<Point> points = new ArrayList<>();
        double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

        for (int i = 0; i < TrailData.latitudes.size(); i++) {
            double lat = TrailData.latitudes.get(i);
            double lon = TrailData.longitudes.get(i);

            points.add(Point.fromLngLat(lon, lat));

            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }

        LineString lineString = LineString.fromLngLats(points);

        mapView.getMapboxMap().getStyle(style -> {
            LoadTrailPolyline polylineLoader = new LoadTrailPolyline();
            polylineLoader.draw(style, lineString);
        });

        zoomToBounds(minLat, maxLat, minLon, maxLon);
    }

    private void zoomToBounds(double minLat, double maxLat, double minLon, double maxLon) {
        CoordinateBounds bounds = new CoordinateBounds(
                Point.fromLngLat(minLon, minLat),
                Point.fromLngLat(maxLon, maxLat),
                false
        );

        EdgeInsets padding = new EdgeInsets(150, 150, 150, 150);

        CameraOptions camera = mapView.getMapboxMap().cameraForCoordinateBounds(
                bounds, padding, 0.0, 0.0, null, null
        );

        mapView.getMapboxMap().setCamera(camera);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button toggleMap = view.findViewById(R.id.btnToggleStyle);
        toggleMap.setOnClickListener(v -> {
            // Add style toggle logic here if needed
        });
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        liveHandler.removeCallbacksAndMessages(null);
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
    }

    public void centerMapFromActivity(double lat, double lon) {
        if (mapView != null) {
            new LoadMapBox().centreMap(mapView, lat, lon);
        }
    }
}