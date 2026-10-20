package com.example.highpath;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.example.highpath.Calculations.CalculateTime;
import com.example.highpath.Models.GPXData;
import com.example.highpath.Utilities.GPX.GPXParser;

public class CalculateTimesTest {

    private GPXData data;
    private final String filePath = "src/main/assets/test_data/gpx_test_file.gpx";

    @Before
    public void setUp() throws Exception {
        if (data == null) {
            GPXParser parser = new GPXParser();
            data = parser.parse(filePath);
        }
    }

    @Test
    public void testTotalAndMovingTime_expectedConstants() {
        assertNotNull("GPXData must be present", data);
        assertNotNull("timestamps must be present", data.timestamps);

        long actualTotal = CalculateTime.getTotalDurationSeconds(data.timestamps);
        long actualMoving = CalculateTime.getMovingDurationSeconds(data.timestamps, data.latitudes, data.longitudes);

        System.out.println("Actual totalSeconds: " + actualTotal);
        System.out.println("Actual movingSeconds: " + actualMoving);

        // Expected values you requested
        assertEquals(18200L, actualTotal);
        assertEquals(18186L, actualMoving);
    }
}
