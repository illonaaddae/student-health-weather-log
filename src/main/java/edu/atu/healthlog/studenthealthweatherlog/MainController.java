package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.HashMap;
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

        if (userProfileCircle != null) {
            userProfileCircle.setFill(javafx.scene.paint.Color.web("#d4e3ff"));
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
        updateNavigation(dashboardBtn);
    }

    @FXML
    public void switchToAddLog() {
        loadScreen(ADD_LOG_VIEW);
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
        // TODO: Implement support screen or dialog
    }

    @FXML
    public void openNotifications() {
        System.out.println("Opening Notifications...");
        // TODO: Implement notification screen or dialog
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

