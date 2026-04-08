package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * WeatherInsightsController - Manages the weather & wellness correlations view.
 * Displays charts and analytics showing how weather affects wellness metrics.
 */
public class WeatherInsightsController {

    @FXML
    private Button syncCalendarBtn;
    @FXML
    private Button viewGoalsBtn;

    @FXML
    private javafx.scene.shape.Rectangle sunnyBar;
    @FXML
    private javafx.scene.shape.Rectangle cloudyBar;
    @FXML
    private javafx.scene.shape.Rectangle rainyBar;
    @FXML
    private javafx.scene.shape.Rectangle snowBar;
    @FXML
    private javafx.scene.shape.Rectangle partlyBar;

    @FXML
    public void initialize() {
        // Initialize chart data and correlations
        // Use Platform.runLater to ensure layout is complete if needed,
        // but for simple height setting, initialize should work if elements are injected.
        loadWeatherInsights();
    }

    /**
     * Loads weather and wellness correlation data
     */
    private void loadWeatherInsights() {
        System.out.println("Loading weather & wellness correlation data...");
        // Set mock heights based on "real" data to ensure chart isn't blank
        // Scale: 200px = 5.0 Mood
        if (sunnyBar != null) sunnyBar.setHeight(168); // 4.2
        if (cloudyBar != null) cloudyBar.setHeight(140); // 3.5
        if (rainyBar != null) rainyBar.setHeight(112); // 2.8
        if (snowBar != null) snowBar.setHeight(76);   // 1.9
        if (partlyBar != null) partlyBar.setHeight(156); // 3.9
    }

    /**
     * Switches the mood impact chart to monthly view
     */
    @FXML
    public void switchToMonthlyView() {
        System.out.println("Switching to monthly view...");
        // TODO: Update chart data for monthly perspective
    }

    /**
     * Opens filter options for the correlation data
     */
    @FXML
    public void openFilter() {
        System.out.println("Opening filter menu...");
        // TODO: Implement filter dialog
    }

    /**
     * Shows detailed wellness insights based on weather patterns
     */
    @FXML
    public void viewGoals() {
        System.out.println("Opening sunlight goals...");
        // TODO: Navigate to sunlight goals screen
    }

    /**
     * Syncs wellness logs with calendar application
     */
    @FXML
    public void syncCalendar() {
        System.out.println("Syncing with calendar...");
        // TODO: Implement calendar sync functionality
    }

    /**
     * Helper class for weather correlation data
     */
    public static class WeatherCorrelation {
        private String weatherCondition;
        private int moodImpact;
        private int sleepQuality;
        private int activityLevel;
        private int anxietyFrequency;

        public WeatherCorrelation(String weatherCondition, int moodImpact,
                                  int sleepQuality, int activityLevel, int anxietyFrequency) {
            this.weatherCondition = weatherCondition;
            this.moodImpact = moodImpact;
            this.sleepQuality = sleepQuality;
            this.activityLevel = activityLevel;
            this.anxietyFrequency = anxietyFrequency;
        }

        // Getters
        public String getWeatherCondition() { return weatherCondition; }
        public int getMoodImpact() { return moodImpact; }
        public int getSleepQuality() { return sleepQuality; }
        public int getActivityLevel() { return activityLevel; }
        public int getAnxietyFrequency() { return anxietyFrequency; }
    }
}

