package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.repositories.HealthEntryRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HealthEntryRepository integration tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HealthEntryRepositoryTest {
    private static Connection connection;
    private HealthEntryRepository healthEntryRepository;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:healthdb;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS health_entries (
                id                INT AUTO_INCREMENT PRIMARY KEY,
                user_id           INT          NOT NULL,
                entry_date        DATE         NOT NULL,
                mood_score        VARCHAR(100) NOT NULL,
                sleep_hours       DOUBLE       NOT NULL,
                water_intake      DOUBLE       NOT NULL,
                exercise          VARCHAR(100) NOT NULL,
                notes             VARCHAR(500) NOT NULL,
                weather_condition VARCHAR(100),
                temperature       DOUBLE
            )
        """);
        healthEntryRepository = new HealthEntryRepository(connection);
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    void save_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.save(null));
    }

    @Test
    @Order(2)
    void save_withValidEntry_returnsPositiveGeneratedId() throws SQLException {
        int generatedId = healthEntryRepository.save(buildValidEntry());
        assertTrue(generatedId > 0);
    }

    @Test
    @Order(3)
    void save_withValidEntry_persistsDataRetrievableById() throws SQLException {
        int generatedId = healthEntryRepository.save(buildValidEntry());

        Optional<HealthEntry> result = healthEntryRepository.findById(generatedId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserId());
        assertEquals("Good", result.get().getMoodScore());
        assertEquals("Running", result.get().getExercise());
        assertEquals(7.5, result.get().getSleepHours());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    void findById_withExistingId_returnsCorrectEntry() throws SQLException {
        int generatedId = healthEntryRepository.save(buildValidEntry());

        Optional<HealthEntry> result = healthEntryRepository.findById(generatedId);

        assertTrue(result.isPresent());
        assertEquals(generatedId, result.get().getId());
    }

    @Test
    @Order(5)
    void findById_withNonExistentId_returnsEmptyOptional() throws SQLException {
        Optional<HealthEntry> result = healthEntryRepository.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(6)
    void findById_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.findById(0));
    }

    @Test
    @Order(7)
    void findById_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.findById(-1));
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    @Test
    @Order(8)
    void findByUserId_withExistingUserId_returnsMatchingEntries() throws SQLException {
        healthEntryRepository.save(buildEntry(1, LocalDate.now()));
        healthEntryRepository.save(buildEntry(1, LocalDate.now().minusDays(1)));
        healthEntryRepository.save(buildEntry(2, LocalDate.now()));

        List<HealthEntry> result = healthEntryRepository.findByUserId(1);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getUserId() == 1));
    }

    @Test
    @Order(9)
    void findByUserId_withNonExistentUserId_returnsEmptyList() throws SQLException {
        List<HealthEntry> result = healthEntryRepository.findByUserId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(10)
    void findByUserId_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.findByUserId(0));
    }

    @Test
    @Order(11)
    void findByUserId_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.findByUserId(-1));
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    @Order(12)
    void findAll_onEmptyTable_returnsEmptyList() throws SQLException {
        List<HealthEntry> result = healthEntryRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(13)
    void findAll_withMultipleEntries_returnsAllEntries() throws SQLException {
        healthEntryRepository.save(buildEntry(1, LocalDate.now()));
        healthEntryRepository.save(buildEntry(2, LocalDate.now().minusDays(1)));
        healthEntryRepository.save(buildEntry(3, LocalDate.now().minusDays(2)));

        List<HealthEntry> result = healthEntryRepository.findAll();

        assertEquals(3, result.size());
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    @Order(14)
    void update_withExistingEntry_returnsTrueAndPersistsChanges() throws SQLException {
        int generatedId = healthEntryRepository.save(buildValidEntry());

        Optional<HealthEntry> saved = healthEntryRepository.findById(generatedId);
        assertTrue(saved.isPresent());

        HealthEntry toUpdate = saved.get();
        toUpdate.setMoodScore("Tired");
        toUpdate.setNotes("Felt exhausted today");

        boolean result = healthEntryRepository.update(toUpdate);

        assertTrue(result);

        Optional<HealthEntry> updated = healthEntryRepository.findById(generatedId);
        assertTrue(updated.isPresent());
        assertEquals("Tired", updated.get().getMoodScore());
        assertEquals("Felt exhausted today", updated.get().getNotes());
    }

    @Test
    @Order(15)
    void update_withNonExistentEntry_returnsFalse() throws SQLException {
        HealthEntry ghost = new HealthEntry(999, LocalDate.now(), "Neutral", 6.0, 1.5, "Walking", "");
        ghost.setId(999);

        boolean result = healthEntryRepository.update(ghost);

        assertFalse(result);
    }

    @Test
    @Order(16)
    void update_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.update(null));
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @Order(17)
    void delete_withExistingId_returnsTrueAndRemovesEntry() throws SQLException {
        int generatedId = healthEntryRepository.save(buildValidEntry());

        boolean result = healthEntryRepository.delete(generatedId);

        assertTrue(result);

        Optional<HealthEntry> afterDelete = healthEntryRepository.findById(generatedId);
        assertFalse(afterDelete.isPresent());
    }

    @Test
    @Order(18)
    void delete_withNonExistentId_returnsFalse() throws SQLException {
        boolean result = healthEntryRepository.delete(999);
        assertFalse(result);
    }

    @Test
    @Order(19)
    void delete_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.delete(0));
    }

    @Test
    @Order(20)
    void delete_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntryRepository.delete(-1));
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

    private HealthEntry buildValidEntry() {
        return buildEntry(1, LocalDate.now());
    }

    private HealthEntry buildEntry(int userId, LocalDate date) {
        return new HealthEntry(userId, date, "Good", 7.5, 2.0, "Running", "Feeling good");
    }
}
