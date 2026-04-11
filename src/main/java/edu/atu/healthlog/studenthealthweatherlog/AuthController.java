package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.models.User;
import edu.atu.healthlog.studenthealthweatherlog.repositories.UserRepository;
import edu.atu.healthlog.studenthealthweatherlog.repositories.WeatherDataRepository;
import edu.atu.healthlog.studenthealthweatherlog.services.WeatherService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AuthController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private Button signInBtn;
    @FXML private Button signUpBtn;
    @FXML private Label statusLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label nameHelpLabel;
    @FXML private TabPane authTabPane;

    private Tab signInTab;
    private Tab signUpTab;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        try {
            userRepository = new UserRepository(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            setStatus("Database unavailable. Please start the Docker container.", true);
        }
        setStatus("Please sign in to continue.", false);
        setupTabs();
    }

    private void setupTabs() {
        if (authTabPane == null || authTabPane.getTabs().size() < 2) return;
        signInTab = authTabPane.getTabs().get(0);
        signUpTab = authTabPane.getTabs().get(1);

        authTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) ->
                updateModeUI(newTab == signInTab));
        updateModeUI(true);
    }

    private void updateModeUI(boolean isSignIn) {
        if (nameField != null) {
            nameField.setVisible(!isSignIn);
            nameField.setManaged(!isSignIn);
        }
        if (nameHelpLabel != null) {
            nameHelpLabel.setVisible(!isSignIn);
            nameHelpLabel.setManaged(!isSignIn);
        }
        if (signInBtn != null) {
            signInBtn.setDisable(!isSignIn);
            signInBtn.setVisible(isSignIn);
            signInBtn.setManaged(isSignIn);
        }
        if (signUpBtn != null) {
            signUpBtn.setDisable(isSignIn);
            signUpBtn.setVisible(!isSignIn);
            signUpBtn.setManaged(!isSignIn);
        }
        if (subtitleLabel != null) {
            subtitleLabel.setText(isSignIn ? "Sign in to continue." : "Create a new account to get started.");
        }
        setStatus(isSignIn ? "Please sign in to continue." : "Create your account to get started.", false);
    }

    @FXML
    public void handleSignIn() {
        if (userRepository == null) {
            setStatus("Database unavailable. Please start the Docker container.", true);
            return;
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            setStatus("Email and password are required.", true);
            return;
        }

        try {
            Optional<User> found = userRepository.findByEmail(email);
            if (found.isEmpty()) {
                setStatus("No account found with that email.", true);
                return;
            }

            User user = found.get();
            if (!PasswordUtils.verify(password, user.getPassword())) {
                setStatus("Incorrect password.", true);
                return;
            }

            UserSession.login(user);
            UserPreferences.setUserName(user.getUsername());
            UserPreferences.setUserEmail(user.getEmail());
            if (user.getCity() != null && !user.getCity().isBlank()) {
                UserPreferences.setCity(user.getCity());
            }
            System.out.println("User logged in: [id=" + user.getUserId() + ", username=" + user.getUsername() + ", email=" + user.getEmail() + "]");
            fetchWeatherInBackground();
            switchToMain();

        } catch (SQLException e) {
            setStatus("Sign in failed: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleSignUp() {
        if (userRepository == null) {
            setStatus("Database unavailable. Please start the Docker container.", true);
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            setStatus("Name, email, and password are required.", true);
            return;
        }

        if (!email.contains("@")) {
            setStatus("Please enter a valid email address.", true);
            return;
        }

        if (password.length() < 8) {
            setStatus("Password must be at least 8 characters.", true);
            return;
        }

        try {
            if (userRepository.findByEmail(email).isPresent()) {
                setStatus("An account with that email already exists.", true);
                return;
            }

            User newUser = new User(0, name, email, PasswordUtils.hash(password), UserPreferences.getCity());
            int generatedId = userRepository.save(newUser);

            User savedUser = new User(generatedId, name, email, password, UserPreferences.getCity());
            UserSession.login(savedUser);
            UserPreferences.setUserName(name);
            UserPreferences.setUserEmail(email);
            fetchWeatherInBackground();
            switchToMain();

        } catch (SQLException e) {
            setStatus("Sign up failed: " + e.getMessage(), true);
        }
    }

    /**
     * Kicks off a background thread to fetch today's weather and persist it.
     * Silently skips if no real API key is configured or if the fetch fails.
     */
    private void fetchWeatherInBackground() {
        new Thread(() -> {
            try {
                WeatherService service = new WeatherService();
                edu.atu.healthlog.studenthealthweatherlog.models.WeatherData data = service.fetchForPersistence();
                if (data == null) return;

                WeatherDataRepository repo = new WeatherDataRepository(DatabaseConnection.getConnection());
                repo.save(data);
                System.out.println("Weather data persisted for city: " + data.getCity());
            } catch (Exception e) {
                System.err.println("Background weather fetch/save failed: " + e.getMessage());
            }
        }, "weather-fetch").start();
    }

    private void switchToMain() {
        try {
            AppRouter.showMain();
        } catch (IOException e) {
            setStatus("Failed to load main app.", true);
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #d32f2f;" : "-fx-text-fill: #2e7d32;");
    }
}
