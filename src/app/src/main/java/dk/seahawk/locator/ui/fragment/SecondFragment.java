// Fixed BOM issue
package dk.seahawk.locator.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import dk.seahawk.locator.R;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Locale;

import dk.seahawk.locator.ui.adapter.LocationHistoryAdapter;
import dk.seahawk.locator.databinding.FragmentSecondBinding;
import dk.seahawk.locator.data.model.LocationRecord;
import dk.seahawk.locator.data.local.LocationStorage;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private LocationHistoryAdapter adapter;
    private LocationStorage locationStorage;
    private GestureDetectorCompat gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize storage
        locationStorage = new LocationStorage(requireContext());

        // Initialize Gesture Detector
        gestureDetector = new GestureDetectorCompat(requireContext(), new SwipeGestureListener());

        // Attach touch listener to the container wrapping everything
        // Note: Using the first child of CoordinatorLayout to ensure full coverage
        View rootContent = binding.getRoot().getChildAt(0);
        if (rootContent != null) {
            rootContent.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return false; 
            });
        }

        // Also explicitly set on RecyclerView
        binding.recyclerViewHistory.addOnItemTouchListener(new androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull androidx.recyclerview.widget.RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });

        // Setup RecyclerView
        adapter = new LocationHistoryAdapter();
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewHistory.setAdapter(adapter);

        // Load location history
        loadLocationHistory();

        // Setup button listeners
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        binding.buttonExport.setOnClickListener(v -> exportToGoogleDrive());
        
        // Setup adapters extra listeners
        adapter.setOnDeleteClickListener((position, record) -> showDeleteConfirmation(position, record));
        adapter.setOnOpenMapsClickListener(this::openInGoogleMaps);
    }

    private void loadLocationHistory() {
        List<LocationRecord> locations = locationStorage.getLocations();

        if (locations.isEmpty()) {
            // Show empty state
            binding.recyclerViewHistory.setVisibility(View.GONE);
            binding.emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            // Show list
            binding.recyclerViewHistory.setVisibility(View.VISIBLE);
            binding.emptyStateContainer.setVisibility(View.GONE);
            adapter.setLocations(locations);
        }
    }

    /**
     * Show confirmation dialog before deleting a location
     */
    private void showDeleteConfirmation(int position, LocationRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                    deleteLocation(position);
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    /**
     * Delete a location and refresh the list
     */
    private void deleteLocation(int position) {
        locationStorage.deleteLocation(position);
        loadLocationHistory();
        Toast.makeText(requireContext(), R.string.location_deleted, Toast.LENGTH_SHORT).show();
    }

    /**
     * Open location in Google Maps
     */
    private void openInGoogleMaps(LocationRecord record) {
        String uriString = String.format(Locale.US, "geo:0,0?q=%f,%f(%s)",
                record.getLatitude(),
                record.getLongitude(),
                record.getGridLocator());
        
        Uri gmmIntentUri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            String browserUriString = String.format(Locale.US, "https://www.google.com/maps/search/?api=1&query=%f,%f",
                    record.getLatitude(),
                    record.getLongitude());
            Uri browserUri = Uri.parse(browserUriString);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }

    /**
     * Export location history to Google Drive
     */
    private void exportToGoogleDrive() {
        List<LocationRecord> locations = locationStorage.getLocations();

        if (locations.isEmpty()) {
            Toast.makeText(requireContext(), R.string.export_no_locations, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.export_title)
                .setMessage(R.string.export_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Toast.makeText(requireContext(), R.string.export_in_progress, Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), "Export requires Google Drive integration", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocationHistory();
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
                        // Swipe Right (Left-to-Right) -> Navigate back to main view
                        NavHostFragment.findNavController(SecondFragment.this)
                                .navigate(R.id.action_SecondFragment_to_FirstFragment);
                        return true;
                    }
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
