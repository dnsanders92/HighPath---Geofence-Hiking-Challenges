package com.example.highpath;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapboxTokenTest {

    @Test
    public void testMapboxTokenExists() {
        Context context = ApplicationProvider.getApplicationContext();

        String token = context.getString(R.string.mapbox_access_token);

        assertNotNull("Mapbox token should not be null", token);
        assertFalse("Mapbox token should not be empty", token.isEmpty());
    }

    @Test
    public void testMapboxTokenStartsWithCorrectPrefix() {
        Context context = ApplicationProvider.getApplicationContext();

        String token = context.getString(R.string.mapbox_access_token);

        // All valid Mapbox public tokens start with "pk."
        assertTrue("Mapbox token should start with pk.", token.startsWith("pk."));
    }

    @Test
    public void testMapboxTokenIsNotPlaceholder() {
        Context context = ApplicationProvider.getApplicationContext();

        String token = context.getString(R.string.mapbox_access_token);

        // Make sure it hasn't been left as a placeholder value
        assertFalse("Token should not be placeholder", token.equals("YOUR_MAPBOX_ACCESS_TOKEN"));
        assertFalse("Token should not be empty placeholder", token.equals("YOUR_ACCESS_TOKEN"));
    }

    @Test
    public void testMapboxTokenHasMinimumLength() {
        Context context = ApplicationProvider.getApplicationContext();

        String token = context.getString(R.string.mapbox_access_token);

        // Mapbox tokens are typically over 100 characters
        assertTrue("Mapbox token should have a valid length", token.length() > 50);
    }
}