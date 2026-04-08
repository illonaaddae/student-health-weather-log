package com.kingsley.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HealthEntryTests {
    private final int ENTRY_ID = 1;
    private final int USER_ID = 1;
    private final int WEATHER_ID =1;
    private final LocalDate DATE = LocalDate.now();
    private final int MOOD_SCORE = 3;
//    private final double SLEEP_HOURS = 7.30;
    private final String ACTIVITY_TYPE = "Sleep";
    private final double ACTIVITY_DURATION = 7.30;
    private final int ENERGY_LEVEL = 3;
    private final String NOTES = "Some notes go here";

    private HealthEntry healthEntry;

    @BeforeEach
    void setUp(){
        healthEntry = new HealthEntry(ENTRY_ID, USER_ID, WEATHER_ID, DATE, MOOD_SCORE, ACTIVITY_TYPE, ACTIVITY_DURATION, ENERGY_LEVEL, NOTES);
    }

    @Test
    void constructor_withValidInputs_createsHealthEntrySuccessfully(){
        assertNotNull(healthEntry);
    }

    @Test
    void getUserId_afterConstruction_returnsCorrectValue(){
        assertEquals(USER_ID, healthEntry.getUserId());
    }

    @Test
    void getWeatherId_afterConstruction_returnsCorrectValue(){
        assertEquals(WEATHER_ID, healthEntry.getWeatherId());
    }

    @Test
    void setUserId_withZero_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setUserId(0));
    }

    @Test
    void setUserId_withNegativeValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setUserId(-1));
    }

    @Test
    void setUserId_withPositiveValue_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setUserId(2));
        assertEquals(2, healthEntry.getUserId());
    }

    @Test
    void setWeatherId_withZero_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setWeatherId(0));
    }

    @Test
    void setWeatherId_withNegativeValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setWeatherId(-1));
    }

    @Test
    void setWeatherId_withPositiveValue_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setWeatherId(2));
        assertEquals(2, healthEntry.getWeatherId());
    }

    @Test
    void setMoodScore_withValueBelowLowerBoundValue1_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setMoodScore(0));
    }

    @Test
    void setMoodScore_withValueAboveUpperBoundValue5_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setMoodScore(6));
    }

    @Test
    void setMoodScore_withLowerValue1_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setMoodScore(1));
        assertEquals(1, healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withUpperValue5_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setMoodScore(5));
        assertEquals(5, healthEntry.getMoodScore());
    }

    @Test
    void setMoodScore_withValueBetweenBounds_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setMoodScore(4));
        assertEquals(4, healthEntry.getMoodScore());
    }

    @Test
    void setActivityType_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setActivityType(null));
    }

    @Test
    void setActivityType_withBlank_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setActivityType(""));
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setActivityType("  "));
    }

    @Test
    void setActivityType_withNone_setSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setActivityType("None"));
        assertEquals("None", healthEntry.getActivityType());
    }

    @Test
    void setActivityDuration_withNegativeValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setActivityDuration(-12.5));
    }

    @Test
    void setActivityType_withValueThatGoesBeyondADay_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setActivityDuration(25));
    }

    @Test
    void setActivityType_withValidValue_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setActivityDuration(23.5));
        assertEquals(23.5, healthEntry.getActivityDuration());
    }

    @Test
    void setEnergyLevel_withValueBelowLowerBoundValue1_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setEnergyLevel(0));
    }

    @Test
    void setEnergyLevel_withValueAboveUpperBoundValue5_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> healthEntry.setEnergyLevel(6));
    }

    @Test
    void setEnergyLevel_withLowerValue1_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setEnergyLevel(1));
        assertEquals(1, healthEntry.getEnergyLevel());
    }

    @Test
    void setEnergyLevel_withUpperValue5_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setEnergyLevel(5));
        assertEquals(5, healthEntry.getEnergyLevel());
    }

    @Test
    void setEnergyLevel_withValueBetweenBounds_setsSuccessfully(){
        assertDoesNotThrow(() -> healthEntry.setEnergyLevel(4));
        assertEquals(4, healthEntry.getEnergyLevel());
    }

    @Test
    void setNotes_withNull_setsSuccessfully(){
        healthEntry.setNotes(null);
        assertEquals("", healthEntry.getNotes());
    }

    @Test
    void setNotes_withGoodNotesText_setSuccessfully(){
        healthEntry.setNotes("Notes for testing");
        assertEquals("Notes for testing", healthEntry.getNotes());
    }

    @Test
    void equals_twoEntriesWithSameId_returnsTrue(){
        HealthEntry healthEntry1 = new HealthEntry(ENTRY_ID, USER_ID + 1, WEATHER_ID + 1, DATE, MOOD_SCORE, ACTIVITY_TYPE, ACTIVITY_DURATION, ENERGY_LEVEL, NOTES);
        assertEquals(healthEntry, healthEntry1);
    }

    @Test
    void equals_twoEntriesWithDifferentId_returnsTrue(){
        HealthEntry healthEntry1 = new HealthEntry(ENTRY_ID + 1, USER_ID + 1, WEATHER_ID + 1, DATE, MOOD_SCORE, ACTIVITY_TYPE, ACTIVITY_DURATION, ENERGY_LEVEL, NOTES);
        assertNotEquals(healthEntry, healthEntry1);
    }

    @Test
    void hashCode_twoEntriesWithSameId_returnTrue(){
        HealthEntry healthEntry1 = new HealthEntry(ENTRY_ID, USER_ID + 1, WEATHER_ID + 1, DATE, MOOD_SCORE, ACTIVITY_TYPE, ACTIVITY_DURATION, ENERGY_LEVEL, NOTES);
        assertEquals(healthEntry.hashCode(), healthEntry1.hashCode());
    }

}
