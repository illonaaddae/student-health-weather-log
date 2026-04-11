package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.Correlation;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CorrelationRepository integration tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CorrelationRepositoryTest {

    private static Connection connection;
    private CorrelationRepository repository;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:correlationdb;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS correlations (
                id                INT AUTO_INCREMENT PRIMARY KEY,
                user_id           INT          NOT NULL,
                health_entry_id   INT          NOT NULL,
                entry_date        DATE         NOT NULL,
                weather_condition VARCHAR(100) NOT NULL,
                temperature       DOUBLE       NOT NULL DEFAULT 0.0,
                mood_numeric      INT          NOT NULL,
                correlation_score DOUBLE       NOT NULL
            )
        """);
        repository = new CorrelationRepository(connection);
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test @Order(1)
    void save_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test @Order(2)
    void save_withValidCorrelation_returnsPositiveId() throws SQLException {
        int id = repository.save(buildValid());
        assertTrue(id > 0);
    }

    @Test @Order(3)
    void save_persistsAllFields() throws SQLException {
        int id = repository.save(buildValid());
        Optional<Correlation> result = repository.findById(id);

        assertTrue(result.isPresent());
        Correlation c = result.get();
        assertEquals(1,        c.getUserId());
        assertEquals(10,       c.getHealthEntryId());
        assertEquals("Sunny",  c.getWeatherCondition());
        assertEquals(28.0,     c.getTemperature(), 0.001);
        assertEquals(4,        c.getMoodNumeric());
        assertEquals(3.80,     c.getCorrelationScore(), 0.001);
        assertEquals(LocalDate.now(), c.getEntryDate());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test @Order(4)
    void findById_withExistingId_returnsRecord() throws SQLException {
        int id = repository.save(buildValid());
        assertTrue(repository.findById(id).isPresent());
    }

    @Test @Order(5)
    void findById_withNonExistentId_returnsEmpty() throws SQLException {
        assertTrue(repository.findById(999).isEmpty());
    }

    @Test @Order(6)
    void findById_withZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(0));
    }

    @Test @Order(7)
    void findById_withNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(-1));
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    @Test @Order(8)
    void findByUserId_returnsOnlyRecordsForThatUser() throws SQLException {
        repository.save(buildFor(1, 10));
        repository.save(buildFor(1, 11));
        repository.save(buildFor(2, 12)); // different user

        List<Correlation> result = repository.findByUserId(1);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getUserId() == 1));
    }

    @Test @Order(9)
    void findByUserId_withNoRecords_returnsEmptyList() throws SQLException {
        List<Correlation> result = repository.findByUserId(99);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test @Order(10)
    void findByUserId_withZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByUserId(0));
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test @Order(11)
    void findAll_onEmptyTable_returnsEmptyList() throws SQLException {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test @Order(12)
    void findAll_returnsAllRecords() throws SQLException {
        repository.save(buildFor(1, 10));
        repository.save(buildFor(2, 11));
        repository.save(buildFor(3, 12));
        assertEquals(3, repository.findAll().size());
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test @Order(13)
    void update_withExistingRecord_persistsChanges() throws SQLException {
        int id = repository.save(buildValid());
        Correlation c = repository.findById(id).orElseThrow();

        c.setWeatherCondition("Rainy");
        c.setCorrelationScore(2.50);
        boolean updated = repository.update(c);

        assertTrue(updated);
        Correlation refreshed = repository.findById(id).orElseThrow();
        assertEquals("Rainy", refreshed.getWeatherCondition());
        assertEquals(2.50,    refreshed.getCorrelationScore(), 0.001);
    }

    @Test @Order(14)
    void update_withNonExistentRecord_returnsFalse() throws SQLException {
        Correlation ghost = buildValid();
        ghost.setId(9999);
        assertFalse(repository.update(ghost));
    }

    @Test @Order(15)
    void update_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.update(null));
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test @Order(16)
    void delete_withExistingId_removesRecord() throws SQLException {
        int id = repository.save(buildValid());
        assertTrue(repository.delete(id));
        assertTrue(repository.findById(id).isEmpty());
    }

    @Test @Order(17)
    void delete_withNonExistentId_returnsFalse() throws SQLException {
        assertFalse(repository.delete(9999));
    }

    @Test @Order(18)
    void delete_withZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.delete(0));
    }

    @Test @Order(19)
    void delete_withNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.delete(-1));
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

    private Correlation buildValid() {
        return buildFor(1, 10);
    }

    private Correlation buildFor(int userId, int healthEntryId) {
        return new Correlation(userId, healthEntryId, LocalDate.now(),
                "Sunny", 28.0, 4, 3.80);
    }
}
