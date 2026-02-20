package com.example.highpath.Fragments.NavigationFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Fragments.OtherFragments.RecordingPanelFragment;
import com.example.highpath.MapBox.LoadMapBox;
import com.example.highpath.R;
import com.mapbox.maps.MapView;

public class RecordFragment extends Fragment {

    private MapView mapView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        mapView = view.findViewById(R.id.RecordMapView);
        LoadMapBox mapLoader = new LoadMapBox();

        Double lat = null;
        Double lon = null;

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            lat = main.getLatitude();
            lon = main.getLongitude();
        }

        mapLoader.initializeMap(requireContext(), mapView, true, lat, lon);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_fragment_container, new RecordingPanelFragment())
                .commit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button toggleMap = view.findViewById(R.id.btnToggleStyle);
        toggleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
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

    public void centerMapFromActivity(double lat, double lon) {
        if (mapView != null) {
            // Use your existing LoadMapBox centreMap which already uses mapView.post if you revert to that
            new LoadMapBox().centreMap(mapView, lat, lon);
        }
    }

}
