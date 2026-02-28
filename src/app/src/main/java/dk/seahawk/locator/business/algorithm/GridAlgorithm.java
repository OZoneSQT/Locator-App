package dk.seahawk.locator.business.algorithm;

import android.location.Location;

import java.util.Locale;

/**
 * Grid algorithm implementation for Maidenhead Locator System (MLS)
 * Converts GPS coordinates to grid squares used in amateur radio
 */
public class GridAlgorithm implements GridAlgorithmInterface {

    /**
     * Convert GPS location to grid square
     */
    @Override
    public String getGridLocation(Location location) {
        return getGridLocation(location.getLatitude(), location.getLongitude());
    }

    /**
     * Convert GPS coordinates to Maidenhead grid square
     * Format: AA00aa00 (e.g., JO34KA12)
     */
    @Override
    public String getGridLocation(double latitude, double longitude) {
        try {
            // Normalize coordinates
            double lat = latitude + 90.0;
            double lon = longitude + 180.0;

            // Calculate grid squares (2 characters)
            int field1Lon = (int) (lon / 20.0);
            int field1Lat = (int) (lat / 10.0);

            double lon2 = lon - (field1Lon * 20.0);
            double lat2 = lat - (field1Lat * 10.0);

            int square1Lon = (int) (lon2 / 2.0);
            int square1Lat = (int) (lat2 / 1.0);

            double lon3 = lon2 - (square1Lon * 2.0);
            double lat3 = lat2 - (square1Lat * 1.0);

            int subsquare1Lon = (int) (lon3 * 12.0);
            int subsquare1Lat = (int) (lat3 * 24.0);

            // Build grid square string
            StringBuilder gridSquare = new StringBuilder();

            // Field (2 letters)
            gridSquare.append((char) ('A' + field1Lon));
            gridSquare.append((char) ('A' + field1Lat));

            // Square (2 digits)
            gridSquare.append(square1Lon);
            gridSquare.append(square1Lat);

            // Subsquare (2 letters)
            gridSquare.append((char) ('A' + subsquare1Lon));
            gridSquare.append((char) ('A' + subsquare1Lat));

            return gridSquare.toString().toUpperCase(Locale.US);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get the precision level of a grid square
     */
    public int getPrecision(String gridSquare) {
        if (gridSquare == null) return 0;
        return gridSquare.length();
    }

    /**
     * Validate grid square format
     */
    public boolean isValidGridSquare(String gridSquare) {
        if (gridSquare == null || gridSquare.length() < 4) {
            return false;
        }

        gridSquare = gridSquare.toUpperCase();

        // Check length is even and between 4 and 8
        if (gridSquare.length() % 2 != 0 || gridSquare.length() < 4 || gridSquare.length() > 8) {
            return false;
        }

        // Check format: letters, digits, letters pattern
        for (int i = 0; i < gridSquare.length(); i++) {
            char c = gridSquare.charAt(i);
            if (i < 2 || i >= 4 && i < 6) {
                // Should be letters
                if (!Character.isLetter(c)) return false;
            } else if (i >= 2 && i < 4 || i >= 6) {
                // Should be digits or letters
                if (i >= 2 && i < 4) {
                    if (!Character.isDigit(c)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Get grid location from mock location object (for testing)
     * Uses reflection to support any object with getLatitude() and getLongitude() methods
     */
    @Override
    public String getGridLocationTestMethod(Object mockLocation) {
        try {
            if (mockLocation == null) return "Unknown";

            // Use reflection to call getLatitude() and getLongitude()
            java.lang.reflect.Method getLatMethod = mockLocation.getClass().getMethod("getLatitude");
            java.lang.reflect.Method getLonMethod = mockLocation.getClass().getMethod("getLongitude");

            double latitude = (double) getLatMethod.invoke(mockLocation);
            double longitude = (double) getLonMethod.invoke(mockLocation);

            return getGridLocation(latitude, longitude);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}

