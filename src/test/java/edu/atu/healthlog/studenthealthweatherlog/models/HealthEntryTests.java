package edu.atu.healthlog.studenthealthweatherlog.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HealthEntryTests {
    private final int ENTRY_ID = 1;
    private final int USER_ID = 1;
    private final LocalDate DATE = LocalDate.now();
    private final String MOOD_SCORE = "Good";
    private final double SLEEP_HOURS = 7.5;
    private final double WATER_INTAKE = 2.0;
    private final String EXERCISE = "Running (30 min)";
    private final String NOTES = "Some notes go here";

    private HealthEntry healthEntry;

    @BeforeEach
    void setUp() {
        healthEntry = new HealthEntry(USER_ID, DATE, MOOD_SCORE, SLEEP_HOURS, WATER_INTAKE, EXERCISE, NOTES);
    }

    // ── constructor ───────────────────────────────────────────────────────────

    @Test
    void constructor_withValidInputs_createsHealthEntrySuccessfully() {
        assertNotNull(healthEntry);
    }

    // ── userId ────────────────────────────────────────────────────────────────

    @Test
    void getUserId_afterConstruction_returnsCorrectValue() {
        assertEquals(USER_ID, healthEntry.getUserId());
    }

    @Test
    void setUserId_withZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setUserId(0));
    }

    @Test
    void setUserId_withNegativeValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setUserId(-1));
    }

    @Test
    void setUserId_withPositiveValue_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setUserId(2));
        assertEquals(2, healthEntry.getUserId());
    }

    // ── entryDate ─────────────────────────────────────────────────────────────

    @Test
    void setEntryDate_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setEntryDate(null));
    }

    @Test
    void setEntryDate_withFutureDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setEntryDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void setEntryDate_withToday_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setEntryDate(LocalDate.now()));
        assertEquals(LocalDate.now(), healthEntry.getEntryDate());
    }

    @Test
    void setEntryDate_withPastDate_setsSuccessfully() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertDoesNotThrow(() -> healthEntry.setEntryDate(lastWeek));
        assertEquals(lastWeek, healthEntry.getEntryDate());
    }

    // ── moodScore ─────────────────────────────────────────────────────────────

    @Test
    void setMoodScore_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setMoodScore(null));
    }

    @Test
    void setMoodScore_withInvalidValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setMoodScore("Happy"));
    }

    @Test
    void setMoodScore_withLow_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setMoodScore("Low"));
        assertEquals("Low", healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withNeutral_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setMoodScore("Neutral"));
        assertEquals("Neutral", healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withGood_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setMoodScore("Good"));
        assertEquals("Good", healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withGreat_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setMoodScore("Great"));
        assertEquals("Great", healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withTired_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setMoodScore("Tired"));
        assertEquals("Tired", healthEntry.getMoodScore());
    }

    // ── sleepHours ────────────────────────────────────────────────────────────

    @Test
    void setSleepHours_withNegativeValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setSleepHours(-1.0));
    }

    @Test
    void setSleepHours_aboveTwentyFour_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setSleepHours(25.0));
    }

    @Test
    void setSleepHours_withValidValue_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setSleepHours(8.0));
        assertEquals(8.0, healthEntry.getSleepHours());
    }

    // ── waterIntake ───────────────────────────────────────────────────────────

    @Test
    void setWaterIntake_withNegativeValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setWaterIntake(-1.0));
    }

    @Test
    void setWaterIntake_withValidValue_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setWaterIntake(3.0));
        assertEquals(3.0, healthEntry.getWaterIntake());
    }

    // ── exercise ──────────────────────────────────────────────────────────────

    @Test
    void setExercise_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setExercise(null));
    }

    @Test
    void setExercise_withValidValue_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setExercise("Walking (20 min)"));
        assertEquals("Walking (20 min)", healthEntry.getExercise());
    }

    @Test
    void setExercise_withNone_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setExercise("None"));
        assertEquals("None", healthEntry.getExercise());
    }

    // ── weatherCondition ──────────────────────────────────────────────────────

    @Test
    void setWeatherCondition_withValidValue_setsSuccessfully() {
        healthEntry.setWeatherCondition("Partly Cloudy");
        assertEquals("Partly Cloudy", healthEntry.getWeatherCondition());
    }

    @Test
    void setWeatherCondition_withNull_setsSuccessfully() {
        assertDoesNotThrow(() -> healthEntry.setWeatherCondition(null));
    }

    // ── temperature ───────────────────────────────────────────────────────────

    @Test
    void setTemperature_withPositiveValue_setsSuccessfully() {
        healthEntry.setTemperature(28.5);
        assertEquals(28.5, healthEntry.getTemperature());
    }

    @Test
    void setTemperature_withNegativeValue_setsSuccessfully() {
        healthEntry.setTemperature(-5.0);
        assertEquals(-5.0, healthEntry.getTemperature());
    }

    // ── notes ─────────────────────────────────────────────────────────────────

    @Test
    void setNotes_withNull_setsSuccessfully() {
        healthEntry.setNotes(null);
        assertEquals("", healthEntry.getNotes());
    }

    @Test
    void setNotes_withGoodNotesText_setsSuccessfully() {
        healthEntry.setNotes("Notes for testing");
        assertEquals("Notes for testing", healthEntry.getNotes());
    }

    // ── equals / hashCode ─────────────────────────────────────────────────────

    @Test
    void equals_twoEntriesWithSameId_returnsTrue() {
        HealthEntry healthEntry1 = new HealthEntry(USER_ID + 1, DATE, MOOD_SCORE, SLEEP_HOURS, WATER_INTAKE, EXERCISE, NOTES);
        healthEntry.setId(ENTRY_ID);
        healthEntry1.setId(ENTRY_ID);
        assertEquals(healthEntry, healthEntry1);
    }

    @Test
    void equals_twoEntriesWithDifferentId_returnsFalse() {
        HealthEntry healthEntry1 = new HealthEntry(USER_ID + 1, DATE, MOOD_SCORE, SLEEP_HOURS, WATER_INTAKE, EXERCISE, NOTES);
        healthEntry.setId(ENTRY_ID);
        healthEntry1.setId(ENTRY_ID + 1);
        assertNotEquals(healthEntry, healthEntry1);
    }

    @Test
    void hashCode_twoEntriesWithSameId_returnsSameHash() {
        HealthEntry healthEntry1 = new HealthEntry(USER_ID + 1, DATE, MOOD_SCORE, SLEEP_HOURS, WATER_INTAKE, EXERCISE, NOTES);
        healthEntry.setId(ENTRY_ID);
        healthEntry1.setId(ENTRY_ID);
        assertEquals(healthEntry.hashCode(), healthEntry1.hashCode());
    }
}
