package edu.atu.healthlog.studenthealthweatherlog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * AppRouter - simple scene switcher for auth/main screens.
 */
public final class AppRouter {
    private static Stage primaryStage;

    private AppRouter() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showAuth() throws IOException {
        if (primaryStage == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("auth-view.fxml"));
        Scene scene = new Scene(loader.load(), 980, 640);
        addStylesheet(scene);
        applySavedTheme(scene);
        primaryStage.setTitle("Wellness Sanctuary - Sign In");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMain() throws IOException {
        if (primaryStage == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1400, 900);
        addStylesheet(scene);
        applySavedTheme(scene);
        primaryStage.setTitle("Wellness Sanctuary - Health & Wellness Log");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void applySavedTheme(Scene scene) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }
        scene.getRoot().getStyleClass().remove("dark-theme");
        if (UserPreferences.isDarkThemeEnabled()) {
            scene.getRoot().getStyleClass().add("dark-theme");
        }
    }

    private static void addStylesheet(Scene scene) {
        if (scene == null) {
            return;
        }
        var resource = HelloApplication.class.getResource("styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        }
    }
}

