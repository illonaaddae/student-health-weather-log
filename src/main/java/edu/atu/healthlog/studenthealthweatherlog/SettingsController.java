package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.models.User;
import edu.atu.healthlog.studenthealthweatherlog.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;

/**
 * SettingsController - Manages the settings and preferences view.
 * Handles user account settings, notifications, wellness goals, and privacy options.
 */
public class SettingsController {

    @FXML
    private CheckBox dailyReminders;
    @FXML
    private CheckBox weatherAlerts;
    @FXML
    private CheckBox weeklyInsights;
    @FXML
    private CheckBox goalReminders;
    @FXML
    private Spinner<Integer> targetSleepSpinner;
    @FXML
    private Spinner<Integer> targetWaterSpinner;
    @FXML
    private Spinner<Integer> targetExerciseSpinner;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private CheckBox dataSharing;
    @FXML
    private Button saveBtn;

    // Profile fields
    @FXML
    private TextField userNameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private Button editProfileBtn;
    @FXML
    private Circle profileCircle;
    @FXML
    private Label profileInitials;
    @FXML
    private StackPane profileStackPane;

    // Appearance
    @FXML
    private ToggleGroup themeToggleGroup;
    @FXML
    private RadioButton lightModeRadio;
    @FXML
    private RadioButton darkModeRadio;
    @FXML
    private RadioButton systemModeRadio;

    private boolean isEditingProfile = false;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        try {
            userRepository = new UserRepository(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            System.err.println("Settings: could not connect to database: " + e.getMessage());
        }
        setupSpinners();
        loadSettings();
        applySavedThemeSelection();
    }

    /**
     * Sets up spinner configurations for wellness goals
     */
    private void setupSpinners() {
        // Target sleep spinner
        SpinnerValueFactory<Integer> sleepFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 8);
        targetSleepSpinner.setValueFactory(sleepFactory);

