package com.example.highpath;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.example.highpath.Utilities.GPX.GPXParser;
import com.example.highpath.Utilities.GPX.GPXData;

public class gpxParseTest {

    private GPXParser parser;
    private final String filePath = "src/main/assets/test_data/gpx_test_file.gpx";

    @Before
    public void setUp() {
        parser = new GPXParser();
    }

    @Test
    public void testGpxParsingMatchesFileData() throws Exception {
        // Act
        GPXData data = parser.parse(filePath);

        // Assert - Basic structure
        assertNotNull("Data should not be null", data);
        assertEquals("Should have exactly 5234 entries", 5234, data.latitudes.size());

        // Assert - First Data Point (Index 0)
        // Values from the top of your updated GPX file
        assertEquals(55.10725, data.latitudes.get(0), 0.000001); //
        assertEquals(-6.93993, data.longitudes.get(0), 0.000001); //
        assertEquals(15.35, data.elevations.get(0), 0.00001); //
        assertEquals("2025-06-29T10:51:27Z", data.timestamps.get(0)); //

        // Assert - Last Data Point (Index 5233)
        // Values from the bottom of your updated GPX file
        int lastIndex = data.latitudes.size() - 1;
        assertEquals(55.10726, data.latitudes.get(lastIndex), 0.000001); // [cite: 56]
        assertEquals(-6.93993, data.longitudes.get(lastIndex), 0.000001); // [cite: 56]
        assertEquals(15.2, data.elevations.get(lastIndex), 0.00001); // [cite: 56]
        assertEquals("2025-06-29T15:54:47Z", data.timestamps.get(lastIndex)); // [cite: 56]
    }
}