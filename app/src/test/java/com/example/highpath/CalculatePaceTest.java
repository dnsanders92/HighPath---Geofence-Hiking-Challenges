package com.example.highpath;

import static org.junit.Assert.*;
import org.junit.Test;

import com.example.highpath.Calculations.CalculatePace;

public class CalculatePaceTest {

    @Test
    public void testKnownPaceCalculation() {
        // 7.23 km in 11520 seconds (3h 12m)
        // Expected: 11520 / 60 = 192 minutes / 7.23 km = 26.556... min/km
        double pace = CalculatePace.pace(11520, 7.23);
        assertEquals(26.56, pace, 0.05);
    }

    @Test
    public void testKnownPaceFormat() {
        // 26.556 min/km should format to "26:33"
        double pace = CalculatePace.pace(11520, 7.23);
        String formatted = CalculatePace.formatPaceToString(pace);
        assertEquals("26:33", formatted);
    }

    @Test
    public void testTypicalRunPace() {
        // 10 km in 3600 seconds (1 hour) = exactly 6:00 /km
        double pace = CalculatePace.pace(3600, 10.0);
        assertEquals(6.0, pace, 0.01);
        assertEquals("06:00", CalculatePace.formatPaceToString(pace));
    }

    @Test
    public void testHalfMarathonPace() {
        // 21.1 km in 7200 seconds (2 hours) = 5:41 /km approx
        double pace = CalculatePace.pace(7200, 21.1);
        assertEquals("05:41", CalculatePace.formatPaceToString(pace));
    }

    @Test
    public void testZeroDistanceReturnsZero() {
        double pace = CalculatePace.pace(3600, 0.0);
        assertEquals(0.0, pace, 0.0);
    }

    @Test
    public void testZeroTimeReturnsZero() {
        double pace = CalculatePace.pace(0, 10.0);
        assertEquals(0.0, pace, 0.0);
    }

    @Test
    public void testFormatZeroReturnsDefault() {
        assertEquals("00:00", CalculatePace.formatPaceToString(0.0));
    }

    @Test
    public void testFormatNegativeReturnsDefault() {
        assertEquals("00:00", CalculatePace.formatPaceToString(-1.0));
    }
}