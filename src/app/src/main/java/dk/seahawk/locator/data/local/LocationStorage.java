package dk.seahawk.locator.data.local;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dk.seahawk.locator.data.model.LocationRecord;

/**
 * Manages local storage of location records
 * Uses SharedPreferences for persistence
 */
public class LocationStorage {
    private static final String PREF_LOCATIONS = "saved_locations";
    private final PreferenceManager preferenceManager;

    public LocationStorage(Context context) {
        this.preferenceManager = new PreferenceManager(context);
    }

    /**
     * Save a location record
     */
    public void saveLocation(LocationRecord record) {
        // Get existing locations
        List<LocationRecord> locations = getLocations();

        // Add new location to the beginning
        locations.add(0, record);

        // Keep only last 100 locations
        if (locations.size() > 100) {
            locations = locations.subList(0, 100);
        }

        // Save to preferences
        preferenceManager.saveLocations(locations);
    }

    /**
     * Get all saved locations
     */
    public List<LocationRecord> getLocations() {
        return preferenceManager.getLocations();
    }

    /**
     * Delete a specific location by record object
     */
    public void deleteLocation(LocationRecord record) {
        List<LocationRecord> locations = getLocations();
        locations.remove(record);
        preferenceManager.saveLocations(locations);
    }

    /**
     * Delete a specific location by index
     */
    public void deleteLocation(int index) {
        List<LocationRecord> locations = getLocations();
        if (index >= 0 && index < locations.size()) {
            locations.remove(index);
            preferenceManager.saveLocations(locations);
        }
    }

    /**
     * Delete location by index (alias for deleteLocation(int))
     */
    public void deleteLocationByIndex(int index) {
        deleteLocation(index);
    }

    /**
     * Clear all locations
     */
    public void clearAll() {
        preferenceManager.saveLocations(new ArrayList<>());
    }

    /**
     * Get number of saved locations
     */
    public int getLocationCount() {
        return getLocations().size();
    }
}

