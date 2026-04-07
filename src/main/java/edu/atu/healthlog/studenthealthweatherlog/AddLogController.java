package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.HealthLogRepository;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.services.WeatherService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * AddLogController - Manages the "Add Today's Log" view.
 * Handles form inputs for mood, sleep, water, and exercise logging.
 */
public class AddLogController {

    @FXML
    private Button moodLowBtn;
    @FXML
    private Button moodNeutralBtn;
    @FXML
    private Button moodGoodBtn;
    @FXML
    private Button moodGreatBtn;
    @FXML
    private Button moodTiredBtn;
    @FXML
    private Spinner<Integer> sleepSpinner;
    @FXML
    private Spinner<Integer> waterSpinner;
    @FXML
    private ComboBox<String> activityComboBox;
    @FXML
    private Spinner<Integer> durationSpinner;
    @FXML
    private Button saveBtn;
    @FXML
    private javafx.scene.image.ImageView hydrationImage;

    private final HealthLogRepository repository = new HealthLogRepository();
    private final WeatherService weatherService = new WeatherService();
    private String selectedMood = "Good"; // Default mood

    @FXML
    public void initialize() {
        setupSpinners();
        setupActivityComboBox();
        setMoodButtonDefault();
    }

    /**
     * Sets up spinner configurations for numeric inputs
     */
    private void setupSpinners() {
        // Sleep spinner
        SpinnerValueFactory<Integer> sleepFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 8);
        sleepSpinner.setValueFactory(sleepFactory);

        // Water spinner
        SpinnerValueFactory<Integer> waterFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000, 2000, 100);
        waterSpinner.setValueFactory(waterFactory);

        // Duration spinner
        SpinnerValueFactory<Integer> durationFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 240, 30, 5);
        durationSpinner.setValueFactory(durationFactory);
    }

    /**
     * Populates the activity type dropdown with options
     */
    private void setupActivityComboBox() {
        activityComboBox.getItems().addAll(
                "Select Activity Type",
                "Walking",
                "Running",
                "Yoga",
                "Swimming",
                "Cycling",
                "Strength Training",
                "Sports",
                "Other"
        );
        activityComboBox.setValue("Select Activity Type");
    }

    /**
     * Sets the "Good" mood button as selected by default
     */
    private void setMoodButtonDefault() {
        moodGoodBtn.setStyle("-fx-background-color: #d4e3ff; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em; -fx-border-color: #005faf; -fx-border-width: 2;");
    }

    /**
     * Handles mood selection
     */
    @FXML
    public void selectMood(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String mood = resolveMood(clickedButton);
        selectedMood = mood;

        // Update button styles
        resetMoodButtons();
        clickedButton.setStyle("-fx-background-color: #d4e3ff; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em; -fx-border-color: #005faf; -fx-border-width: 2;");
    }

    private String resolveMood(Button clickedButton) {
        if (clickedButton == moodLowBtn) {
            return "Low";
        }
        if (clickedButton == moodNeutralBtn) {
            return "Neutral";
        }
        if (clickedButton == moodGoodBtn) {
            return "Good";
        }
        if (clickedButton == moodGreatBtn) {
            return "Great";
        }
        if (clickedButton == moodTiredBtn) {
            return "Tired";
        }

        String text = clickedButton.getText();
        return text == null ? "Good" : text.trim();
    }

    /**
     * Resets all mood button styles to default
     */
    private void resetMoodButtons() {
        moodLowBtn.setStyle("-fx-background-color: #f2f4f4; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em;");
        moodNeutralBtn.setStyle("-fx-background-color: #f2f4f4; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em;");
        moodGoodBtn.setStyle("-fx-background-color: #f2f4f4; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em;");
        moodGreatBtn.setStyle("-fx-background-color: #f2f4f4; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em;");
        moodTiredBtn.setStyle("-fx-background-color: #f2f4f4; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em;");
    }

    /**
     * Handles saving the daily log entry to the database
     */
    @FXML
    public void saveLogEntry() {
        Integer sleepHours = sleepSpinner.getValue();
        Integer waterMl = waterSpinner.getValue();
        String activity = activityComboBox.getValue();
        Integer duration = durationSpinner.getValue();

        // Fetch current weather to associate with this log
        WeatherService.WeatherData weather = weatherService.getCurrentWeather();

        // Create a real health entry object
        HealthEntry entry = new HealthEntry();
        entry.setUserId(1); // Mock user ID
        entry.setEntryDate(LocalDate.now());
        entry.setMood(selectedMood);
        entry.setSleepHours(sleepHours);
        entry.setWaterIntake(waterMl / 1000.0); // Convert ml to liters
        entry.setExercise(activity + " (" + duration + " min)");
        entry.setWeatherCondition(weather.condition);
        entry.setTemperature(weather.temp);

        System.out.println("Saving log entry to database: " + entry);

        // Save in background
        new Thread(() -> {
            try {
                repository.save(entry);
                System.out.println("Log entry saved successfully to DB!");
                
                Platform.runLater(() -> {
                    if (MainController.getInstance() != null) {
                        MainController.getInstance().switchToDashboard();
                    }
                });
            } catch (SQLException e) {
                System.err.println("Failed to save log entry: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Simple data class for log entries (TODO: move to separate file)
     */
    public static class LogEntry {
        private String mood;
        private int sleep;
        private int water;
        private String activity;
        private int duration;

        public void setMood(String mood) { this.mood = mood; }
        public void setSleep(int sleep) { this.sleep = sleep; }
        public void setWater(int water) { this.water = water; }
        public void setActivity(String activity) { this.activity = activity; }
        public void setDuration(int duration) { this.duration = duration; }

        @Override
        public String toString() {
            return "LogEntry{" +
                    "mood='" + mood + '\'' +
                    ", sleep=" + sleep +
                    ", water=" + water +
                    ", activity='" + activity + '\'' +
                    ", duration=" + duration +
                    '}';
        }
    }
}

