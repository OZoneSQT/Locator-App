package dk.seahawk.locator.data.model;

/**
 * Data class for saving location objects to storage
 */
public class SaveObject {
    private String gridLocator;
    private double latitude;
    private double longitude;
    private double altitude;
    private long timestamp;
    private String callSign;
    private String weatherJson;

    public SaveObject() {}

    public SaveObject(String gridLocator, double latitude, double longitude, double altitude, long timestamp, String callSign, String weatherJson) {
        this.gridLocator = gridLocator;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.callSign = callSign;
        this.weatherJson = weatherJson;
    }

    public String getGridLocator() {
        return gridLocator;
    }

    public void setGridLocator(String gridLocator) {
        this.gridLocator = gridLocator;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherJson) {
        this.weatherJson = weatherJson;
    }
}

