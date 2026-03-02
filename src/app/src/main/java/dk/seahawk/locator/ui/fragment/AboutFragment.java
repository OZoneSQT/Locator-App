
package dk.seahawk.locator.ui.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import dk.seahawk.locator.R;
import dk.seahawk.locator.databinding.FragmentAboutBinding;

/**
 * About Fragment displaying app information and credits
 */
public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private GestureDetectorCompat gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
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
        binding.scrollViewAbout.setOnTouchListener(touchListener);

        // Fetch version name from BuildConfig or PackageManager
        String versionName = "1.0";
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Load about content from string resources with dynamic version
        binding.textVersion.setText(getString(R.string.about_version, versionName));

        binding.textDeveloper.setText(getString(R.string.about_developer));
        binding.textDescription.setText(getString(R.string.about_description));
        binding.textCopyright.setText(getString(R.string.about_copyright));
        binding.textFeaturesList.setText(getString(R.string.about_features_list));
        binding.textPermissionsList.setText(getString(R.string.about_permissions_list));
        binding.textSupport.setText(getString(R.string.about_support));

        // Setup back button
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(AboutFragment.this).navigateUp()
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
                        NavHostFragment.findNavController(AboutFragment.this).navigateUp();
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
