package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * AuthController - handles mock sign-in/sign-up flows.
 */
public class AuthController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private Button signInBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label nameHelpLabel;
    @FXML
    private TabPane authTabPane;

    private Tab signInTab;
    private Tab signUpTab;

    @FXML
    public void initialize() {
        setStatus("Please sign in to continue.", false);
        setupTabs();

        if (nameField != null) {
            nameField.focusedProperty().addListener((obs, oldVal, focused) -> {
                if (focused && authTabPane != null && signUpTab != null
                        && authTabPane.getSelectionModel().getSelectedItem() != signUpTab) {
                    authTabPane.getSelectionModel().select(signUpTab);
                }
            });
        }
    }

    private void setupTabs() {
        if (authTabPane == null || authTabPane.getTabs().size() < 2) {
            return;
        }
        signInTab = authTabPane.getTabs().get(0);
        signUpTab = authTabPane.getTabs().get(1);

        authTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            updateModeUI(newTab == signInTab);
        });
        updateModeUI(authTabPane.getSelectionModel().getSelectedItem() == signInTab);
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

        if (isSignIn) {
            setStatus("Please sign in to continue.", false);
        } else {
            setStatus("Create your account to get started.", false);
        }
    }

    @FXML
    public void handleSignIn() {
        if (emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            setStatus("Email and password are required.", true);
            Toast.show(signInBtn, "Missing credentials", true);
            return;
        }

        String email = emailField.getText().trim();

        UserPreferences.setUserEmail(email);
        if (UserPreferences.getUserName() == null || UserPreferences.getUserName().isBlank()
                || "Student".equalsIgnoreCase(UserPreferences.getUserName())) {
            UserPreferences.setUserName(deriveNameFromEmail(email));
        }

        setStatus("Signed in successfully (mock).", false);
        Toast.show(signInBtn, "Signed in", false);
        switchToMain();
    }

    @FXML
    public void handleSignUp() {
        if (nameField.getText().isBlank() || emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            setStatus("Name, email, and password are required.", true);
            Toast.show(signUpBtn, "Missing fields", true);
            return;
        }

        UserPreferences.setUserName(nameField.getText().trim());
        UserPreferences.setUserEmail(emailField.getText().trim());

        setStatus("Account created (mock).", false);
        Toast.show(signUpBtn, "Account created", false);
        switchToMain();
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

    private String deriveNameFromEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "Student";
        }
        String prefix = email.substring(0, email.indexOf('@')).trim();
        return prefix.isEmpty() ? "Student" : prefix;
    }
}
