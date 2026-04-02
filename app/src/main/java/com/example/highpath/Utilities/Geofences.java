package com.example.highpath.Utilities;

import android.content.Context;

import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;

import java.util.ArrayList;
import java.util.List;

public class Geofences {

    public List<Integer> summitId = new ArrayList<>();
    public List<Double> latitudes = new ArrayList<>();
    public List<Double> longitudes = new ArrayList<>();

    public void getGeofences(Context context) {

        LocalDatabase localDatabase = LocalDatabase.getInstance(context);

        List <SummitsEntity> summits = localDatabase.summitsDao().getAllSummits();

        clearList();

        if (summits == null) return;

        for (int i = 0; i < summits.size(); i++) {
            summitId.add(summits.get(i).getSummitId());
            latitudes.add(summits.get(i).getLatitude());
            longitudes.add(summits.get(i).getLongitude());
        }
    }

    private void clearList() {
        summitId.clear();
        latitudes.clear();
        longitudes.clear();
    }
}
