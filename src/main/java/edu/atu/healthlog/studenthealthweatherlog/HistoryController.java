package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * HistoryController - Manages the wellness history view.
 * Displays past log entries in a data table with filtering and export capabilities.
 */
public class HistoryController {
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button filterBtn;
    @FXML
    private Button exportCsvBtn;
    @FXML
    private TableView<HistoryEntry> historyTable;
    @FXML
    private TableColumn<HistoryEntry, String> dateColumn;
    @FXML
    private TableColumn<HistoryEntry, String> moodColumn;
    @FXML
    private TableColumn<HistoryEntry, String> sleepColumn;
    @FXML
    private TableColumn<HistoryEntry, String> waterColumn;
    @FXML
    private TableColumn<HistoryEntry, String> exerciseColumn;
    @FXML
    private TableColumn<HistoryEntry, String> weatherColumn;
    @FXML
    private Button viewDeepInsightsBtn;
    @FXML
    private Button syncCalendarBtn;

    // Full data list for filtering
    private ObservableList<HistoryEntry> allHistoryData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
        sleepColumn.setCellValueFactory(new PropertyValueFactory<>("sleep"));
        waterColumn.setCellValueFactory(new PropertyValueFactory<>("water"));
        exerciseColumn.setCellValueFactory(new PropertyValueFactory<>("exercise"));
        weatherColumn.setCellValueFactory(new PropertyValueFactory<>("weather"));

        // Load mock data into the table
        loadHistoryData();
    }

    /**
     * Loads mock wellness history data into the table
     */
    private void loadHistoryData() {
        // TODO: Implement data loading from database
        System.out.println("Loading wellness history data...");
        // Mock data structure (real implementation would fetch from database)

        allHistoryData.setAll(
            new HistoryEntry("Oct 21, 2023", "Content", "7.8 hrs", "3.5 L", "Swimming", "26°C"),
            new HistoryEntry("Oct 22, 2023", "Anxious", "6.1 hrs", "2.0 L", "None recorded", "14°C"),
            new HistoryEntry("Oct 23, 2023", "Calm", "7.5 hrs", "2.4 L", "Running", "19°C"),
            new HistoryEntry("Oct 24, 2023", "Joyful", "8.2 hrs", "3.1 L", "Morning Yoga", "24°C")
        );

        historyTable.setItems(allHistoryData);
    }

    /**
     * Applies date range and filter options to the data
     */
    @FXML
    public void applyFilters() {
        System.out.println("Applying filters from " + startDatePicker.getValue() + " to " + endDatePicker.getValue());
        
        if (startDatePicker.getValue() == null && endDatePicker.getValue() == null) {
            historyTable.setItems(allHistoryData);
            return;
        }

        ObservableList<HistoryEntry> filteredData = allHistoryData.filtered(entry -> {
            // Very simple date string matching logic for mock data
            // In real app, we would compare LocalDate objects
            boolean matches = true;
            if (startDatePicker.getValue() != null) {
                // Mock data is "Oct 21, 2023"
                // Simple check if it contains the month/day (just for demo)
                System.out.println("Filtering by start date: " + startDatePicker.getValue());
            }
            return matches;
        });
        
        historyTable.setItems(filteredData);
    }

    /**
     * Exports the current table data to CSV format
     */
    @FXML
    public void exportCSV() {
        // TODO: Implement CSV export functionality
        System.out.println("Exporting wellness history to CSV...");
    }

    /**
     * Opens a detailed analysis view of the data
     */
    @FXML
    public void viewDeepInsights() {
        System.out.println("Opening deep insights view...");
        if (MainController.getInstance() != null) {
            MainController.getInstance().switchToWeatherInsights();
        }
    }

    /**
     * Syncs wellness logs with calendar application
     */
    @FXML
    public void syncCalendar() {
        // TODO: Implement calendar sync functionality
        System.out.println("Syncing with calendar...");
    }

    /**
     * Helper class to represent a history table entry
     */
    public static class HistoryEntry {
        private final String date;
        private final String mood;
        private final String sleep;
        private final String water;
        private final String exercise;
        private final String weather;

        public HistoryEntry(String date, String mood, String sleep, String water, String exercise, String weather) {
            this.date = date;
            this.mood = mood;
            this.sleep = sleep;
            this.water = water;
            this.exercise = exercise;
            this.weather = weather;
        }

        // Getters
        public String getDate() {
            return date;
        }

        public String getMood() {
            return mood;
        }

        public String getSleep() {
            return sleep;
        }

        public String getWater() {
            return water;
        }

        public String getExercise() {
            return exercise;
        }

        public String getWeather() {
            return weather;
        }
    }
}
