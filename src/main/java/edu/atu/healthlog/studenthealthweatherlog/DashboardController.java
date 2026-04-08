package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.HealthLogRepository;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.services.WeatherService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * DashboardController - Manages the dashboard view displaying daily wellness metrics.
 * Shows mood, sleep, water intake, exercise, and weather information.
 */
public class DashboardController {

    @FXML
    private Label userNameLabel;
    @FXML
    private Label moodLabel;
    @FXML
    private Label moodStatusLabel;
    @FXML
    private Label sleepLabel;
    @FXML
    private ProgressBar sleepProgressBar;
    @FXML
    private Label waterLabel;
    @FXML
    private Label exerciseLabel;
    @FXML
    private Label weatherConditionLabel;
    @FXML
    private Label weatherTempLabel;
    @FXML
    private Label wellnessTipLabel;
    @FXML
    private Button weekBtn;
    @FXML
    private Button monthBtn;

    // Chart elements
    @FXML private Label day1Label, day2Label, day3Label, day4Label, day5Label, day6Label, day7Label;
    @FXML private Rectangle day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar;

    private final HealthLogRepository repository = new HealthLogRepository();
    private final WeatherService weatherService = new WeatherService();
    private boolean monthlyTrendMode = false;

    @FXML
    public void initialize() {
        refreshGreeting();
        refreshThemeStyles();
        // Load real data in a background thread to keep UI responsive
        new Thread(this::loadDashboardData).start();
    }

    public void refreshGreeting() {
        if (userNameLabel != null) {
            userNameLabel.setText(UserPreferences.getUserName());
        }
    }

    public void refreshThemeStyles() {
        if (monthlyTrendMode) {
            applyMonthlyTrendStyles();
        } else {
            applyWeeklyTrendStyles();
        }
    }

    /**
     * Loads real wellness data and current weather into the dashboard components
     */
    private void loadDashboardData() {
        // Fetch weather data
        WeatherService.WeatherData weather = weatherService.getCurrentWeather();

        // Fetch user data from DB (mocking user ID 1 for now)
        int currentUserId = 1; 
        List<HealthEntry> recentEntries;
        try {
            recentEntries = repository.getAllByUserId(currentUserId);
        } catch (SQLException e) {
            System.err.println("Failed to load entries: " + e.getMessage());
            recentEntries = List.of();
        }

        final List<HealthEntry> entries = recentEntries;

        // Update UI on JavaFX Application Thread
        Platform.runLater(() -> {
            refreshGreeting();

            // Update Weather UI
            weatherConditionLabel.setText(weather.condition);
            weatherTempLabel.setText(String.format("%.0f", weather.temp));

            if (!entries.isEmpty()) {
                HealthEntry latest = entries.get(0);
                moodLabel.setText(latest.getMood());
                sleepLabel.setText(String.format("%.1f", latest.getSleepHours()));
                sleepProgressBar.setProgress(Math.min(latest.getSleepHours() / 10.0, 1.0));
                waterLabel.setText(String.format("%.1f", latest.getWaterIntake()));
                exerciseLabel.setText(latest.getExercise());
                moodStatusLabel.setText("Last updated: " + latest.getEntryDate());
                
                // Update wellness tip based on latest data
                updateWellnessTip(latest);
            } else {
                // Default if no entries
                moodLabel.setText("No data");
                sleepLabel.setText("0");
                sleepProgressBar.setProgress(0);
                waterLabel.setText("0");
                exerciseLabel.setText("None");
                moodStatusLabel.setText("Add your first log today!");
            }

            // Update chart with recent 7 days
            updateWeeklyChart(entries);
        });
    }

    private void updateWellnessTip(HealthEntry latest) {
        String tip;
        if (latest.getSleepHours() < 6) {
            tip = "You slept less than 6 hours. Try to avoid caffeine this afternoon and aim for an earlier bedtime tonight.";
        } else if (latest.getWaterIntake() < 2.0) {
            tip = "Staying hydrated is key to focus! You've logged " + latest.getWaterIntake() + "L. Try to reach 2.5L today.";
        } else if ("Stressed".equalsIgnoreCase(latest.getMood())) {
            tip = "Feeling stressed? A 5-minute deep breathing exercise can help reset your focus.";
        } else {
            tip = "Great job maintaining your wellness! Consistency is the secret to long-term health.";
        }
        wellnessTipLabel.setText(tip);
    }

