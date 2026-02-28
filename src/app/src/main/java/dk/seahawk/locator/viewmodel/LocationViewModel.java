// Fixed BOM issue
package dk.seahawk.locator.viewmodel;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import dk.seahawk.locator.business.algorithm.GridAlgorithm;
import dk.seahawk.locator.business.algorithm.GridAlgorithmInterface;
import dk.seahawk.locator.data.model.LocationRecord;
import dk.seahawk.locator.data.model.WeatherData;
import dk.seahawk.locator.data.local.PreferenceManager;
import dk.seahawk.locator.data.remote.WeatherService;
import dk.seahawk.locator.data.local.LocationStorage;

/**
 * ViewModel for managing location-related data and business logic
 * Handles location fetching, grid calculation, and weather data
 */
public class LocationViewModel extends AndroidViewModel {

    private final LocationStorage locationStorage;
    private final PreferenceManager preferenceManager;
    private final WeatherService weatherService;
    private final GridAlgorithmInterface gridAlgorithm;
    private final DateTimeFormatter dateFormatter;

    // LiveData for UI observation
    private final MutableLiveData<String> gridLocatorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> localTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> utcTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> heightLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    private final MutableLiveData<WeatherData> weatherDataLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<LocationRecord>> locationHistoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private Location currentLocation;
    private LocationRecord lastLocationRecord;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        this.locationStorage = new LocationStorage(application);
        this.preferenceManager = new PreferenceManager(application);
        this.weatherService = new WeatherService(application);
        this.gridAlgorithm = new GridAlgorithm();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    }

    // ============ Public Methods ============

    /**
     * Process location update and update all related UI data
     */
    public void onLocationReceived(Location location, WeatherData weather) {
        this.currentLocation = location;
        updateLocationData(location, weather);
    }

    /**
     * Save current location to storage
     */
    public void saveCurrentLocation() {
        if (currentLocation == null) {
            errorMessageLiveData.postValue("Location not available");
            return;
        }

        setLoading(true);
        try {
            String gridLocator = gridLocatorLiveData.getValue();
            String callSign = preferenceManager.getCallSign();
            WeatherData weather = weatherDataLiveData.getValue();

            LocationRecord record = new LocationRecord(
                    gridLocator,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    currentLocation.getAltitude(),
                    System.currentTimeMillis(),
                    callSign,
                    weather
            );

            locationStorage.saveLocation(record);
            lastLocationRecord = record;
            loadLocationHistory();
            statusLiveData.postValue("Location saved successfully");
        } catch (Exception e) {
            errorMessageLiveData.postValue("Failed to save location: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Load location history from storage
     */
    public void loadLocationHistory() {
        setLoading(true);
        try {
            List<LocationRecord> locations = locationStorage.getLocations();
            locationHistoryLiveData.postValue(locations);

            if (locations.isEmpty()) {
                statusLiveData.postValue("No saved locations");
            }
        } catch (Exception e) {
            errorMessageLiveData.postValue("Failed to load history: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Delete a location record
     */
    public void deleteLocation(LocationRecord record) {
        setLoading(true);
        try {
            locationStorage.deleteLocation(record);
            loadLocationHistory();
            statusLiveData.postValue("Location deleted");
        } catch (Exception e) {
            errorMessageLiveData.postValue("Failed to delete location: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Load last saved location for display
     */
    public void loadLastSavedLocation() {
        setLoading(true);
        try {
            List<LocationRecord> locations = locationStorage.getLocations();
            if (!locations.isEmpty()) {
                lastLocationRecord = locations.get(0);
                displayLocationRecord(lastLocationRecord);
                statusLiveData.postValue("Last location loaded");
            } else {
                statusLiveData.postValue("No saved locations yet");
            }
        } catch (Exception e) {
            errorMessageLiveData.postValue("Failed to load last location: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    // ============ Private Helper Methods ============

    private void updateLocationData(Location location, WeatherData weather) {
        if (location == null) return;

        try {
            // Update grid locator
            String gridLocator = gridAlgorithm.getGridLocation(location).toUpperCase(Locale.US);
            gridLocatorLiveData.postValue(gridLocator);

            // Update timestamps
            Instant instant = Instant.ofEpochMilli(location.getTime());
            ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
            ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));

            localTimeLiveData.postValue(dateFormatter.format(localDateTime));
            utcTimeLiveData.postValue(dateFormatter.format(utcDateTime));

            // Update height
            String height = String.format(Locale.US, "%.1f m", location.getAltitude());
            heightLiveData.postValue(height);

            // Update weather
            if (weather != null) {
                weatherDataLiveData.postValue(weather);
            }

            currentLocationLiveData.postValue(location);
            statusLiveData.postValue("Location updated");
        } catch (Exception e) {
            errorMessageLiveData.postValue("Error processing location: " + e.getMessage());
        }
    }

    private void displayLocationRecord(LocationRecord record) {
        gridLocatorLiveData.postValue(record.getGridLocator());

        Instant instant = Instant.ofEpochMilli(record.getTimestampMillis());
        ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));

        localTimeLiveData.postValue(dateFormatter.format(localDateTime));
        utcTimeLiveData.postValue(dateFormatter.format(utcDateTime));

        String height = String.format(Locale.US, "%.1f m", record.getAltitude());
        heightLiveData.postValue(height);

        if (record.getWeatherData() != null) {
            weatherDataLiveData.postValue(record.getWeatherData());
        }
    }

    private void setLoading(boolean loading) {
        isLoadingLiveData.postValue(loading);
    }

    // ============ LiveData Getters ============

    public MutableLiveData<String> getGridLocatorLiveData() {
        return gridLocatorLiveData;
    }

    public MutableLiveData<String> getLocalTimeLiveData() {
        return localTimeLiveData;
    }

    public MutableLiveData<String> getUtcTimeLiveData() {
        return utcTimeLiveData;
    }

    public MutableLiveData<String> getHeightLiveData() {
        return heightLiveData;
    }

    public MutableLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public MutableLiveData<Location> getCurrentLocationLiveData() {
        return currentLocationLiveData;
    }

    public MutableLiveData<WeatherData> getWeatherDataLiveData() {
        return weatherDataLiveData;
    }

    public MutableLiveData<List<LocationRecord>> getLocationHistoryLiveData() {
        return locationHistoryLiveData;
    }

    public MutableLiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    // ============ Non-LiveData Getters ============

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LocationRecord getLastLocationRecord() {
        return lastLocationRecord;
    }

    public String getCallSign() {
        return preferenceManager.getCallSign();
    }

    public PreferenceManager.TemperatureUnit getTemperatureUnit() {
        return preferenceManager.getTemperatureUnit();
    }
}
