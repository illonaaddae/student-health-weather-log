package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.TextStyle;
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

    @FXML
    public void initialize() {
        // Load mock data for the dashboard
        loadDashboardData();
    }

    /**
     * Loads mock wellness data into the dashboard components
     */
    private void loadDashboardData() {
        // Mock user data
        String userName = "Illona";
        userNameLabel.setText(userName);

        // Mock mood data
        moodLabel.setText("Calm");
        moodStatusLabel.setText("↗ Steady since 8 AM");

        // Mock sleep data
        sleepLabel.setText("7.5");
        sleepProgressBar.setProgress(0.75); // 7.5 / 10 hours

        // Mock water intake
        waterLabel.setText("1.8");

        // Mock exercise data
        exerciseLabel.setText("Active");

        // Mock weather data
        weatherConditionLabel.setText("Partly Cloudy");
        weatherTempLabel.setText("68");

    // Mock wellness tip
        wellnessTipLabel.setText("Your screen time is up by 15% today. Try a 5-minute deep breathing exercise to reset your focus before your next session.");
    }

    /**
     * Shows weekly trends in the activity chart
     */
    @FXML
    public void showWeeklyTrends() {
        System.out.println("Switching to weekly trends...");
        if (weekBtn != null) weekBtn.setStyle("-fx-background-color: #005faf; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15;");
        if (monthBtn != null) monthBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #70767a; -fx-font-size: 0.8em; -fx-padding: 5 15;");

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
        if (monthBtn != null) monthBtn.setStyle("-fx-background-color: #005faf; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 0.8em; -fx-padding: 5 15;");
        if (weekBtn != null) weekBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #70767a; -fx-font-size: 0.8em; -fx-padding: 5 15;");

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

