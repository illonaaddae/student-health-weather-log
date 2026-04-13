package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.repositories.HealthEntryRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MainController - Manages the main application shell with navigation and screen swapping.
 * Implements MVC pattern for the application root view.
 */
public class MainController {
    private static MainController instance;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    private StackPane contentArea;
    @FXML
    private Label userNameLabel;
    @FXML
    private Circle userProfileCircle;
    @FXML
    private Label userProfileInitials;
    @FXML
    private TextField searchField;
    @FXML
    private Button notificationBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button dashboardBtn;
    @FXML
    private Button addLogBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private Button weatherInsightsBtn;
    @FXML
    private Button supportBtn;
    @FXML
    private Button signOutBtn;

    private static final String DASHBOARD_VIEW = "dashboard-view.fxml";
    private static final String ADD_LOG_VIEW = "add-log-view.fxml";
    private static final String HISTORY_VIEW = "history-view.fxml";
    private static final String WEATHER_INSIGHTS_VIEW = "weather-insights-view.fxml";
    private static final String SETTINGS_VIEW = "settings-view.fxml";

    private final Map<String, Parent> screenCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();

    @FXML
    public void initialize() {
        // Setup search field listener
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                handleSearch(newValue);
            });
        }

        refreshUserProfile();

        // Load the dashboard by default
        switchToDashboard();
    }

    public void refreshUserProfile() {
        String name = UserPreferences.getUserName();
        if (userNameLabel != null) {
            userNameLabel.setText(name);
        }

        if (userProfileInitials != null) {
            userProfileInitials.setText(buildInitials(name));
            userProfileInitials.setVisible(true);
        }

        String picturePath = UserPreferences.getCurrentUserProfilePicturePath();
        if (userProfileCircle != null) {
            if (picturePath != null) {
                java.io.File file = new java.io.File(picturePath);
                if (file.exists()) {
                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString());
                        userProfileCircle.setFill(new ImagePattern(img));
                        if (userProfileInitials != null) userProfileInitials.setVisible(false);
                    } catch (Exception e) {
                        userProfileCircle.setFill(javafx.scene.paint.Color.web("#d4e3ff"));
                    }
                } else {
                    UserPreferences.setCurrentUserProfilePicturePath(null);
                    userProfileCircle.setFill(javafx.scene.paint.Color.web("#d4e3ff"));
                }
            } else {
                userProfileCircle.setFill(javafx.scene.paint.Color.web("#d4e3ff"));
            }
        }

        refreshDashboardGreeting();
    }

    /**
     * Handles search input from the global search bar
     * @param query The search query
     */
    private void handleSearch(String query) {
        System.out.println("Searching for: " + query);
        // TODO: Implement global search logic (e.g., filter history or navigate to specific insights)
    }

    @FXML
    public void switchToDashboard() {
        loadScreen(DASHBOARD_VIEW);
        Object controller = controllerCache.get(DASHBOARD_VIEW);
        if (controller instanceof DashboardController dashboardController) {
            dashboardController.refreshDashboardData();
        }
        updateNavigation(dashboardBtn);
    }

    @FXML
    public void switchToAddLog() {
        loadScreen(ADD_LOG_VIEW);
        Object controller = controllerCache.get(ADD_LOG_VIEW);
        if (controller instanceof AddLogController addLogController) {
            addLogController.refreshMotivationalQuote();
        }
        updateNavigation(addLogBtn);
    }

    @FXML
    public void switchToHistory() {
        loadScreen(HISTORY_VIEW);
        updateNavigation(historyBtn);
    }

    @FXML
    public void switchToWeatherInsights() {
        loadScreen(WEATHER_INSIGHTS_VIEW);
        updateNavigation(weatherInsightsBtn);
    }

    @FXML
    public void openSettings() {
        loadScreen(SETTINGS_VIEW);
        updateNavigation(settingsBtn);
    }

    @FXML
    public void openSupport() {
        System.out.println("Opening Support...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Support");
        alert.setHeaderText("Need help with Wellness Sanctuary?");
        alert.setContentText("Choose Settings -> Contact Support for guided help, account support, and FAQ links.");
        Toast.styleAlert(alert, supportBtn, false);

        ButtonType openSettingsChoice = new ButtonType("Open Settings");
        alert.getButtonTypes().setAll(openSettingsChoice, ButtonType.OK);
        alert.showAndWait().ifPresent(choice -> {
            if (choice == openSettingsChoice) {
                openSettings();
            }
        });
        Toast.show(supportBtn, "Support options opened", false);
    }

    @FXML
    public void openNotifications() {
        System.out.println("Opening Notifications...");

        List<String> notifications = new ArrayList<>();
        boolean hasLogToday = hasLogForToday();
        String city = UserPreferences.getCity();

        if (!hasLogToday) {
            notifications.add("You have not logged today yet. Add a quick wellness check-in.");
        } else {
            notifications.add("Great job! Today's wellness entry is already saved.");
        }

        if (city == null || city.isBlank()) {
            notifications.add("Set your city in Settings for more accurate weather insights.");
        }

        notifications.add("Tip: Review History weekly to spot mood and weather patterns.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Wellness updates for you");
        alert.setContentText(String.join("\n\n- ", prependBullet(notifications)));
        Toast.styleAlert(alert, notificationBtn, false);

        ButtonType addLogChoice = new ButtonType("Open Add Log");
        ButtonType settingsChoice = new ButtonType("Open Settings");
        alert.getButtonTypes().setAll(addLogChoice, settingsChoice, ButtonType.CLOSE);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == addLogChoice) {
                switchToAddLog();
            } else if (choice == settingsChoice) {
                openSettings();
            }
        });

        Toast.show(notificationBtn, "Notifications loaded", false);
    }

    private boolean hasLogForToday() {
        int userId = UserSession.getCurrentUserId();
        if (userId <= 0) {
            return false;
        }
        try {
            HealthEntryRepository repository = new HealthEntryRepository(DatabaseConnection.getConnection());
            List<HealthEntry> entries = repository.findByUserId(userId);
            LocalDate today = LocalDate.now();
            return entries.stream().anyMatch(entry -> today.equals(entry.getEntryDate()));
        } catch (SQLException e) {
            System.err.println("Notification check failed: " + e.getMessage());
            return false;
        }
    }

    private String prependBullet(List<String> lines) {
        if (lines.isEmpty()) {
            return "- No new notifications.";
        }
        return "- " + String.join("\n\n- ", lines);
    }

    @FXML
    public void signOut() {
        System.out.println("Signing out...");
        Toast.show(signOutBtn, "Signed out", false);
        try {
            AppRouter.showAuth();
        } catch (IOException e) {
            System.err.println("Failed to load auth screen: " + e.getMessage());
        }
    }

    /**
     * Loads a screen FXML file into the content area
     * @param fxmlFile The FXML filename to load
     */
    private void loadScreen(String fxmlFile) {
        try {
            Parent screen = screenCache.get(fxmlFile);
            if (screen == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                screen = loader.load();
                screenCache.put(fxmlFile, screen);
                controllerCache.put(fxmlFile, loader.getController());
            }
            contentArea.getChildren().setAll(screen);
        } catch (IOException e) {
            System.err.println("Error loading screen: " + fxmlFile);
            e.printStackTrace();
        }
    }

    /**
     * Updates navigation styling to highlight the active button
     * @param activeButton The button that is currently active
     */
    private void updateNavigation(Button activeButton) {
        // Reset all buttons
        dashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-alignment: left; -fx-alignment: CENTER_LEFT; -fx-font-size: 0.95em;");
        addLogBtn.setStyle("-fx-background-color: transparent; -fx-text-alignment: left; -fx-alignment: CENTER_LEFT; -fx-font-size: 0.95em;");
        historyBtn.setStyle("-fx-background-color: transparent; -fx-text-alignment: left; -fx-alignment: CENTER_LEFT; -fx-font-size: 0.95em;");
        weatherInsightsBtn.setStyle("-fx-background-color: transparent; -fx-text-alignment: left; -fx-alignment: CENTER_LEFT; -fx-font-size: 0.95em;");

        // Highlight active button
        activeButton.setStyle("-fx-background-color: transparent; -fx-text-alignment: left; -fx-alignment: CENTER_LEFT; -fx-font-size: 0.95em; -fx-text-fill: #005faf; -fx-font-weight: bold;");
    }

    public void updateProfilePicture(Image image) {
        if (userProfileCircle != null && image != null) {
            userProfileCircle.setFill(new ImagePattern(image));
            if (userProfileInitials != null) {
                userProfileInitials.setVisible(false);
            }
        }
    }

    public void refreshCurrentScreen() {
        refreshDashboardGreeting();
    }

    private void refreshDashboardGreeting() {
        Object controller = controllerCache.get(DASHBOARD_VIEW);
        if (controller instanceof DashboardController dashboardController) {
            dashboardController.refreshGreeting();
            dashboardController.refreshThemeStyles();
        }
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) {
            return "S";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isBlank()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.length() == 0 ? "S" : initials.toString().toUpperCase();
    }
}

