// Fixed BOM issue
package dk.seahawk.locator.ui.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import dk.seahawk.locator.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import dk.seahawk.locator.databinding.FragmentSettingsBinding;
import dk.seahawk.locator.data.local.PreferenceManager;

/**
 * Settings Fragment for managing language preference and user call sign
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize preferences
        preferenceManager = new PreferenceManager(requireContext());

        // Check for temperature sensor presence
        checkTemperatureSensor();

        // Setup language spinner
        setupLanguageSpinner();

        // Setup temperature unit spinner
        setupTemperatureUnitSpinner();

        // Load saved call sign
        String savedCallSign = preferenceManager.getCallSign();
        binding.inputCallSign.setText(savedCallSign);

        // Setup done button
        binding.buttonDone.setOnClickListener(v -> saveSettings());

        // Setup help button
        binding.buttonHelp.setOnClickListener(v ->
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_SettingsFragment_to_HelpFragment)
        );

        // Setup about button
        binding.buttonAbout.setOnClickListener(v ->
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_SettingsFragment_to_AboutFragment)
        );
    }

    /**
     * Hides temperature settings if hardware sensor is not available
     */
    private void checkTemperatureSensor() {
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            // Fallback check for deprecated TYPE_TEMPERATURE if needed, though most modern devices use AMBIENT
            if (tempSensor == null) {
                tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
            }

            if (tempSensor == null) {
                binding.labelTemperatureUnit.setVisibility(View.GONE);
                binding.layoutTemperatureUnit.setVisibility(View.GONE);
            }
        }
    }

    private String[] getLanguageLabels() {
        return new String[] {
                getString(R.string.language_english_us),
                getString(R.string.language_english_gb),
                getString(R.string.language_danish),
                getString(R.string.language_german),
                getString(R.string.language_spanish),
                getString(R.string.language_portuguese),
                getString(R.string.language_french),
                getString(R.string.language_russian),
                getString(R.string.language_arabic),
                getString(R.string.language_chinese)
        };
    }

    private void setupLanguageSpinner() {
        String[] languageLabels = getLanguageLabels();

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                languageLabels
        );
        binding.spinnerLanguage.setAdapter(adapter);

        // Set current language selection
        PreferenceManager.Language currentLanguage = preferenceManager.getLanguage();
        int index = currentLanguage.ordinal();
        if (index >= 0 && index < languageLabels.length) {
            binding.spinnerLanguage.setText(languageLabels[index], false);
        }
    }

    private String[] getTemperatureUnitLabels() {
        return new String[] {
                getString(R.string.temp_unit_celsius),
                getString(R.string.temp_unit_fahrenheit)
        };
    }

    private void setupTemperatureUnitSpinner() {
        String[] tempUnitLabels = getTemperatureUnitLabels();

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                tempUnitLabels
        );
        binding.spinnerTemperatureUnit.setAdapter(adapter);

        // Set current temperature unit selection
        PreferenceManager.TemperatureUnit currentUnit = preferenceManager.getTemperatureUnit();
        int index = currentUnit.ordinal();
        if (index >= 0 && index < tempUnitLabels.length) {
            binding.spinnerTemperatureUnit.setText(tempUnitLabels[index], false);
        }
    }

    private void saveSettings() {
        boolean languageChanged = false;

        // Save language selection
        String languageText = binding.spinnerLanguage.getText().toString();
        String[] languageLabels = getLanguageLabels();
        PreferenceManager.Language[] languages = PreferenceManager.Language.values();
        for (int i = 0; i < languageLabels.length; i++) {
            if (languageLabels[i].equals(languageText)) {
                PreferenceManager.Language selectedLang = languages[i];
                if (preferenceManager.getLanguage() != selectedLang) {
                    preferenceManager.setLanguage(selectedLang);
                    languageChanged = true;
                }
                break;
            }
        }

        // Save temperature unit selection (only if visible)
        if (binding.layoutTemperatureUnit.getVisibility() == View.VISIBLE) {
            String tempUnitText = binding.spinnerTemperatureUnit.getText().toString();
            String[] tempUnitLabels = getTemperatureUnitLabels();
            PreferenceManager.TemperatureUnit[] tempUnits = PreferenceManager.TemperatureUnit.values();
            for (int i = 0; i < tempUnitLabels.length; i++) {
                if (tempUnitLabels[i].equals(tempUnitText)) {
                    preferenceManager.setTemperatureUnit(tempUnits[i]);
                    break;
                }
            }
        }
        
        // Save call sign
        CharSequence callSignText = binding.inputCallSign.getText();
        String callSign = callSignText != null ? callSignText.toString().trim() : "";
        if (!callSign.isEmpty()) {
            preferenceManager.setCallSign(callSign);
        }

        // Mark first run as complete
        preferenceManager.setFirstRunComplete();

        if (languageChanged) {
            Toast.makeText(requireContext(), R.string.settings_call_sign_saved, Toast.LENGTH_SHORT).show();
            // Recreate activity to apply language change immediately
            if (getActivity() != null) {
                getActivity().recreate();
            }
        } else {
            Toast.makeText(requireContext(), R.string.settings_call_sign_saved, Toast.LENGTH_SHORT).show();
            // Close settings and return
            NavHostFragment.findNavController(SettingsFragment.this).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
