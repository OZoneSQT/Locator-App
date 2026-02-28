// Fixed BOM issue
package dk.seahawk.locator.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.seahawk.locator.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import dk.seahawk.locator.databinding.FragmentHelpBinding;

/**
 * Help Fragment displaying user manual
 */
public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
}
