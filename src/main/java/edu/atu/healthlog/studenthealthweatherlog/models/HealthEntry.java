package edu.atu.healthlog.studenthealthweatherlog.models;

import java.time.LocalDate;

/**
 * HealthEntry model representing a daily wellness log.
 */
public class HealthEntry {
    private int id;
    private int userId;
    private LocalDate entryDate;
    private String mood;
    private double sleepHours;
    private double waterIntake;
    private String exercise;
    private String notes;
    private String weatherCondition;
    private double temperature;

    public HealthEntry() {}

    public HealthEntry(int userId, LocalDate entryDate, String mood, double sleepHours, 
                       double waterIntake, String exercise, String notes) {
        this.userId = userId;
        this.entryDate = entryDate;
        this.mood = mood;
        this.sleepHours = sleepHours;
        this.waterIntake = waterIntake;
        this.exercise = exercise;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
    public double getWaterIntake() { return waterIntake; }
    public void setWaterIntake(double waterIntake) { this.waterIntake = waterIntake; }
    public String getExercise() { return exercise; }
    public void setExercise(String exercise) { this.exercise = exercise; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
}
