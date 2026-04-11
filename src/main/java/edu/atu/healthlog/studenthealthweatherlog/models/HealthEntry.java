package edu.atu.healthlog.studenthealthweatherlog.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class HealthEntry {
    private static final Set<String> VALID_MOODS = Set.of("Low", "Neutral", "Good", "Great", "Tired");

    private int id;
    private int userId;
    private LocalDate entryDate;
    private String moodScore;
    private double sleepHours;
    private double waterIntake;
    private String exercise;
    private String weatherCondition;
    private double temperature;
    private String notes;

    public HealthEntry() {}

    public HealthEntry(int userId, LocalDate entryDate, String moodScore,
                       double sleepHours, double waterIntake,
                       String exercise, String notes) {
        this.userId = userId;
        this.entryDate = entryDate;
        this.moodScore = moodScore;
        this.sleepHours = sleepHours;
        this.waterIntake = waterIntake;
        this.exercise = exercise;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("User id must be positive");
        this.userId = userId;
    }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) {
        if (entryDate == null) throw new IllegalArgumentException("Entry date cannot be null");
        if (entryDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Entry date cannot be in the future");
        this.entryDate = entryDate;
    }

    public String getMoodScore() { return moodScore; }
    public void setMoodScore(String moodScore) {
        if (moodScore == null || !VALID_MOODS.contains(moodScore))
            throw new IllegalArgumentException("Mood must be one of: Low, Neutral, Good, Great, Tired");
        this.moodScore = moodScore;
    }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) {
        if (sleepHours < 0) throw new IllegalArgumentException("Sleep hours cannot be negative");
        if (sleepHours > 24) throw new IllegalArgumentException("Sleep hours cannot exceed 24");
        this.sleepHours = sleepHours;
    }

    public double getWaterIntake() { return waterIntake; }
    public void setWaterIntake(double waterIntake) {
        if (waterIntake < 0) throw new IllegalArgumentException("Water intake cannot be negative");
        this.waterIntake = waterIntake;
    }

    public String getExercise() { return exercise; }
    public void setExercise(String exercise) {
        if (exercise == null) throw new IllegalArgumentException("Exercise cannot be null");
        this.exercise = exercise;
    }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = (notes == null) ? "" : notes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HealthEntry that = (HealthEntry) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
