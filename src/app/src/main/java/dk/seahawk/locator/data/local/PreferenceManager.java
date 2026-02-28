package dk.seahawk.locator.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dk.seahawk.locator.data.model.LocationRecord;
import dk.seahawk.locator.util.MyExceptionHandler;

/**
 * Manages application preferences using SharedPreferences
 * Handles storage of user settings and location history
 */
public class PreferenceManager {
    private static final String PREF_CALL_SIGN = "call_sign";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_TEMPERATURE_UNIT = "temperature_unit";
    private static final String PREF_LOCATIONS = "saved_locations_json";
    private static final String PREF_FIRST_RUN = "is_first_run";
    private static final String PREF_LAST_GRID = "last_grid_locator";

    public enum Language {
        ENGLISH_US("en-US", "English (US)"),
        ENGLISH_GB("en-GB", "English (GB)"),
        DANISH("da", "Dansk"),
        GERMAN("de", "Deutsch"),
        SPANISH("es", "Español"),
        PORTUGUESE("pt", "Português"),
        FRENCH("fr", "Français"),
        RUSSIAN("ru", "Русский"),
        ARABIC("ar", "العربية"),
        CHINESE("zh", "中文");

        private final String code;
        private final String displayName;

        Language(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TemperatureUnit {
        CELSIUS("°C"),
        FAHRENHEIT("°F");

        private final String symbol;

        TemperatureUnit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public PreferenceManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("locator_prefs", Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Check if it is the first time the app is run
     */
    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(PREF_FIRST_RUN, true);
    }

    /**
     * Mark that the app has been run at least once
     */
    public void setFirstRunComplete() {
        sharedPreferences.edit().putBoolean(PREF_FIRST_RUN, false).apply();
    }

    /**
     * Save user call sign
     */
    public void saveCallSign(String callSign) {
        sharedPreferences.edit().putString(PREF_CALL_SIGN, callSign).apply();
    }

    /**
     * Set user call sign (alias for saveCallSign)
     */
    public void setCallSign(String callSign) {
        saveCallSign(callSign);
    }

    /**
     * Get saved call sign
     */
    public String getCallSign() {
        return sharedPreferences.getString(PREF_CALL_SIGN, "");
    }

    /**
     * Save language preference
     */
    public void saveLanguage(Language language) {
        sharedPreferences.edit().putString(PREF_LANGUAGE, language.name()).apply();
    }

    /**
     * Set language preference (alias for saveLanguage)
     */
    public void setLanguage(Language language) {
        saveLanguage(language);
    }

    /**
     * Get language preference
     */
    public Language getLanguage() {
        String languageStr = sharedPreferences.getString(PREF_LANGUAGE, Language.ENGLISH_US.name());
        try {
            return Language.valueOf(languageStr);
        } catch (IllegalArgumentException e) {
            return Language.ENGLISH_US;
        }
    }

    /**
     * Save temperature unit preference
     */
    public void saveTemperatureUnit(TemperatureUnit unit) {
        sharedPreferences.edit().putString(PREF_TEMPERATURE_UNIT, unit.name()).apply();
    }

    /**
     * Set temperature unit preference (alias for saveTemperatureUnit)
     */
    public void setTemperatureUnit(TemperatureUnit unit) {
        saveTemperatureUnit(unit);
    }

    /**
     * Get temperature unit preference
     */
    public TemperatureUnit getTemperatureUnit() {
        String unitStr = sharedPreferences.getString(PREF_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name());
        try {
            return TemperatureUnit.valueOf(unitStr);
        } catch (IllegalArgumentException e) {
            return TemperatureUnit.CELSIUS;
        }
    }

    /**
     * Save location history
     */
    public void saveLocations(List<LocationRecord> locations) {
        try {
            String json = gson.toJson(locations);
            sharedPreferences.edit().putString(PREF_LOCATIONS, json).apply();
        } catch (Exception e) {
            MyExceptionHandler.handleException(e);
        }
    }

    /**
     * Get location history
     */
    public List<LocationRecord> getLocations() {
        try {
            String json = sharedPreferences.getString(PREF_LOCATIONS, "[]");
            Type listType = new TypeToken<List<LocationRecord>>() {}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            MyExceptionHandler.handleException(e);
            return new ArrayList<>();
        }
    }

    /**
     * Save last known grid locator for widget persistence
     */
    public void setLastGrid(String grid) {
        sharedPreferences.edit().putString(PREF_LAST_GRID, grid).apply();
    }

    /**
     * Get last known grid locator
     */
    public String getLastGrid() {
        return sharedPreferences.getString(PREF_LAST_GRID, "--");
    }

    /**
     * Clear all preferences
     */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}
