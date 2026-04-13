package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.repositories.HealthEntryRepository;
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
import java.util.concurrent.ThreadLocalRandom;

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
    private Label weatherCityLabel;
    @FXML
    private Label wellnessTipLabel;
    @FXML
    private Button weekBtn;
    @FXML
    private Button monthBtn;

    // Chart elements
    @FXML private Label day1Label, day2Label, day3Label, day4Label, day5Label, day6Label, day7Label;
    @FXML private Rectangle day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar;

    private HealthEntryRepository repository;
    private final WeatherService weatherService = new WeatherService();
    private boolean monthlyTrendMode = false;
    private int lastQuoteIndex = -1;

    private static final String[] WELLNESS_QUOTES = {
            "\"Take care of your body. It's the only place you have to live.\" - Jim Rohn",
            "\"Happiness is the highest form of health.\" - Dalai Lama",
            "\"To keep the body in good health is a duty.\" - Buddha",
            "\"Self-care is not a luxury, it is a priority.\" - Audre Lorde",
            "\"The greatest wealth is health.\" - Virgil",
            "\"A calm mind brings inner strength and self-confidence.\" - Dalai Lama"
    };

    @FXML
    public void initialize() {
        try {
            repository = new HealthEntryRepository(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            System.err.println("Dashboard: could not connect to database: " + e.getMessage());
        }
        refreshGreeting();
        refreshThemeStyles();
        refreshDashboardData();
    }

    public void refreshDashboardData() {
        new Thread(this::loadDashboardData).start();
    }

    public void refreshGreeting() {
        if (userNameLabel != null) {
            userNameLabel.setText(UserPreferences.getUserName());
        }
        if (weatherCityLabel != null) {
            String city = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getCity() : null;
            if (city == null || city.isBlank()) {
                city = UserPreferences.getCity();
            }
            weatherCityLabel.setText(city == null || city.isBlank() ? "SET CITY IN SETTINGS" : city.trim().toUpperCase());
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

        List<HealthEntry> recentEntries;
        try {
            recentEntries = repository != null
                    ? repository.findByUserId(UserSession.getCurrentUserId())
                    : List.of();
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
                moodLabel.setText(latest.getMoodScore());
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
                wellnessTipLabel.setText(nextWellnessQuote());
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
        } else if ("Stressed".equalsIgnoreCase(latest.getMoodScore())) {
            tip = "Feeling stressed? A 5-minute deep breathing exercise can help reset your focus.";
        } else {
            tip = "Great job maintaining your wellness! Consistency is the secret to long-term health.";
        }
        wellnessTipLabel.setText(tip + "\n\n" + nextWellnessQuote());
    }

    private String nextWellnessQuote() {
        if (WELLNESS_QUOTES.length == 0) {
            return "Keep going - your future self will thank you.";
        }
        int next = ThreadLocalRandom.current().nextInt(WELLNESS_QUOTES.length);
        if (WELLNESS_QUOTES.length > 1 && next == lastQuoteIndex) {
            next = (next + 1) % WELLNESS_QUOTES.length;
        }
        lastQuoteIndex = next;
        return WELLNESS_QUOTES[next];
    }

    private void updateWeeklyChart(List<HealthEntry> entries) {
        Label[] labels = {day1Label, day2Label, day3Label, day4Label, day5Label, day6Label, day7Label};
        Rectangle[] bars = {day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar};

        for (Rectangle bar : bars) bar.setHeight(10);
        for (Label label : labels) label.setText("-");

        // entries are DESC (index 0 = today). Fill bars right-to-left so newest is on the right.
        int count = Math.min(entries.size(), 7);
        for (int i = 0; i < count; i++) {
            HealthEntry entry = entries.get(i);
            int barIndex = 6 - i; // i=0 (today) → bar 6 (rightmost)
            bars[barIndex].setHeight(entry.getSleepHours() * 15);
            labels[barIndex].setText(entry.getEntryDate()
                    .getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
                    .toUpperCase());
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
        refreshDashboardData();
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
        Label[] labels = {day1Label, day2Label, day3Label, day4Label, day5Label, day6Label, day7Label};
        Rectangle[] bars = {day1Bar, day2Bar, day3Bar, day4Bar, day5Bar, day6Bar, day7Bar};
        int activeIndex = LocalDate.now().getDayOfWeek().getValue() - 1; // Monday=0 ... Sunday=6

        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != null) {
                labels[i].setStyle(i == activeIndex ? activeLabelStyle : mutedLabelStyle);
            }
            if (bars[i] != null) {
                bars[i].setOpacity(i == activeIndex ? 1.0 : (dark ? 0.35 : 0.2));
            }
        }
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

