package com.example.highpath;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.highpath.Services.OpenWeatherAPIService;
import com.example.highpath.Utilities.OpenWeather.FiveDayWeatherData;
import com.example.highpath.Utilities.OpenWeather.ParseOpenWeather;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class OpenWeatherApiTest {

    // Errigal mountain coordinates as test location
    private static final double TEST_LAT = 55.0343;
    private static final double TEST_LON = -8.1130;
    private static final double ERROR_VALUE = -99.9;

    @Test
    public void testApiReturnsValidResponse() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> jsonResult = new AtomicReference<>(null);
        AtomicReference<Exception> errorResult = new AtomicReference<>(null);

        OpenWeatherAPIService.getFiveDayForecast(context, TEST_LAT, TEST_LON,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        jsonResult.set(json);
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        errorResult.set(e);
                        latch.countDown();
                    }
                });

        // Wait up to 10 seconds for response
        latch.await(10, TimeUnit.SECONDS);

        assertNull("API should not return an error", errorResult.get());
        assertNotNull("API response should not be null", jsonResult.get());
        assertFalse("API response should not be empty", jsonResult.get().isEmpty());
    }

    @Test
    public void testParsedTemperaturesAreNotErrorValue() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<FiveDayWeatherData> dataResult = new AtomicReference<>(null);

        OpenWeatherAPIService.getFiveDayForecast(context, TEST_LAT, TEST_LON,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        dataResult.set(ParseOpenWeather.parse(json));
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        FiveDayWeatherData data = dataResult.get();
        assertNotNull("Parsed weather data should not be null", data);
        assertFalse("High temps should not be empty", data.HighTemps.isEmpty());
        assertFalse("Low temps should not be empty", data.LowTemps.isEmpty());

        // Print out the temperatures
        for (int i = 0; i < data.HighTemps.size(); i++) {
            android.util.Log.d("WeatherTest", "Day " + (i + 1) +
                    " → High: " + data.HighTemps.get(i) +
                    "°C  Low: " + data.LowTemps.get(i) +
                    "°C  Formatted: " + (i < data.HighLowTemps.size() ? data.HighLowTemps.get(i) : "N/A"));
        }

        for (Double temp : data.HighTemps) {
            assertNotEquals("High temp should not be error value", ERROR_VALUE, temp, 0.01);
        }
        for (Double temp : data.LowTemps) {
            assertNotEquals("Low temp should not be error value", ERROR_VALUE, temp, 0.01);
        }
    }

    @Test
    public void testFiveDayForecastReturnsFiveDays() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<FiveDayWeatherData> dataResult = new AtomicReference<>(null);

        OpenWeatherAPIService.getFiveDayForecast(context, TEST_LAT, TEST_LON,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        dataResult.set(ParseOpenWeather.parse(json));
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        FiveDayWeatherData data = dataResult.get();
        assertNotNull(data);
        assertEquals("Should have 5 days of high temps", 5, data.HighTemps.size());
        assertEquals("Should have 5 days of low temps", 5, data.LowTemps.size());
        assertEquals("Should have 5 formatted strings", 5, data.HighLowTemps.size());
    }

    @Test
    public void testHighTempIsAlwaysGreaterThanOrEqualToLowTemp() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<FiveDayWeatherData> dataResult = new AtomicReference<>(null);

        OpenWeatherAPIService.getFiveDayForecast(context, TEST_LAT, TEST_LON,
                new OpenWeatherAPIService.WeatherCallback() {
                    @Override
                    public void onSuccess(String json) {
                        dataResult.set(ParseOpenWeather.parse(json));
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        FiveDayWeatherData data = dataResult.get();
        assertNotNull(data);

        for (int i = 0; i < data.HighTemps.size(); i++) {
            assertTrue("High temp should be >= low temp for day " + i,
                    data.HighTemps.get(i) >= data.LowTemps.get(i));
        }
    }
}