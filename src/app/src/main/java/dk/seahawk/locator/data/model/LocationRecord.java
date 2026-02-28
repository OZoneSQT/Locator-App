package dk.seahawk.locator.data.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a saved location record with all relevant data
 */
public class LocationRecord {
    private String gridLocator;
    private double latitude;
    private double longitude;
    private double altitude;
    private long timestampMillis;
    private String callSign;
    private WeatherData weatherData;

    public LocationRecord(String gridLocator, double latitude, double longitude, double altitude, long timestampMillis) {
        this(gridLocator, latitude, longitude, altitude, timestampMillis, "", null);
    }

    public LocationRecord(String gridLocator, double latitude, double longitude, double altitude, long timestampMillis, String callSign) {
        this(gridLocator, latitude, longitude, altitude, timestampMillis, callSign, null);
    }

    public LocationRecord(String gridLocator, double latitude, double longitude, double altitude, long timestampMillis, String callSign, WeatherData weatherData) {
        this.gridLocator = gridLocator;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestampMillis = timestampMillis;
        this.callSign = callSign != null ? callSign : "";
        this.weatherData = weatherData;
    }

    public String getGridLocator() {
        return gridLocator;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getFormattedTimestampUTC() {
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        ZonedDateTime utcTime = instant.atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return utcTime.format(formatter);
    }

    public String getFormattedTimestampLocal() {
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        ZonedDateTime localTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return localTime.format(formatter);
    }

    public String getFormattedCoordinates() {
        // Ensuring space between coordinates and dots for decimals
        return String.format(Locale.US, "%.6f,  %.6f", latitude, longitude);
    }

    public String getFormattedAltitude() {
        return String.format(Locale.US, "%.1f m", altitude);
    }

    public String getCallSign() {
        return callSign;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public boolean hasWeatherData() {
        return weatherData != null;
    }
}
