// Fixed BOM issue
package dk.seahawk.locator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import dk.seahawk.locator.data.local.PreferenceManager;

/**
 * ViewModel for managing settings-related data
 * Handles language, temperature unit, and call sign preferences
 */
public class SettingsViewModel extends AndroidViewModel {

    private final PreferenceManager preferenceManager;

    // LiveData for UI observation
    private final MutableLiveData<String> callSignLiveData = new MutableLiveData<>();
    private final MutableLiveData<PreferenceManager.Language> languageLiveData = new MutableLiveData<>();
    private final MutableLiveData<PreferenceManager.TemperatureUnit> temperatureUnitLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.preferenceManager = new PreferenceManager(application);
        loadSettings();
    }

    // ============ Public Methods ============

    /**
     * Load current settings from preferences
     */
    public void loadSettings() {
        callSignLiveData.setValue(preferenceManager.getCallSign());
        languageLiveData.setValue(preferenceManager.getLanguage());
        temperatureUnitLiveData.setValue(preferenceManager.getTemperatureUnit());
    }

    /**
     * Save all settings
     */
    public void saveSettings(String callSign, PreferenceManager.Language language,
                            PreferenceManager.TemperatureUnit temperatureUnit) {
        try {
            if (callSign != null && !callSign.isEmpty()) {
                preferenceManager.setCallSign(callSign);
            }

            if (language != null) {
                preferenceManager.setLanguage(language);
            }

            if (temperatureUnit != null) {
                preferenceManager.setTemperatureUnit(temperatureUnit);
            }

            loadSettings();
            statusLiveData.setValue("Settings saved successfully");
        } catch (Exception e) {
            errorMessageLiveData.setValue("Failed to save settings: " + e.getMessage());
        }
    }

    /**
     * Update call sign
     */
    public void setCallSign(String callSign) {
        try {
            preferenceManager.setCallSign(callSign);
            callSignLiveData.setValue(callSign);
        } catch (Exception e) {
            errorMessageLiveData.setValue("Failed to update call sign: " + e.getMessage());
        }
    }

    /**
     * Update language
     */
    public void setLanguage(PreferenceManager.Language language) {
        try {
            preferenceManager.setLanguage(language);
            languageLiveData.setValue(language);
        } catch (Exception e) {
            errorMessageLiveData.setValue("Failed to update language: " + e.getMessage());
        }
    }

    /**
     * Update temperature unit
     */
    public void setTemperatureUnit(PreferenceManager.TemperatureUnit unit) {
        try {
            preferenceManager.setTemperatureUnit(unit);
            temperatureUnitLiveData.setValue(unit);
        } catch (Exception e) {
            errorMessageLiveData.setValue("Failed to update temperature unit: " + e.getMessage());
        }
    }

    // ============ LiveData Getters ============

    public MutableLiveData<String> getCallSignLiveData() {
        return callSignLiveData;
    }

    public MutableLiveData<PreferenceManager.Language> getLanguageLiveData() {
        return languageLiveData;
    }

    public MutableLiveData<PreferenceManager.TemperatureUnit> getTemperatureUnitLiveData() {
        return temperatureUnitLiveData;
    }

    public MutableLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    // ============ Non-LiveData Getters ============

    public String getCurrentCallSign() {
        String value = callSignLiveData.getValue();
        return value != null ? value : "";
    }

    public PreferenceManager.Language getCurrentLanguage() {
        PreferenceManager.Language value = languageLiveData.getValue();
        return value != null ? value : PreferenceManager.Language.ENGLISH_US;
    }

    public PreferenceManager.TemperatureUnit getCurrentTemperatureUnit() {
        PreferenceManager.TemperatureUnit value = temperatureUnitLiveData.getValue();
        return value != null ? value : PreferenceManager.TemperatureUnit.CELSIUS;
    }
}
