// Fixed BOM issue
package dk.seahawk.locator.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import dk.seahawk.locator.R;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import dk.seahawk.locator.databinding.FragmentHelpBinding;

/**
 * Help Fragment displaying user manual
 */
public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;
    private GestureDetectorCompat gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Gesture Detector
        gestureDetector = new GestureDetectorCompat(requireContext(), new SwipeGestureListener());

        // Create a common touch listener
        View.OnTouchListener touchListener = (v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false; // Return false to allow vertical scrolling
        };

        // Attach touch listener to the root view and the scroll view
        binding.getRoot().setOnTouchListener(touchListener);
        binding.scrollViewHelp.setOnTouchListener(touchListener);

        // Load manual content from string resources
        binding.textOverview.setText(getString(R.string.manual_overview));
        binding.textFeatures.setText(getString(R.string.manual_features));
        binding.textPermissions.setText(getString(R.string.manual_permissions));
        binding.textHowToUse.setText(getString(R.string.manual_how_to_use));
        binding.textTips.setText(getString(R.string.manual_tips));

        // Setup back button
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(HelpFragment.this).navigateUp()
        );
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
                        // Swipe Right (Left-to-Right) -> Navigate back
                        NavHostFragment.findNavController(HelpFragment.this).navigateUp();
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
