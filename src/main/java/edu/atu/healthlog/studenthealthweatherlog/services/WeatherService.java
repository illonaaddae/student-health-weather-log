package edu.atu.healthlog.studenthealthweatherlog.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * WeatherService - Fetches real-time weather data from an external API.
 */
public class WeatherService {
    // Note: Using a public API like OpenWeatherMap requires an API key. 
    // This is a placeholder for demonstration. In a real app, you'd secure this.
    private static final String API_KEY = "YOUR_API_KEY"; // Placeholder
    private static final String CITY = "London"; // Can be dynamic from settings

    public static class WeatherData {
        public String condition;
        public double temp;

        public WeatherData(String condition, double temp) {
            this.condition = condition;
            this.temp = temp;
        }
    }

    /**
     * Fetches the current weather data.
     * Returns mock data if API key is not provided or if request fails.
     */
    public WeatherData getCurrentWeather() {
        if ("YOUR_API_KEY".equals(API_KEY)) {
            // Fallback mock data if no real API key is set
            return new WeatherData("Partly Cloudy", 68.0);
        }

        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                
                String condition = json.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();
                double temp = json.get("main").getAsJsonObject().get("temp").getAsDouble();
                
                return new WeatherData(condition, temp);
            }
        } catch (Exception e) {
            System.err.println("Weather API call failed: " + e.getMessage());
        }

        return new WeatherData("Unknown", 0.0);
    }
}
