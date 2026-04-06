package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

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
        String mood = clickedButton.getText().split("\n")[1].trim();
        selectedMood = mood;

        // Update button styles
        resetMoodButtons();
        clickedButton.setStyle("-fx-background-color: #d4e3ff; -fx-padding: 1em; -fx-background-radius: 0.75em; -fx-text-alignment: center; -fx-font-size: 0.85em; -fx-border-color: #005faf; -fx-border-width: 2;");
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
     * Handles saving the daily log entry
     */
    @FXML
    public void saveLogEntry() {
        Integer sleepHours = sleepSpinner.getValue();
        Integer waterMl = waterSpinner.getValue();
        String activity = activityComboBox.getValue();
        Integer duration = durationSpinner.getValue();

        // Create a log entry object (TODO: implement data persistence)
        LogEntry entry = new LogEntry();
        entry.setMood(selectedMood);
        entry.setSleep(sleepHours);
        entry.setWater(waterMl);
        entry.setActivity(activity);
        entry.setDuration(duration);

        System.out.println("Saving log entry: " + entry);
        // TODO: Save to database or file
        // Show success message
        System.out.println("Log entry saved successfully!");
        if (MainController.getInstance() != null) {
            MainController.getInstance().switchToDashboard();
        }
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