    private void updateWeeklyChart(List<HealthEntry> entries) {
        // Simplified: set bar heights based on sleep hours of last 7 entries
        Rectangle[] bars = {day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar};
        // Reset bars first
        for (Rectangle bar : bars) bar.setHeight(10);

        for (int i = 0; i < Math.min(entries.size(), 7); i++) {
            HealthEntry entry = entries.get(entries.size() - 1 - i); // Oldest to newest
            int barIndex = 6 - i; // Fill from right
            if (barIndex >= 0) {
                bars[barIndex].setHeight(entry.getSleepHours() * 15); // Scale 1h = 15px
            }
        }
    }

    /**
     * Shows weekly trends in the activity chart
     */
    @FXML
    public void showWeeklyTrends() {
        System.out.println("Switching to weekly trends...");
        monthlyTrendMode = false;
        applyWeeklyTrendStyles();

        // Update labels for Week
        day1Label.setText("MON");
        day2Label.setText("TUE");
        day3Label.setText("WED");
        day4Label.setText("THU");
        day5Label.setText("FRI");
        day6Label.setText("SAT");
        day7Label.setText("SUN");

        // Restore active day highlighting (e.g., THU as in mock)
        day4Label.setStyle("-fx-text-fill: #005faf;");
        day4Bar.setOpacity(1.0);
        
        // Reset others (simple mock logic)
        Label[] labels = {day1Label, day2Label, day3Label, day5Label, day6Label, day7Label};
        Rectangle[] bars = {day1Bar, day2Bar, day3Bar, day5Bar, day6Bar, day7Bar};
        for (Label l : labels) l.setStyle("-fx-text-fill: #70767a;");
        for (Rectangle b : bars) b.setOpacity(0.2);

        // Reset bar heights to mock weekly data
        day1Bar.setHeight(60);
        day2Bar.setHeight(100);
        day3Bar.setHeight(75);
        day4Bar.setHeight(130);
        day5Bar.setHeight(65);
        day6Bar.setHeight(45);
        day7Bar.setHeight(30);
    }

    /**
     * Shows monthly trends in the activity chart
     */
    @FXML
    public void showMonthlyTrends() {
        System.out.println("Switching to monthly trends...");
        monthlyTrendMode = true;
        applyMonthlyTrendStyles();

        // Update labels for Month - showing last 7 months
        Label[] labels = {day1Label, day2Label, day3Label, day4Label, day5Label, day6Label, day7Label};
        LocalDate date = LocalDate.now();
        
        for (int i = 0; i < 7; i++) {
            // Get month name for (6-i) months ago
            LocalDate targetDate = date.minusMonths(6 - i);
            String monthName = targetDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
            labels[i].setText(monthName);
        }

        // Mock monthly data visualization
        day1Bar.setHeight(80);
        day2Bar.setHeight(95);
        day3Bar.setHeight(110);
        day4Bar.setHeight(85);
        day5Bar.setHeight(125);
        day6Bar.setHeight(90);
        day7Bar.setHeight(115);

        // Reset highlighting for month view
        Rectangle[] bars = {day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar};
        for (Label l : labels) l.setStyle("-fx-text-fill: #70767a;");
        for (Rectangle b : bars) b.setOpacity(0.4); // More solid for monthly averages
        
        // Highlight current month (the last one)
        day7Label.setStyle("-fx-text-fill: #005faf;");
        day7Bar.setOpacity(0.8);
    }

