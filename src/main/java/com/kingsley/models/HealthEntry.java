package com.kingsley.models;

import java.time.LocalDate;
import java.util.Objects;

public class HealthEntry {
    private int entryId;
    private int userId;
    private int weatherId;
    private LocalDate date;
    private int moodScore;
//    private double sleepHours;
    private String activityType;
    private double activityDuration;
    private int energyLevel;
//    private double waterIntake;
    private String notes;

    public HealthEntry(int entryId, int userId, int weatherId, LocalDate date, int moodScore, String activityType, double activityDuration, int energyLevel, String notes) {
        this.entryId = entryId;
        this.userId = userId;
        this.weatherId = weatherId;
        this.date = date;
        this.moodScore = moodScore;
        this.activityType = activityType;
        this.activityDuration = activityDuration;
        this.energyLevel = energyLevel;
        this.notes = notes;
    }

    public int getEntryId() {
        return this.entryId;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getWeatherId() {
        return this.weatherId;
    }

    public void setUserId(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("User's id cannot be zero or less");
        this.userId = userId;
    }

    public void setWeatherId(int weatherId) {
        if (weatherId <= 0) throw new IllegalArgumentException("Weather id cannot be zero or less");
        this.weatherId = weatherId;
    }

    public void setMoodScore(int moodScore) {
        if (moodScore < 1 || moodScore > 5) throw new IllegalArgumentException("Mood score must be 1, 5 or any value between");
        this.moodScore = moodScore;
    }

    public int getMoodScore() {
        return this.moodScore;
    }

    public void setActivityType(String activityType) {
        if (activityType == null) throw new IllegalArgumentException("Activity type cannot be null");
        if (activityType.isBlank()) throw new IllegalArgumentException("Activity type cannot be empty");
        this.activityType = activityType;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public void setActivityDuration(double duration) {
        if (duration < 0.0) throw new IllegalArgumentException("Activity cannot be a negative number");
        if (duration > 24) throw new IllegalArgumentException("Activity duration cannot be more than a day(24hours)");
        this.activityDuration = duration;
    }

    public double getActivityDuration() {
        return this.activityDuration;
    }

    public void setEnergyLevel(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("Energy level must be 1, 5 or any value between");
        this.energyLevel = level;
    }

    public int getEnergyLevel() {
        return this.energyLevel;
    }

    public void setNotes(String notes) {
        if (notes == null) this.notes = "";
        else this.notes = notes;
    }

    public String getNotes() {
        return this.notes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HealthEntry that = (HealthEntry) o;
        return entryId == that.entryId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entryId);
    }
}
