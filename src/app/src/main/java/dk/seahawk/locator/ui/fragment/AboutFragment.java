
package dk.seahawk.locator.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import dk.seahawk.locator.R;
import dk.seahawk.locator.databinding.FragmentAboutBinding;

/**
 * About Fragment displaying app information and credits
 */
public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
}