    private void applyWeeklyTrendStyles() {
        boolean dark = UserPreferences.isDarkThemeEnabled();
        if (weekBtn != null) {
            weekBtn.setStyle(dark
                    ? "-fx-background-color: #7f94b3; -fx-text-fill: #0d0f11; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15; -fx-font-weight: bold;"
                    : "-fx-background-color: #005faf; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15; -fx-font-weight: bold;");
        }
        if (monthBtn != null) {
            monthBtn.setStyle(dark
                    ? "-fx-background-color: transparent; -fx-text-fill: #c4c6ca; -fx-font-size: 0.8em; -fx-padding: 5 15;"
                    : "-fx-background-color: transparent; -fx-text-fill: #70767a; -fx-font-size: 0.8em; -fx-padding: 5 15;");
        }

        String mutedLabelStyle = dark
                ? "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #c4c6ca;"
                : "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #70767a;";
        String activeLabelStyle = dark
                ? "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #d4e3ff;"
                : "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #005faf;";
        if (day1Label != null) day1Label.setStyle(mutedLabelStyle);
        if (day2Label != null) day2Label.setStyle(mutedLabelStyle);
        if (day3Label != null) day3Label.setStyle(mutedLabelStyle);
        if (day4Label != null) day4Label.setStyle(activeLabelStyle);
        if (day5Label != null) day5Label.setStyle(mutedLabelStyle);
        if (day6Label != null) day6Label.setStyle(mutedLabelStyle);
        if (day7Label != null) day7Label.setStyle(mutedLabelStyle);

        if (day1Bar != null) day1Bar.setOpacity(dark ? 0.35 : 0.2);
        if (day2Bar != null) day2Bar.setOpacity(dark ? 0.35 : 0.2);
        if (day3Bar != null) day3Bar.setOpacity(dark ? 0.35 : 0.2);
        if (day4Bar != null) day4Bar.setOpacity(1.0);
        if (day5Bar != null) day5Bar.setOpacity(dark ? 0.35 : 0.2);
        if (day6Bar != null) day6Bar.setOpacity(dark ? 0.35 : 0.2);
        if (day7Bar != null) day7Bar.setOpacity(dark ? 0.35 : 0.2);
    }

    private void applyMonthlyTrendStyles() {
        boolean dark = UserPreferences.isDarkThemeEnabled();
        if (monthBtn != null) {
            monthBtn.setStyle(dark
                    ? "-fx-background-color: #7f94b3; -fx-text-fill: #0d0f11; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15; -fx-font-weight: bold;"
                    : "-fx-background-color: #005faf; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15; -fx-font-weight: bold;");
        }
        if (weekBtn != null) {
            weekBtn.setStyle(dark
                    ? "-fx-background-color: transparent; -fx-text-fill: #c4c6ca; -fx-font-size: 0.8em; -fx-padding: 5 15;"
                    : "-fx-background-color: transparent; -fx-text-fill: #70767a; -fx-font-size: 0.8em; -fx-padding: 5 15;");
        }

        String mutedLabelStyle = dark
                ? "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #c4c6ca;"
                : "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #70767a;";
        if (day1Label != null) day1Label.setStyle(mutedLabelStyle);
        if (day2Label != null) day2Label.setStyle(mutedLabelStyle);
        if (day3Label != null) day3Label.setStyle(mutedLabelStyle);
        if (day4Label != null) day4Label.setStyle(mutedLabelStyle);
        if (day5Label != null) day5Label.setStyle(mutedLabelStyle);
        if (day6Label != null) day6Label.setStyle(mutedLabelStyle);
        if (day7Label != null) day7Label.setStyle(dark
                ? "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #d4e3ff;"
                : "-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #005faf;");

        if (day1Bar != null) day1Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day2Bar != null) day2Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day3Bar != null) day3Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day4Bar != null) day4Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day5Bar != null) day5Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day6Bar != null) day6Bar.setOpacity(dark ? 0.55 : 0.4);
        if (day7Bar != null) day7Bar.setOpacity(dark ? 0.85 : 0.8);
    }

    /**
     * Starts a wellness reset session
     */
    @FXML
    public void startWellnessReset() {
        System.out.println("Starting wellness reset...");
        if (MainController.getInstance() != null) {
            MainController.getInstance().switchToAddLog();
        }
    }

    /**
     * Navigates to the add log screen
     */
    @FXML
    public void addNewLog() {
        System.out.println("Navigating to Add Log...");
        if (MainController.getInstance() != null) {
            MainController.getInstance().switchToAddLog();
        }
    }
}

