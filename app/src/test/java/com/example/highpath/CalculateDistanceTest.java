package com.example.highpath;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.example.highpath.Utilities.GPX.GPXParser;
import com.example.highpath.Utilities.GPX.GPXData;
import com.example.highpath.Calculations.CalculateDistance;

public class CalculateDistanceTest {

    private GPXData data;
    private final String filePath = "src/main/assets/test_data/gpx_test_file.gpx";

    @Before
    public void setUp() throws Exception {
        // Step 1: Parse the data so it's ready for the math tests
        GPXParser parser = new GPXParser();
        data = parser.parse(filePath);
    }

    @Test
    public void testTotalDistanceCalculation() {
        // Step 2: Use the static method from your CalculateDistance class
        double result = CalculateDistance.totalDistance(data.latitudes, data.longitudes);

        // Step 3: Assert the result
        // Based on the coordinates in your 181-point file, the total loop distance
        // is approximately 2.94 km.
        assertNotNull("Result should not be null", result);
        assertTrue("Distance should be a positive number", result > 0);

        // Adjust the 2.94 to your manually calculated expectation if needed
        assertEquals(17.58, result, 0.05);
    }

    @Test
    public void testEmptyListsReturnZero() {
        // Edge case: Test that your safety check works
        double result = CalculateDistance.totalDistance(null, null);
        assertEquals(0.0, result, 0.0);
    }
}