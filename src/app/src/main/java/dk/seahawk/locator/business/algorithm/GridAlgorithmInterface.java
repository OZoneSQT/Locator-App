package dk.seahawk.locator.business.algorithm;

import android.location.Location;

/**
 * Interface for grid location algorithms
 */
public interface GridAlgorithmInterface {
    /**
     * Convert GPS coordinates to grid location (e.g., grid square)
     * @param location GPS location
     * @return Grid location string
     */
    String getGridLocation(Location location);

    /**
     * Convert GPS coordinates to grid location
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @return Grid location string
     */
    String getGridLocation(double latitude, double longitude);

    /**
     * Convert mock location object to grid location (for testing)
     * @param mockLocation Mock location object with getLatitude() and getLongitude()
     * @return Grid location string
     */
    String getGridLocationTestMethod(Object mockLocation);
}

