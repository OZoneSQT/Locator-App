package muckup;

/**
 * Mock up class for testing
 */
public class MuckUpLocation {
    private final double longitude, latitude, altitude;
    private final float accuracy;

    public MuckUpLocation(double longitude, double latitude, double altitude, float accuracy) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

}
