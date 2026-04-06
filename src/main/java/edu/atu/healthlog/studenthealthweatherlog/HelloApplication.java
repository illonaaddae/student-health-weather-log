package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launcher application for the Wellness Sanctuary health & wellness tracking system.
 * This is a JavaFX desktop application following MVC architecture.
 */
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database tables
        DatabaseConnection.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        // Apply stylesheet
        String cssResource = HelloApplication.class.getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(cssResource);

        stage.setTitle("Wellness Sanctuary - Health & Wellness Log");
        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
