package edu.atu.healthlog.studenthealthweatherlog.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.atu.healthlog.studenthealthweatherlog.models.WeatherData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeatherService unit tests")
class WeatherServiceTest {

    /**
     * Subclass that bypasses HTTP by overriding fetchJson.
     * The constructor sets a non-placeholder API key so fetchForPersistence
     * does not short-circuit before calling fetchJson.
     */
    private static class StubWeatherService extends WeatherService {
        private final JsonObject stubResponse;

        StubWeatherService(JsonObject stubResponse) {
            super("test-api-key"); // non-placeholder key — skips the "YOUR_API_KEY" guard
            this.stubResponse = stubResponse;
        }

        @Override
        protected JsonObject fetchJson(String city) {
            return stubResponse; // null means "simulate network failure"
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private JsonObject buildApiResponse(String condition, double temp, double humidity, double wind) {
        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("temp", temp);
        mainObj.addProperty("humidity", humidity);

        JsonObject weatherEntry = new JsonObject();
        weatherEntry.addProperty("main", condition);
        JsonArray weatherArr = new JsonArray();
        weatherArr.add(weatherEntry);

        JsonObject windObj = new JsonObject();
        windObj.addProperty("speed", wind);

        JsonObject root = new JsonObject();
        root.add("main", mainObj);
        root.add("weather", weatherArr);
        root.add("wind", windObj);
        return root;
    }

    // ── getCurrentWeather ────────────────────────────────────────────────────

    @Test
    @DisplayName("getCurrentWeather: returns mock data when no real API key is set")
    void getCurrentWeather_withPlaceholderKey_returnsMockData() {
        WeatherService service = new WeatherService(); // uses "YOUR_API_KEY"
        WeatherService.WeatherData result = service.getCurrentWeather();

        assertNotNull(result);
        assertFalse(result.condition.isBlank());
    }

    @Test
    @DisplayName("getCurrentWeather: parses condition and temperature from JSON")
    void getCurrentWeather_parsesConditionAndTemp() {
        JsonObject mockJson = buildApiResponse("Rain", 18.5, 80.0, 5.0);
        WeatherService service = new StubWeatherService(mockJson);

        WeatherService.WeatherData result = service.getCurrentWeather();

        assertNotNull(result);
        assertEquals("Rain", result.condition);
        assertEquals(18.5, result.temp, 0.001);
    }

    @Test
    @DisplayName("getCurrentWeather: returns 'Unknown' when network fails")
    void getCurrentWeather_onNetworkFailure_returnsUnknown() {
        WeatherService service = new StubWeatherService(null); // null simulates failure

        WeatherService.WeatherData result = service.getCurrentWeather();

        assertNotNull(result);
        assertEquals("Unknown", result.condition);
        assertEquals(0.0, result.temp, 0.001);
    }

    // ── fetchForPersistence ──────────────────────────────────────────────────

    @Test
    @DisplayName("fetchForPersistence: returns null when no real API key is set")
    void fetchForPersistence_withPlaceholderKey_returnsNull() {
        WeatherService service = new WeatherService();
        assertNull(service.fetchForPersistence());
    }

    @Test
    @DisplayName("fetchForPersistence: maps all fields from JSON response")
    void fetchForPersistence_mapsAllFields() {
        JsonObject mockJson = buildApiResponse("Sunny", 32.0, 60.0, 10.5);
        WeatherService service = new StubWeatherService(mockJson);

        WeatherData result = service.fetchForPersistence();

        assertNotNull(result);
        assertEquals("Sunny",  result.getCondition());
        assertEquals(32.0,     result.getTemperature(), 0.001);
        assertEquals(60.0,     result.getHumidity(),    0.001);
        assertEquals(10.5,     result.getWindSpeed(),   0.001);
        assertNotNull(result.getDate());
        assertNotNull(result.getCity());
    }

    @Test
    @DisplayName("fetchForPersistence: returns null when network fails")
    void fetchForPersistence_onNetworkFailure_returnsNull() {
        WeatherService service = new StubWeatherService(null);
        assertNull(service.fetchForPersistence());
    }

    @Test
    @DisplayName("fetchForPersistence: sets UV index to 0 (separate API endpoint)")
    void fetchForPersistence_uvIndexIsZero() {
        JsonObject mockJson = buildApiResponse("Cloudy", 25.0, 70.0, 8.0);
        WeatherService service = new StubWeatherService(mockJson);

        WeatherData result = service.fetchForPersistence();

        assertNotNull(result);
        assertEquals(0.0, result.getUvIndex(), 0.001);
    }
}
