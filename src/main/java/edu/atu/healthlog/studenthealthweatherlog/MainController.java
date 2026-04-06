package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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

    @FXML
    public void initialize() {
        // Setup search field listener
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                handleSearch(newValue);
            });
        }

        // Mock user data
        if (userNameLabel != null) userNameLabel.setText("Illona");

        // Load the dashboard by default
        switchToDashboard();
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
        // TODO: Implement sign-out functionality
    }

    /**
     * Loads a screen FXML file into the content area
     * @param fxmlFile The FXML filename to load
     */
    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlFile)
            );
            contentArea.getChildren().clear();
            contentArea.getChildren().add(loader.load());
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
}

