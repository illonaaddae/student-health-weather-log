package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.WeatherData;
import edu.atu.healthlog.studenthealthweatherlog.repositories.WeatherDataRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeatherDataRepository integration tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WeatherDataRepositoryTest {
    private static Connection connection;
    private WeatherDataRepository weatherDataRepository;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:weatherdb;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS weather_data (
                weather_id  INT AUTO_INCREMENT PRIMARY KEY,
                date        DATE          NOT NULL,
                temperature DOUBLE        NOT NULL,
                humidity    DOUBLE        NOT NULL,
                condition   VARCHAR(100)  NOT NULL,
                uv_index    DOUBLE        NOT NULL,
                wind_speed  DOUBLE        NOT NULL,
                city        VARCHAR(100)  NOT NULL
            )
        """);
        weatherDataRepository = new WeatherDataRepository(connection);
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    void save_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.save(null));
    }

    @Test
    @Order(2)
    void save_withValidWeatherData_returnsPositiveGeneratedId() throws SQLException {
        int generatedId = weatherDataRepository.save(buildValidWeatherData());
        assertTrue(generatedId > 0);
    }

    @Test
    @Order(3)
    void save_withValidWeatherData_persistsDataRetrievableById() throws SQLException {
        int generatedId = weatherDataRepository.save(buildValidWeatherData());

        Optional<WeatherData> result = weatherDataRepository.findById(generatedId);

        assertTrue(result.isPresent());
        assertEquals("Accra", result.get().getCity());
        assertEquals("Sunny", result.get().getCondition());
        assertEquals(28.5, result.get().getTemperature());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    void findById_withExistingId_returnsCorrectRecord() throws SQLException {
        int generatedId = weatherDataRepository.save(buildValidWeatherData());

        Optional<WeatherData> result = weatherDataRepository.findById(generatedId);

        assertTrue(result.isPresent());
        assertEquals(generatedId, result.get().getId());
    }

    @Test
    @Order(5)
    void findById_withNonExistentId_returnsEmptyOptional() throws SQLException {
        Optional<WeatherData> result = weatherDataRepository.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(6)
    void findById_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.findById(0));
    }

    @Test
    @Order(7)
    void findById_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.findById(-1));
    }

    // ── findByCity ────────────────────────────────────────────────────────────

    @Test
    @Order(8)
    void findByCity_withExistingCity_returnsMatchingRecords() throws SQLException {
        weatherDataRepository.save(buildValidWeatherData());
        weatherDataRepository.save(buildValidWeatherData());

        List<WeatherData> result = weatherDataRepository.findByCity("Accra");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(w -> w.getCity().equals("Accra")));
    }

    @Test
    @Order(9)
    void findByCity_withNonExistentCity_returnsEmptyList() throws SQLException {
        List<WeatherData> result = weatherDataRepository.findByCity("Atlantis");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(10)
    void findByCity_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.findByCity(null));
    }

    @Test
    @Order(11)
    void findByCity_withBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.findByCity("   "));
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    @Order(12)
    void findAll_onEmptyTable_returnsEmptyList() throws SQLException {
        List<WeatherData> result = weatherDataRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(13)
    void findAll_withMultipleRecords_returnsAllRecords() throws SQLException {
        weatherDataRepository.save(buildWeatherData("Accra",  LocalDate.now()));
        weatherDataRepository.save(buildWeatherData("Kumasi", LocalDate.now().minusDays(1)));
        weatherDataRepository.save(buildWeatherData("Tema",   LocalDate.now().minusDays(2)));

        List<WeatherData> result = weatherDataRepository.findAll();

        assertEquals(3, result.size());
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    @Order(14)
    void update_withExistingRecord_returnsTrueAndPersistsChanges() throws SQLException {
        int generatedId = weatherDataRepository.save(buildValidWeatherData());

        Optional<WeatherData> saved = weatherDataRepository.findById(generatedId);
        assertTrue(saved.isPresent());

        WeatherData toUpdate = saved.get();
        toUpdate.setTemperature(35.0);
        toUpdate.setCondition("Hot");

        boolean result = weatherDataRepository.update(toUpdate);

        assertTrue(result);

        Optional<WeatherData> updated = weatherDataRepository.findById(generatedId);
        assertTrue(updated.isPresent());
        assertEquals(35.0, updated.get().getTemperature());
        assertEquals("Hot", updated.get().getCondition());
    }

    @Test
    @Order(15)
    void update_withNonExistentRecord_returnsFalse() throws SQLException {
        WeatherData ghost = new WeatherData(999, LocalDate.now().minusDays(1), "Nowhere");
        ghost.setTemperature(20.0);
        ghost.setHumidity(50.0);
        ghost.setCondition("Cloudy");
        ghost.setUvIndex(3.0);
        ghost.setWindSpeed(5.0);

        boolean result = weatherDataRepository.update(ghost);

        assertFalse(result);
    }

    @Test
    @Order(16)
    void update_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.update(null));
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @Order(17)
    void delete_withExistingId_returnsTrueAndRemovesRecord() throws SQLException {
        int generatedId = weatherDataRepository.save(buildValidWeatherData());

        boolean result = weatherDataRepository.delete(generatedId);

        assertTrue(result);

        Optional<WeatherData> afterDelete = weatherDataRepository.findById(generatedId);
        assertFalse(afterDelete.isPresent());
    }

    @Test
    @Order(18)
    void delete_withNonExistentId_returnsFalse() throws SQLException {
        boolean result = weatherDataRepository.delete(999);
        assertFalse(result);
    }

    @Test
    @Order(19)
    void delete_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.delete(0));
    }

    @Test
    @Order(20)
    void delete_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weatherDataRepository.delete(-1));
    }

    // ── lifecycle ─────────────────────────────────────────────────────────────

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DROP ALL OBJECTS");
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private WeatherData buildValidWeatherData() {
        return buildWeatherData("Accra", LocalDate.now());
    }

    private WeatherData buildWeatherData(String city, LocalDate date) {
        WeatherData wd = new WeatherData(0, date, city);
        wd.setTemperature(28.5);
        wd.setHumidity(75.0);
        wd.setCondition("Sunny");
        wd.setUvIndex(6.0);
        wd.setWindSpeed(12.0);
        return wd;
    }
}
