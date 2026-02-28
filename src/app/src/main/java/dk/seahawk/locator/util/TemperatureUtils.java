package dk.seahawk.locator.util;

/**
 * Utility class for temperature conversion and validation
 */
public class TemperatureUtils {

    // Realistic temperature ranges in Celsius
    private static final double MIN_CELSIUS = -89.0;  // Coldest recorded on Earth (Vostok, Antarctica)
    private static final double MAX_CELSIUS = 60.0;   // Hottest recorded on Earth (Death Valley)

    // Realistic temperature ranges in Fahrenheit
    private static final double MIN_FAHRENHEIT = -128.0;  // ~-89°C
    private static final double MAX_FAHRENHEIT = 140.0;   // ~60°C

    // Default fallback temperatures
    private static final double DEFAULT_CELSIUS = 20.0;
    private static final double DEFAULT_FAHRENHEIT = 68.0;

    /**
     * Convert Celsius to Fahrenheit
     */
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    /**
     * Convert Fahrenheit to Celsius
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    /**
     * Validate temperature in Celsius and return sanitized value
     */
    public static double validateCelsius(double temperature) {
        if (Double.isNaN(temperature) || Double.isInfinite(temperature)) {
            return DEFAULT_CELSIUS;
        }

        if (temperature < MIN_CELSIUS || temperature > MAX_CELSIUS) {
            // Value is unrealistic, return default
            return DEFAULT_CELSIUS;
        }

        return temperature;
    }

    /**
     * Validate temperature in Fahrenheit and return sanitized value
     */
    public static double validateFahrenheit(double temperature) {
        if (Double.isNaN(temperature) || Double.isInfinite(temperature)) {
            return DEFAULT_FAHRENHEIT;
        }

        if (temperature < MIN_FAHRENHEIT || temperature > MAX_FAHRENHEIT) {
            // Value is unrealistic, return default
            return DEFAULT_FAHRENHEIT;
        }

        return temperature;
    }

    /**
     * Check if temperature is realistic in Celsius
     */
    public static boolean isRealisticCelsius(double temperature) {
        return !Double.isNaN(temperature) &&
               !Double.isInfinite(temperature) &&
               temperature >= MIN_CELSIUS &&
               temperature <= MAX_CELSIUS;
    }

    /**
     * Check if temperature is realistic in Fahrenheit
     */
    public static boolean isRealisticFahrenheit(double temperature) {
        return !Double.isNaN(temperature) &&
               !Double.isInfinite(temperature) &&
               temperature >= MIN_FAHRENHEIT &&
               temperature <= MAX_FAHRENHEIT;
    }

    /**
     * Format temperature with unit symbol
     */
    public static String formatTemperature(double temperature, String unit) {
        return String.format("%.1f%s", temperature, unit);
    }
}