        // Target water spinner
        SpinnerValueFactory<Integer> waterFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, 5000, 2500, 100);
        targetWaterSpinner.setValueFactory(waterFactory);

        // Target exercise spinner
        SpinnerValueFactory<Integer> exerciseFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 240, 30, 5);
        targetExerciseSpinner.setValueFactory(exerciseFactory);
    }

    /**
     * Loads user settings from preferences (mock implementation)
     */
    private void loadSettings() {
        // TODO: Load actual user settings from database/preferences file
        // Mock values
        dailyReminders.setSelected(true);
        weatherAlerts.setSelected(true);
        weeklyInsights.setSelected(true);
        goalReminders.setSelected(false);
        dataSharing.setSelected(false);
        if (cityField != null) {
            String city = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getCity() : null;
            if (city == null || city.isBlank()) {
                city = UserPreferences.getCity();
            }
            cityField.setText(extractCity(city));
            if (countryField != null) {
                countryField.setText(extractCountry(city));
            }
        }
        if (userNameField != null) {
            userNameField.setText(UserPreferences.getUserName());
        }
        if (userEmailField != null) {
            userEmailField.setText(UserPreferences.getUserEmail());
        }
        restoreProfilePicture();
    }

    private void restoreProfilePicture() {
        String path = UserPreferences.getCurrentUserProfilePicturePath();
        if (path == null || profileCircle == null) return;
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            UserPreferences.setCurrentUserProfilePicturePath(null);
            return;
        }
        try {
            javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
            profileCircle.setFill(new javafx.scene.paint.ImagePattern(image));
            if (profileInitials != null) profileInitials.setVisible(false);
        } catch (Exception e) {
            System.err.println("Could not restore profile picture: " + e.getMessage());
        }
    }

    private void applySavedThemeSelection() {
        if (UserPreferences.isDarkThemeEnabled()) {
            if (darkModeRadio != null) {
                darkModeRadio.setSelected(true);
            }
            applyDarkTheme();
        } else {
            if (lightModeRadio != null) {
                lightModeRadio.setSelected(true);
            }
            applyLightTheme();
        }
    }

    /**
     * Updates user profile information
     */
    @FXML
    public void editProfile() {
        if (!isEditingProfile) {
            // Enable editing
            userNameField.setEditable(true);
            userEmailField.setEditable(true);
            userNameField.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-radius: 4;");
            userEmailField.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-radius: 4;");
            editProfileBtn.setText("Save");
            isEditingProfile = true;
            System.out.println("Profile editing enabled.");
            Toast.show(editProfileBtn, "Edit profile enabled", false);
        } else {
            // Save profile
            saveProfile();
            userNameField.setEditable(false);
            userEmailField.setEditable(false);
            userNameField.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            userEmailField.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            editProfileBtn.setText("Edit");
            isEditingProfile = false;
            
            // Update initials
            updateInitials();
            refreshMainProfile();
            System.out.println("Profile saved.");
            Toast.show(editProfileBtn, "Profile saved", false);
        }
    }

    /**
     * Implementation for changing the profile picture
     */
    @FXML
    public void changeProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profileCircle.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                profileCircle.setFill(new ImagePattern(image));
                profileInitials.setVisible(false);

                UserPreferences.setCurrentUserProfilePicturePath(selectedFile.getAbsolutePath());

                // Update sidebar profile globally via MainController instance
                if (MainController.getInstance() != null) {
                    MainController.getInstance().updateProfilePicture(image);
                }

                System.out.println("Profile picture updated: " + selectedFile.getName());
                Toast.show(profileCircle, "Profile picture updated", false);
            } catch (Exception e) {
                System.err.println("Error loading profile picture: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Image Load Failed");
                alert.setContentText("Could not load the selected image. Please try another one.");
                Toast.styleAlert(alert, profileCircle, true);
                alert.show();
                Toast.show(profileCircle, "Image load failed", true);
            }
        }
    }

    private void saveProfile() {
        String name = userNameField.getText().trim();
        String email = userEmailField.getText().trim();

        if (name.isBlank() || email.isBlank()) {
            Toast.show(editProfileBtn, "Name and email cannot be empty", true);
            return;
        }

        User current = UserSession.getCurrentUser();
        if (current == null || userRepository == null) {
            Toast.show(editProfileBtn, "Not logged in", true);
            return;
        }

        try {
            current.setUserName(name);
            current.setEmail(email);
            userRepository.update(current);
            UserPreferences.setUserName(name);
            UserPreferences.setUserEmail(email);
            System.out.println("Profile saved to DB: " + name + " (" + email + ")");
        } catch (IllegalArgumentException e) {
            Toast.show(editProfileBtn, e.getMessage(), true);
        } catch (SQLException e) {
            System.err.println("Failed to save profile: " + e.getMessage());
            Toast.show(editProfileBtn, "Failed to save profile", true);
        }
    }

    private void refreshMainProfile() {
        if (MainController.getInstance() != null) {
            MainController.getInstance().refreshUserProfile();
            MainController.getInstance().refreshCurrentScreen();
        }
    }

    private void updateInitials() {
        String name = userNameField.getText();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split("\\s+");
            StringBuilder initials = new StringBuilder();
            for (int i = 0; i < Math.min(parts.length, 2); i++) {
                if (!parts[i].isEmpty()) {
                    initials.append(parts[i].charAt(0));
                }
            }
            profileInitials.setText(initials.toString().toUpperCase());
        }
    }

    @FXML
    private void handleThemeChange() {
        RadioButton selected = (RadioButton) themeToggleGroup.getSelectedToggle();
        if (selected == null) return;
        
        String theme = selected.getText();
        System.out.println("Theme changed to: " + theme);
        UserPreferences.setDarkThemeEnabled(theme.contains("Dark"));
        
        if (theme.contains("Dark")) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }

    private void applyDarkTheme() {
        System.out.println("Applying Dark Theme...");
        if (saveBtn != null && saveBtn.getScene() != null && saveBtn.getScene().getRoot() != null) {
            saveBtn.getScene().getRoot().getStyleClass().remove("dark-theme");
            saveBtn.getScene().getRoot().getStyleClass().add("dark-theme");
        }
        if (MainController.getInstance() != null) {
            MainController.getInstance().refreshCurrentScreen();
        }
    }

    private void applyLightTheme() {
        System.out.println("Applying Light Theme...");
        if (saveBtn != null && saveBtn.getScene() != null && saveBtn.getScene().getRoot() != null) {
            saveBtn.getScene().getRoot().getStyleClass().remove("dark-theme");
        }
        if (MainController.getInstance() != null) {
            MainController.getInstance().refreshCurrentScreen();
        }
    }

    /**
     * Opens change password dialog
     */
    @FXML
    public void changePassword() {
        if (userRepository == null || UserSession.getCurrentUser() == null) {
            Toast.show(saveBtn, "Not logged in", true);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField currentPwField = new PasswordField();
        PasswordField newPwField = new PasswordField();
        PasswordField confirmPwField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Current password:"), 0, 0);
        grid.add(currentPwField, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPwField, 1, 1);
        grid.add(new Label("Confirm new password:"), 0, 2);
        grid.add(confirmPwField, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;

            String current = currentPwField.getText();
            String newPw = newPwField.getText();
            String confirm = confirmPwField.getText();

            User user = UserSession.getCurrentUser();

            if (!PasswordUtils.verify(current, user.getPassword())) {
                Toast.show(saveBtn, "Current password is incorrect", true);
                return;
            }
            if (newPw.length() < 8) {
                Toast.show(saveBtn, "New password must be at least 8 characters", true);
                return;
            }
            if (!newPw.equals(confirm)) {
                Toast.show(saveBtn, "New passwords do not match", true);
                return;
            }

            try {
                user.setPassword(PasswordUtils.hash(newPw));
                userRepository.update(user);
                System.out.println("Password changed successfully for user: " + user.getUsername());
                Toast.show(saveBtn, "Password changed successfully", false);
            } catch (SQLException e) {
                System.err.println("Failed to change password: " + e.getMessage());
                Toast.show(saveBtn, "Failed to change password", true);
            }
        });
    }

    /**
     * Exports user data as JSON/CSV
     */
    @FXML
    public void exportData() {
        System.out.println("Exporting user data...");
        Toast.show(saveBtn, "Export started (mock)", false);
        // TODO: Implement data export functionality
    }

    /**
     * Permanently deletes the user account
     */
    @FXML
    public void deleteAccount() {
        if (userRepository == null || UserSession.getCurrentUser() == null) {
            Toast.show(saveBtn, "Not logged in", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("This will permanently delete your account and all data.");
        confirm.setContentText("Are you sure you want to continue?");
        Toast.styleAlert(confirm, saveBtn, true);

        confirm.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;
            try {
                int userId = UserSession.getCurrentUserId();
                userRepository.delete(userId);
                UserSession.logout();
                System.out.println("Account deleted for user id: " + userId);
                AppRouter.showAuth();
            } catch (SQLException e) {
                System.err.println("Failed to delete account: " + e.getMessage());
                Toast.show(saveBtn, "Failed to delete account", true);
            } catch (java.io.IOException e) {
                System.err.println("Failed to navigate to auth: " + e.getMessage());
            }
        });
    }

    /**
     * Saves all settings changes
     */
    @FXML
    public void saveChanges() {
        System.out.println("Saving settings...");
        if (saveBtn != null) {
            saveBtn.setDisable(true);
        }
        try {
            // Collect all settings
            boolean dailyRemindersEnabled = dailyReminders.isSelected();
            boolean weatherAlertsEnabled = weatherAlerts.isSelected();
            boolean weeklyInsightsEnabled = weeklyInsights.isSelected();
            boolean goalRemindersEnabled = goalReminders.isSelected();
            int targetSleep = targetSleepSpinner.getValue();
            int targetWater = targetWaterSpinner.getValue();
            int targetExercise = targetExerciseSpinner.getValue();
            boolean dataSharingEnabled = dataSharing.isSelected();

            if (cityField != null) {
                String cityValue = cityField.getText();
                String countryValue = countryField != null ? countryField.getText() : "";
                String city = composeLocation(cityValue, countryValue);
                UserPreferences.setCity(city);
                User current = UserSession.getCurrentUser();
                if (current != null && userRepository != null) {
                    try {
                        current.setCity(city);
                        userRepository.update(current);
                    } catch (SQLException e) {
                        System.err.println("Failed to update city in DB: " + e.getMessage());
                    }
                }
            }

            // TODO: Persist settings to database/preferences
            System.out.println("Settings saved successfully!");
            System.out.println("  Daily Reminders: " + dailyRemindersEnabled);
            System.out.println("  Weather Alerts: " + weatherAlertsEnabled);
            System.out.println("  Weekly Insights: " + weeklyInsightsEnabled);
            System.out.println("  Goal Reminders: " + goalRemindersEnabled);
            System.out.println("  Target Sleep: " + targetSleep + " hours");
            System.out.println("  Target Water: " + targetWater + " ml");
            System.out.println("  Target Exercise: " + targetExercise + " mins/day");
            System.out.println("  Data Sharing: " + dataSharingEnabled);
            if (cityField != null) {
                System.out.println("  Weather City: " + UserPreferences.getCity());
            }

            Toast.show(saveBtn, "Settings saved successfully", false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Settings");
            alert.setHeaderText("Saved");
            alert.setContentText("Your settings have been saved successfully.");
            Toast.styleAlert(alert, saveBtn, false);
            alert.show();
        } catch (Exception e) {
            Toast.show(saveBtn, "Failed to save settings", true);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Settings Error");
            alert.setHeaderText("Save failed");
            alert.setContentText(e.getMessage());
            Toast.styleAlert(alert, saveBtn, true);
            alert.show();
        } finally {
            if (saveBtn != null) {
                saveBtn.setDisable(false);
            }
        }
    }

    private String composeLocation(String city, String country) {
        String c = city == null ? "" : city.trim();
        String k = country == null ? "" : country.trim();
        if (c.isBlank()) {
            return "";
        }
        return k.isBlank() ? c : c + ", " + k;
    }

    private String extractCity(String location) {
        if (location == null || location.isBlank()) {
            return "";
        }
        String[] parts = location.split(",", 2);
        return parts[0].trim();
    }

    private String extractCountry(String location) {
        if (location == null || location.isBlank()) {
            return "";
        }
        String[] parts = location.split(",", 2);
        if (parts.length < 2) {
            return "";
        }
        return parts[1].trim();
    }

    /**
     * Opens contact support dialog or email
     */
    @FXML
    public void contactSupport() {
        System.out.println("Opening support contact...");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Support");
        alert.setHeaderText("Connecting to Wellness Support");
        alert.setContentText("You are being redirected to our support portal. You can also reach us directly at support@wellnesslog.atu.edu");
        Toast.styleAlert(alert, saveBtn, false);
        alert.show();

        Toast.show(saveBtn, "Support portal opened (mock)", false);

        // In a real JavaFX application, we'd use getHostServices().showDocument()
        // For this mock, we'll just simulate the redirection
    }

    /**
     * Opens user guide documentation
     */
    @FXML
    public void openUserGuide() {
        System.out.println("Opening user guide...");
        Toast.show(saveBtn, "User guide coming soon", false);
    }

    /**
     * Opens terms of service
     */
    @FXML
    public void openTerms() {
        System.out.println("Opening terms of service...");
        Toast.show(saveBtn, "Terms of service coming soon", false);
    }

    /**
     * Opens privacy policy
     */
    @FXML
    public void openPrivacy() {
        System.out.println("Opening privacy policy...");
        Toast.show(saveBtn, "Privacy policy coming soon", false);
    }

    /**
     * Helper class for user settings
     */
    public static class UserSettings {
        private boolean dailyReminders;
        private boolean weatherAlerts;
        private boolean weeklyInsights;
        private boolean goalReminders;
        private int targetSleep;
        private int targetWater;
        private int targetExercise;
        private boolean dataSharing;

        public UserSettings(boolean dailyReminders, boolean weatherAlerts, boolean weeklyInsights,
                           boolean goalReminders, int targetSleep, int targetWater,
                           int targetExercise, boolean dataSharing) {
            this.dailyReminders = dailyReminders;
            this.weatherAlerts = weatherAlerts;
            this.weeklyInsights = weeklyInsights;
            this.goalReminders = goalReminders;
            this.targetSleep = targetSleep;
            this.targetWater = targetWater;
            this.targetExercise = targetExercise;
            this.dataSharing = dataSharing;
        }

        // Getters and setters...
        public boolean isDailyReminders() { return dailyReminders; }
        public void setDailyReminders(boolean dailyReminders) { this.dailyReminders = dailyReminders; }

        public boolean isWeatherAlerts() { return weatherAlerts; }
        public void setWeatherAlerts(boolean weatherAlerts) { this.weatherAlerts = weatherAlerts; }

        public boolean isWeeklyInsights() { return weeklyInsights; }
        public void setWeeklyInsights(boolean weeklyInsights) { this.weeklyInsights = weeklyInsights; }

        public boolean isGoalReminders() { return goalReminders; }
        public void setGoalReminders(boolean goalReminders) { this.goalReminders = goalReminders; }

        public int getTargetSleep() { return targetSleep; }
        public void setTargetSleep(int targetSleep) { this.targetSleep = targetSleep; }

        public int getTargetWater() { return targetWater; }
        public void setTargetWater(int targetWater) { this.targetWater = targetWater; }

        public int getTargetExercise() { return targetExercise; }
        public void setTargetExercise(int targetExercise) { this.targetExercise = targetExercise; }

        public boolean isDataSharing() { return dataSharing; }
        public void setDataSharing(boolean dataSharing) { this.dataSharing = dataSharing; }
    }
}
