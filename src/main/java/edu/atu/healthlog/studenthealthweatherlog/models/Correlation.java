package edu.atu.healthlog.studenthealthweatherlog.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a computed correlation record linking a health log entry to the
 * weather conditions on that day. The correlationScore (0–5) is a weighted
 * wellness measure (mood 40%, sleep 30%, water 20%, activity 10%).
 */
public class Correlation {

    private int id;
    private int userId;
    private int healthEntryId;
    private LocalDate entryDate;
    private String weatherCondition;
    private double temperature;
    private int moodNumeric;        // Low=1, Tired=2, Neutral=3, Good=4, Great=5
    private double correlationScore; // 0.0 – 5.0

    public Correlation() {}

    public Correlation(int userId, int healthEntryId, LocalDate entryDate,
                       String weatherCondition, double temperature,
                       int moodNumeric, double correlationScore) {
        this.userId = userId;
        this.healthEntryId = healthEntryId;
        this.entryDate = entryDate;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
        this.moodNumeric = moodNumeric;
        this.correlationScore = correlationScore;
    }

    // ── id ────────────────────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // ── userId ────────────────────────────────────────────────────────────────

    public int getUserId() { return userId; }
    public void setUserId(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("userId must be positive");
        this.userId = userId;
    }

    // ── healthEntryId ─────────────────────────────────────────────────────────

    public int getHealthEntryId() { return healthEntryId; }
    public void setHealthEntryId(int healthEntryId) {
        if (healthEntryId <= 0) throw new IllegalArgumentException("healthEntryId must be positive");
        this.healthEntryId = healthEntryId;
    }

    // ── entryDate ─────────────────────────────────────────────────────────────

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) {
        if (entryDate == null) throw new IllegalArgumentException("entryDate cannot be null");
        if (entryDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("entryDate cannot be in the future");
        this.entryDate = entryDate;
    }

    // ── weatherCondition ──────────────────────────────────────────────────────

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) {
        if (weatherCondition == null || weatherCondition.isBlank())
            throw new IllegalArgumentException("weatherCondition cannot be blank");
        this.weatherCondition = weatherCondition;
    }

    // ── temperature ───────────────────────────────────────────────────────────

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    // ── moodNumeric ───────────────────────────────────────────────────────────

    public int getMoodNumeric() { return moodNumeric; }
    public void setMoodNumeric(int moodNumeric) {
        if (moodNumeric < 1 || moodNumeric > 5)
            throw new IllegalArgumentException("moodNumeric must be between 1 and 5");
        this.moodNumeric = moodNumeric;
    }

    // ── correlationScore ──────────────────────────────────────────────────────

    public double getCorrelationScore() { return correlationScore; }
    public void setCorrelationScore(double correlationScore) {
        if (correlationScore < 0 || correlationScore > 5)
            throw new IllegalArgumentException("correlationScore must be between 0 and 5");
        this.correlationScore = correlationScore;
    }

    // ── equals / hashCode ─────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Correlation that = (Correlation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
