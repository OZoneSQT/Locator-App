package dk.seahawk.locator.data.model;

import com.google.gson.annotations.SerializedName;
import dk.seahawk.locator.data.local.PreferenceManager;
import dk.seahawk.locator.util.TemperatureUtils;

/**
 * Weather data model for storing weather information
 */
public class WeatherData {
    @SerializedName("temp")
    private double temperature;

    @SerializedName("humidity")
    private int humidity;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("wind_speed")
    private double windSpeed;

    @SerializedName("description")
    private String description;

    @SerializedName("main")
    private String weatherMain;

    public WeatherData() {}

    public WeatherData(double temperature, int humidity, int pressure, double windSpeed, String description, String weatherMain) {
        this.temperature = TemperatureUtils.validateCelsius(temperature);
        this.humidity = validateHumidity(humidity);
        this.pressure = validatePressure(pressure);
        this.windSpeed = Math.max(0, windSpeed);
        this.description = description != null ? description : "unknown";
        this.weatherMain = weatherMain != null ? weatherMain : "Unknown";
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperature(PreferenceManager.TemperatureUnit unit) {
        if (unit == PreferenceManager.TemperatureUnit.FAHRENHEIT) {
            return TemperatureUtils.celsiusToFahrenheit(temperature);
        }
        return temperature;
    }

    public String getFormattedTemperature(PreferenceManager.TemperatureUnit unit) {
        double temp = getTemperature(unit);
        return TemperatureUtils.formatTemperature(temp, unit.getSymbol());
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getDescription() {
        return description;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public String getFormattedWeather(PreferenceManager.TemperatureUnit unit) {
        // \uD83C\uDF21\uFE0F (🌡️), \uD83D\uDCA7 (💧), \uD83D\uDCA8 (💨)
        return String.format("\uD83C\uDF21\uFE0F %s | \uD83D\uDCA7 %d%% | \uD83D\uDCA8 %.1f m/s | %s",
                getFormattedTemperature(unit), humidity, windSpeed, description);
    }

    public String getFormattedWeather() {
        return getFormattedWeather(PreferenceManager.TemperatureUnit.CELSIUS);
    }

    private int validateHumidity(int humidity) {
        if (humidity < 0) return 0;
        return Math.min(humidity, 100);
    }

    private int validatePressure(int pressure) {
        if (pressure < 870) return 1013;
        if (pressure > 1085) return 1013;
        return pressure;
    }
}
