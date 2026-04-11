package edu.atu.healthlog.studenthealthweatherlog.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.atu.healthlog.studenthealthweatherlog.UserPreferences;
import edu.atu.healthlog.studenthealthweatherlog.models.WeatherData;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

/**
 * WeatherService - Fetches real-time weather data from the OpenWeatherMap API.
 */
public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final String apiKey;

    /** Default constructor — uses the placeholder key (no live fetch). */
    public WeatherService() {
        this.apiKey = "YOUR_API_KEY";
    }

    /** Testing constructor — supply a real or mock API key. */
    protected WeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

    /** Lightweight result used by Dashboard and AddLog controllers. */
    public static class WeatherData {
        public final String condition;
        public final double temp;

        public WeatherData(String condition, double temp) {
            this.condition = condition;
            this.temp = temp;
        }
    }

    /**
     * Quick fetch for UI display (condition + temp only).
     * Returns mock data if no real API key is configured or if the call fails.
     */
    public WeatherData getCurrentWeather() {
        if ("YOUR_API_KEY".equals(apiKey)) {
            return new WeatherData("Partly Cloudy", 22.0);
        }

        try {
            String city = UserPreferences.getCity();
            JsonObject json = fetchJson(city);
            if (json != null) {
                String condition = json.get("weather").getAsJsonArray()
                        .get(0).getAsJsonObject().get("main").getAsString();
                double temp = json.get("main").getAsJsonObject().get("temp").getAsDouble();
                return new WeatherData(condition, temp);
            }
        } catch (Exception e) {
            System.err.println("WeatherService.getCurrentWeather failed: " + e.getMessage());
        }

        return new WeatherData("Unknown", 0.0);
    }

    /**
     * Full fetch that returns a {@link edu.atu.healthlog.studenthealthweatherlog.models.WeatherData}
     * ready to be persisted. Returns {@code null} on any failure so callers can skip saving.
     */
    public edu.atu.healthlog.studenthealthweatherlog.models.WeatherData fetchForPersistence() {
        if ("YOUR_API_KEY".equals(apiKey)) {
            System.out.println("WeatherService: no real API key configured — skipping persistence fetch.");
            return null;
        }

        String city = UserPreferences.getCity();
        try {
            JsonObject json = fetchJson(city);
            if (json == null) return null;

            String condition = json.get("weather").getAsJsonArray()
                    .get(0).getAsJsonObject().get("main").getAsString();
            double temp     = json.get("main").getAsJsonObject().get("temp").getAsDouble();
            double humidity = json.get("main").getAsJsonObject().get("humidity").getAsDouble();
            double wind     = json.get("wind").getAsJsonObject().get("speed").getAsDouble();

            edu.atu.healthlog.studenthealthweatherlog.models.WeatherData data =
                    new edu.atu.healthlog.studenthealthweatherlog.models.WeatherData(0, LocalDate.now(), city);
            data.setCondition(condition);
            data.setTemperature(temp);
            data.setHumidity(humidity);
            data.setWindSpeed(wind);
            data.setUvIndex(0); // UV index requires a separate API endpoint
            return data;

        } catch (Exception e) {
            System.err.println("WeatherService.fetchForPersistence failed: " + e.getMessage());
            return null;
        }
    }

    /** Opens a connection and parses the JSON response, or returns null on failure. */
    protected JsonObject fetchJson(String city) {
        try {
            String urlString = BASE_URL + "?q=" + city + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                    return JsonParser.parseReader(reader).getAsJsonObject();
                }
            } else {
                System.err.println("WeatherService: HTTP " + conn.getResponseCode() + " for city=" + city);
            }
        } catch (Exception e) {
            System.err.println("WeatherService: network error: " + e.getMessage());
        }
        return null;
    }
}
