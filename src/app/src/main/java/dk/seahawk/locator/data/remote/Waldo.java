package dk.seahawk.locator.data.remote;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Location provider utility (formerly known as "waldo")
 * Provides methods to retrieve current device location from Android
 */
public class Waldo implements LocationListener {
    private final LocationManager locationManager;
    private final Context context;
    private Location currentLocation;
    private LocationCallback callback;

    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
    }

    public Waldo(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Get current location with callback
     */
    public void getCurrentLocation(LocationCallback callback) {
        this.callback = callback;

        try {
            // Try to get last known location from GPS
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                currentLocation = gpsLocation;
                if (callback != null) {
                    callback.onLocationReceived(gpsLocation);
                }
                return;
            }

            // Try network provider as fallback
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (networkLocation != null) {
                currentLocation = networkLocation;
                if (callback != null) {
                    callback.onLocationReceived(networkLocation);
                }
                return;
            }

            if (callback != null) {
                callback.onLocationError("Location not available");
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onLocationError("Permission denied: " + e.getMessage());
            }
        }
    }

    /**
     * Get current cached location
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Check if location services are enabled
     */
    public boolean isLocationEnabled() {
        return locationManager.isLocationEnabled();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        if (callback != null) {
            callback.onLocationReceived(location);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // Location provider enabled
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Location provider disabled
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Status changed
    }

    /**
     * Stop listening for location updates
     */
    public void stopListening() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            // Handle exception
        }
    }
}

