package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * SettingsController - Manages the settings and preferences view.
 * Handles user account settings, notifications, wellness goals, and privacy options.
 */
public class SettingsController {

    @FXML
    private CheckBox dailyReminders;
    @FXML
    private CheckBox weatherAlerts;
    @FXML
    private CheckBox weeklyInsights;
    @FXML
    private CheckBox goalReminders;
    @FXML
    private Spinner<Integer> targetSleepSpinner;
    @FXML
    private Spinner<Integer> targetWaterSpinner;
    @FXML
    private Spinner<Integer> targetExerciseSpinner;
    @FXML
    private CheckBox dataSharing;
    @FXML
    private Button saveBtn;

    @FXML
    public void initialize() {
        setupSpinners();
        loadSettings();
    }

    /**
     * Sets up spinner configurations for wellness goals
     */
    private void setupSpinners() {
        // Target sleep spinner
        SpinnerValueFactory<Integer> sleepFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 8);
        targetSleepSpinner.setValueFactory(sleepFactory);

        // Target water spinner
        SpinnerValueFactory<Integer> waterFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, 5000, 2500, 100);
        targetWaterSpinner.setValueFactory(waterFactory);

        // Target exercise spinner
        SpinnerValueFactory<Integer> exerciseFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 240, 30, 5);
        targetExerciseSpinner.setValueFactory(exerciseFactory);
    }

    /**
     * Loads user settings from preferences (mock implementation)
     */
    private void loadSettings() {
        // TODO: Load actual user settings from database/preferences file
        // Mock values
        dailyReminders.setSelected(true);
        weatherAlerts.setSelected(true);
        weeklyInsights.setSelected(true);
        goalReminders.setSelected(false);
        dataSharing.setSelected(false);
    }

    /**
     * Edits user profile information
     */
    @FXML
    public void editProfile() {
        System.out.println("Opening profile editor...");
        // TODO: Open profile editor dialog
    }

    /**
     * Opens change password dialog
     */
    @FXML
    public void changePassword() {
        System.out.println("Opening password change dialog...");
        // TODO: Implement password change dialog
    }

    /**
     * Exports user data as JSON/CSV
     */
    @FXML
    public void exportData() {
        System.out.println("Exporting user data...");
        // TODO: Implement data export functionality
    }

    /**
     * Permanently deletes the user account
     */
    @FXML
    public void deleteAccount() {
        System.out.println("Deleting account...");
        // TODO: Show confirmation dialog and implement account deletion
    }

    /**
     * Saves all settings changes
     */
    @FXML
    public void saveChanges() {
        System.out.println("Saving settings...");

        // Collect all settings
        boolean dailyRemindersEnabled = dailyReminders.isSelected();
        boolean weatherAlertsEnabled = weatherAlerts.isSelected();
        boolean weeklyInsightsEnabled = weeklyInsights.isSelected();
        boolean goalRemindersEnabled = goalReminders.isSelected();
        int targetSleep = targetSleepSpinner.getValue();
        int targetWater = targetWaterSpinner.getValue();
        int targetExercise = targetExerciseSpinner.getValue();
        boolean dataSharingEnabled = dataSharing.isSelected();

        // TODO: Persist settings to database/preferences
        System.out.println("Settings saved successfully!");
        System.out.println("  Daily Reminders: " + dailyRemindersEnabled);
        System.out.println("  Weather Alerts: " + weatherAlertsEnabled);
        System.out.println("  Weekly Insights: " + weeklyInsightsEnabled);
        System.out.println("  Goal Reminders: " + goalRemindersEnabled);
        System.out.println("  Target Sleep: " + targetSleep + " hours");
        System.out.println("  Target Water: " + targetWater + " ml");
        System.out.println("  Target Exercise: " + targetExercise + " mins/day");
        System.out.println("  Data Sharing: " + dataSharingEnabled);
    }

    /**
     * Opens contact support dialog or email
     */
    @FXML
    public void contactSupport() {
        System.out.println("Opening support contact...");
        // TODO: Implement support contact functionality
    }

    /**
     * Opens user guide documentation
     */
    @FXML
    public void openUserGuide() {
        System.out.println("Opening user guide...");
        // TODO: Implement user guide dialog or web view
    }

    /**
     * Opens terms of service
     */
    @FXML
    public void openTerms() {
        System.out.println("Opening terms of service...");
        // TODO: Show terms of service dialog
    }

    /**
     * Opens privacy policy
     */
    @FXML
    public void openPrivacy() {
        System.out.println("Opening privacy policy...");
        // TODO: Show privacy policy dialog
    }

    /**
     * Helper class for user settings
     */
    public static class UserSettings {
        private boolean dailyReminders;
        private boolean weatherAlerts;
        private boolean weeklyInsights;
        private boolean goalReminders;
        private int targetSleep;
        private int targetWater;
        private int targetExercise;
        private boolean dataSharing;

        public UserSettings(boolean dailyReminders, boolean weatherAlerts, boolean weeklyInsights,
                           boolean goalReminders, int targetSleep, int targetWater,
                           int targetExercise, boolean dataSharing) {
            this.dailyReminders = dailyReminders;
            this.weatherAlerts = weatherAlerts;
            this.weeklyInsights = weeklyInsights;
            this.goalReminders = goalReminders;
            this.targetSleep = targetSleep;
            this.targetWater = targetWater;
            this.targetExercise = targetExercise;
            this.dataSharing = dataSharing;
        }

        // Getters and setters...
        public boolean isDailyReminders() { return dailyReminders; }
        public void setDailyReminders(boolean dailyReminders) { this.dailyReminders = dailyReminders; }

        public boolean isWeatherAlerts() { return weatherAlerts; }
        public void setWeatherAlerts(boolean weatherAlerts) { this.weatherAlerts = weatherAlerts; }

        public boolean isWeeklyInsights() { return weeklyInsights; }
        public void setWeeklyInsights(boolean weeklyInsights) { this.weeklyInsights = weeklyInsights; }

        public boolean isGoalReminders() { return goalReminders; }
        public void setGoalReminders(boolean goalReminders) { this.goalReminders = goalReminders; }

        public int getTargetSleep() { return targetSleep; }
        public void setTargetSleep(int targetSleep) { this.targetSleep = targetSleep; }

        public int getTargetWater() { return targetWater; }
        public void setTargetWater(int targetWater) { this.targetWater = targetWater; }

        public int getTargetExercise() { return targetExercise; }
        public void setTargetExercise(int targetExercise) { this.targetExercise = targetExercise; }

        public boolean isDataSharing() { return dataSharing; }
        public void setDataSharing(boolean dataSharing) { this.dataSharing = dataSharing; }
    }
}

