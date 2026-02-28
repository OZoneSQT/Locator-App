package dk.seahawk.locator.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import dk.seahawk.locator.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import dk.seahawk.locator.business.algorithm.GridAlgorithm;
import dk.seahawk.locator.business.algorithm.GridAlgorithmInterface;
import dk.seahawk.locator.databinding.FragmentFirstBinding;
import dk.seahawk.locator.data.model.LocationRecord;
import dk.seahawk.locator.data.model.WeatherData;
import dk.seahawk.locator.data.local.PreferenceManager;
import dk.seahawk.locator.data.remote.WeatherService;
import dk.seahawk.locator.data.local.LocationStorage;
import dk.seahawk.locator.widget.LocationWidgetProvider;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private GridAlgorithmInterface gridAlgorithm;
    private LocationStorage locationStorage;
    private PreferenceManager preferenceManager;
    private WeatherService weatherService;
    private Location currentLocation;
    private WeatherData currentWeather;
    private DateTimeFormatter dateFormatter;
    private GestureDetectorCompat gestureDetector;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    fetchLocation();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    fetchLocation();
                } else {
                    Toast.makeText(requireContext(), R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
                    updateStatus(getString(R.string.status_permission_denied));
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        gridAlgorithm = new GridAlgorithm();
        locationStorage = new LocationStorage(requireContext());
        preferenceManager = new PreferenceManager(requireContext());
        weatherService = new WeatherService(requireContext());
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        // Initialize Gesture Detector
        gestureDetector = new GestureDetectorCompat(requireContext(), new SwipeGestureListener());

        // Set touch listener on root view to detect swipes
        binding.getRoot().setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true; // We consume the event
        });

        // Setup button listeners
        binding.buttonSaveLocation.setOnClickListener(v -> requestLocationAndSave());

        binding.buttonRefreshLocation.setOnClickListener(v -> refreshCurrentLocation());

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // Load and display the last saved location on app startup
        loadLastSavedLocation();
    }

    private void requestLocationAndSave() {
        updateStatus(getString(R.string.status_getting_location));

        if (!hasLocationPermissions()) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            fetchLocation();
        }
    }

    /**
     * Refresh the current location display (fetch but don't save)
     */
    private void refreshCurrentLocation() {
        updateStatus(getString(R.string.status_getting_location));

        if (!hasLocationPermissions()) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            fetchLocationForDisplay();
        }
    }

    /**
     * Fetch location for display only (without saving)
     */
    @SuppressWarnings("MissingPermission")
    private void fetchLocationForDisplay() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        displayLocation(location);
                        updateStatus(getString(R.string.status_refreshed));
                        // Update widget with refreshed location
                        LocationWidgetProvider.updateAllWidgets(requireContext());
                    } else {
                        // Try last known location as fallback
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(lastLocation -> {
                                    if (lastLocation != null) {
                                        displayLocation(lastLocation);
                                        updateStatus(getString(R.string.status_refreshed));
                                        // Update widget with last known location
                                        LocationWidgetProvider.updateAllWidgets(requireContext());
                                    } else {
                                        updateStatus(getString(R.string.status_unable));
                                        Toast.makeText(requireContext(), R.string.toast_unable_get_location, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    updateStatus(getString(R.string.status_unable));
                    Toast.makeText(requireContext(), R.string.toast_error_getting_location, Toast.LENGTH_SHORT).show();
                });
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("MissingPermission")
    private void fetchLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLocation = location;
                        displayLocation(location);
                        fetchWeather(location); // Fetch weather after getting location
                    } else {
                        // Try last known location as fallback
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(lastLocation -> {
                                    if (lastLocation != null) {
                                        currentLocation = lastLocation;
                                        displayLocation(lastLocation);
                                        fetchWeather(lastLocation);
                                    } else {
                                        updateStatus(getString(R.string.status_unable));
                                        Toast.makeText(requireContext(), R.string.toast_unable_get_location, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    updateStatus(getString(R.string.status_unable));
                    Toast.makeText(requireContext(), R.string.toast_error_getting_location, Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchWeather(Location location) {
        updateStatus(getString(R.string.weather_fetching));

        weatherService.fetchWeather(location.getLatitude(), location.getLongitude(),
            new WeatherService.WeatherCallback() {
                @Override
                public void onWeatherSuccess(WeatherData weather) {
                    currentWeather = weather;
                    saveLocation(location);
                    updateStatus(getString(R.string.status_saved));
                }

                @Override
                public void onWeatherError(String errorMessage) {
                    // Save location even if weather fails
                    currentWeather = null;
                    saveLocation(location);
                    updateStatus(getString(R.string.status_saved) + " (no weather)");
                }
            });
    }

    private void displayLocation(Location location) {
        // Calculate grid locator
        String gridLocator = gridAlgorithm.getGridLocation(location);
        binding.textGridLocator.setText(gridLocator);

        // Display altitude
        binding.textHeight.setText(String.format(Locale.US, "%.1f m", location.getAltitude()));

        // Display timestamps
        long timestamp = location.getTime();
        Instant instant = Instant.ofEpochMilli(timestamp);

        // Local time
        ZonedDateTime localTime = instant.atZone(ZoneId.systemDefault());
        binding.textLocalTime.setText(localTime.format(dateFormatter));

        // UTC time
        ZonedDateTime utcTime = instant.atZone(ZoneId.of("UTC"));
        binding.textUtcTime.setText(utcTime.format(dateFormatter));
    }

    private void saveLocation(Location location) {
        String gridLocator = gridAlgorithm.getGridLocation(location);
        long timestamp = location.getTime();
        String callSign = preferenceManager.getCallSign();

        LocationRecord record = new LocationRecord(
                gridLocator,
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                timestamp,
                callSign,
                currentWeather
        );

        locationStorage.saveLocation(record);

        // Update widget with new location
        LocationWidgetProvider.updateAllWidgets(requireContext());
    }

    private void updateStatus(String message) {
        binding.textStatus.setText(message);
    }

    /**
     * Load and display the last saved location
     */
    private void loadLastSavedLocation() {
        java.util.List<LocationRecord> locations = locationStorage.getLocations();

        if (!locations.isEmpty()) {
            LocationRecord lastLocation = locations.get(0); // Most recent is first
            displayLocationRecord(lastLocation);
            updateStatus(getString(R.string.last_location_loaded));
        } else {
            updateStatus(getString(R.string.status_initial));
        }
    }

    /**
     * Display a LocationRecord on the UI
     */
    private void displayLocationRecord(LocationRecord record) {
        binding.textGridLocator.setText(record.getGridLocator());
        binding.textHeight.setText(record.getFormattedAltitude());

        // Display timestamps
        Instant instant = Instant.ofEpochMilli(record.getTimestampMillis());

        // Local time
        ZonedDateTime localTime = instant.atZone(ZoneId.systemDefault());
        binding.textLocalTime.setText(localTime.format(dateFormatter));

        // UTC time
        ZonedDateTime utcTime = instant.atZone(ZoneId.of("UTC"));
        binding.textUtcTime.setText(utcTime.format(dateFormatter));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Inner class to handle swipe gestures
     */
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe Right (Left-to-Right) -> Refresh
                        refreshCurrentLocation();
                    } else {
                        // Swipe Left (Right-to-Left) -> History
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
