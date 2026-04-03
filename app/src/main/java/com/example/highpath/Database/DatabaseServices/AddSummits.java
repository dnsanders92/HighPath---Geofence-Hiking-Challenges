package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;
import java.util.ArrayList;
import java.util.List;

public class AddSummits {

    private static final String TAG = "AddSummits";

    public void insertSummits(final Context context) {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(context);

            if (db.summitsDao().getAllSummits().size() > 0) {
                Log.d(TAG, "Summits already exist. Skipping.");
                return;
            }

            List<SummitsEntity> summits = new ArrayList<>();

            // --- LEINSTER (10 Unique Summits for 12 Counties) ---
            summits.add(create("Lugnaquilla", "Leinster", 925, 52.966, -6.465)); // Wicklow
            summits.add(create("Mount Leinster", "Leinster", 794, 52.617, -6.783)); // Carlow & Wexford
            summits.add(create("Kippure", "Leinster", 757, 53.175, -6.331)); // Dublin
            summits.add(create("Arderin", "Leinster", 527, 53.003, -7.652)); // Laois & Offaly
            summits.add(create("Brandon Hill", "Leinster", 515, 52.510, -6.911)); // Kilkenny
            summits.add(create("Cupidstown Hill", "Leinster", 379, 53.221, -6.524)); // Kildare
            summits.add(create("Slieve Foye", "Leinster", 589, 54.049, -6.216)); // Louth
            summits.add(create("Slieve na Calliagh", "Leinster", 276, 53.743, -7.118)); // Meath
            summits.add(create("Mullaghmeen", "Leinster", 258, 53.766, -7.283)); // Westmeath
            summits.add(create("Corn Hill", "Leinster", 278, 53.791, -7.712)); // Longford

            // --- MUNSTER (5 Unique Summits for 6 Counties) ---
            summits.add(create("Carrauntoohil", "Munster", 1038, 51.999, -9.742)); // Kerry
            summits.add(create("Galtymore", "Munster", 918, 52.365, -8.178)); // Limerick & Tipperary
            summits.add(create("Knockboy", "Munster", 706, 51.801, -9.444)); // Cork
            summits.add(create("Knockmealdown", "Munster", 794, 52.228, -7.919)); // Waterford
            summits.add(create("Moylussa", "Munster", 532, 52.846, -8.513)); // Clare

            // --- ULSTER (7 Unique Summits for 9 Counties) ---
            summits.add(create("Slieve Donard", "Ulster", 850, 55.231, -7.496)); // Down
            summits.add(create("Errigal", "Ulster", 751, 55.2310, -8.112)); // Donegal
            summits.add(create("Sawel Mountain", "Ulster", 678, 54.819, -7.037)); // Derry & Tyrone
            summits.add(create("Cuilcagh", "Ulster", 665, 54.200, -7.800)); // Fermanagh & Cavan
            summits.add(create("Slieve Gullion", "Ulster", 573, 54.127, -6.431)); // Armagh
            summits.add(create("Trostan", "Ulster", 550, 55.042, -6.155)); // Antrim
            summits.add(create("Sliabh Beagh", "Ulster", 380, 54.316, -7.116)); // Monaghan

            // --- CONNACHT (4 Unique Summits for 5 Counties) ---
            summits.add(create("Mweelrea", "Connacht", 814, 53.637, -9.835)); // Mayo
            summits.add(create("Benbaun", "Connacht", 729, 53.522, -9.833)); // Galway
            summits.add(create("Truskmore", "Connacht", 647, 54.372, -8.371)); // Sligo & Leitrim
            summits.add(create("Mount Mary", "Connacht", 163, 53.518, -8.384)); // Roscommon

            for (SummitsEntity s : summits) {
                db.summitsDao().insertSummit(s);
            }
            Log.d(TAG, "Successfully inserted 26 unique geographic high points.");
        }).start();
    }

    private SummitsEntity create(String name, String province, int elevation, double lat, double lon) {
        SummitsEntity s = new SummitsEntity();
        s.setSummitName(name);
        s.setRegion(province);
        s.setElevation(elevation);
        s.setLatitude(lat);
        s.setLongitude(lon);
        s.setCompleted(false);
        return s;
    }
}