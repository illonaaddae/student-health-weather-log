package edu.atu.healthlog.studenthealthweatherlog.models;

import edu.atu.healthlog.studenthealthweatherlog.models.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherDataTests {
    private WeatherData weatherData;

    private final int WEATHER_ID = 1;
    private final LocalDate WEATHER_DATE = LocalDate.now();
    private final String CITY = "Accra";

    @BeforeEach
    void createWeather(){
        weatherData = new WeatherData(WEATHER_ID, WEATHER_DATE, CITY);
    }

    @Test
    void constructor_withAppropriateValue_objectCreatedSuccessfully(){
        assertNotNull(weatherData);
    }

    @Test
    void getDate_afterConstruction_returnsCorrectValue(){
        assertEquals(WEATHER_DATE, weatherData.getDate());
    }

    @Test
    void getCity_afterConstruction_returnsCorrectValue(){
        assertEquals(CITY, weatherData.getCity());
    }

    @Test
    void setDate_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setDate(null));
    }

    @Test
    void setDate_withFutureDate_throwsIllegalArgumentException(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> weatherData.setDate(tomorrow));
    }

    @Test
    void setDate_withToday_setsSuccessfully(){
        weatherData.setDate(WEATHER_DATE);
        assertEquals(WEATHER_DATE, weatherData.getDate());
    }

    @Test
    void setDate_withPastDate_setsSuccessfully(){
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertDoesNotThrow(() -> weatherData.setDate(lastWeek));
        assertEquals(lastWeek, weatherData.getDate());
    }

    @Test
    void setTemperature_belowMinimum_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setTemperature(-100.0));
    }

    @Test
    void setTemperature_aboveMaximum_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setTemperature(61.0));
    }

    @Test
    void setTemperature_withValidValue_setsSuccessfully(){
        weatherData.setTemperature(20.0);
        assertEquals(20.0, weatherData.getTemperature());
    }

    @Test
    void setHumidity_belowZero_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setHumidity(-10.0));
    }

    @Test
    void setHumidity_aboveHundred_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setHumidity(101.0));
    }

    @Test
    void setHumidity_atZero_setsSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setHumidity(0.0));
        assertEquals(0.0, weatherData.getHumidity());
    }

    @Test
    void setHumidity_atHundred_setsSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setHumidity(100.0));
        assertEquals(100.0, weatherData.getHumidity());
    }

    @Test
    void setHumidity_withValidValue_setsSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setHumidity(65.0));
        assertEquals(65.0, weatherData.getHumidity());
    }

    @Test
    void setUvIndex_withNegativeValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setUvIndex(-1));
    }

    @Test
    void setUvIndex_atZero_setSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setUvIndex(0));
        assertEquals(0, weatherData.getUvIndex());
    }

    @Test
    void setUvIndex_withValidValue_setSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setUvIndex(15.0));
        assertEquals(15.0, weatherData.getUvIndex());
    }

    @Test
    void setWindSpeed_withNegativeValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setWindSpeed(-1));
    }

    @Test
    void setWindSpeed_atZero_setsSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setWindSpeed(0));
        assertEquals(0, weatherData.getWindSpeed());
    }

    @Test
    void setWindSpeed_withValidValue_setSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setWindSpeed(15.0));
        assertEquals(15.0, weatherData.getWindSpeed());
    }

    @Test
    void setCondition_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setCondition(null));
    }

    @Test
    void setCondition_withEmptyString_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setCondition(""));
    }

    @Test
    void setCondition_withBlank_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> weatherData.setCondition("  "));
    }

    @Test
    void setCondition_withValidValue_setsSuccessfully(){
        assertDoesNotThrow(() -> weatherData.setCondition("Sunny"));
        assertEquals("Sunny", weatherData.getCondition());
    }

    @Test
    void equals_twoRecordsWithSameId_returnsTrue(){
        WeatherData weatherData2 = new WeatherData(WEATHER_ID, LocalDate.now(), "Accra");
        assertEquals(weatherData, weatherData2);
    }

    @Test
    void equals_twoRecordsWithDifferentId_returnsFalse(){
        WeatherData weatherData2 = new WeatherData(2, LocalDate.now(), "Accra");
        assertNotEquals(weatherData, weatherData2);
    }

    @Test
    void hashCode_twoRecordsWithSameId_returnsSameHash(){
        WeatherData weatherData2 = new WeatherData(WEATHER_ID, LocalDate.now(), "Accra");
        assertEquals(weatherData.hashCode(), weatherData2.hashCode());
    }
}
