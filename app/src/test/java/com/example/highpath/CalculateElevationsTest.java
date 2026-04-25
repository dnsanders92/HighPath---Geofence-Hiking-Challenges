package com.example.highpath;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.example.highpath.Utilities.GPX.GPXParser;
import com.example.highpath.Models.GPXData;
import com.example.highpath.Calculations.CalculateElevation;

public class CalculateElevationsTest {

    private GPXData data;
    private final String filePath = "src/main/assets/test_data/gpx_test_file.gpx";

    @Before
    public void setUp() throws Exception {
        GPXParser parser = new GPXParser();
        data = parser.parse(filePath);
    }

    @Test
    public void testElevationCalculations() {
        // Calculate distance.
        double[] results = CalculateElevation.calcGainLoss(data.elevations);

        System.out.println("Actual Gain: " + results[0]);
        System.out.println("Actual Loss: " + results[1]);

        // Check values.
        assertEquals(416.04, results[0], 0.01);
        assertEquals(427.53, results[1], 0.01);
    }
}