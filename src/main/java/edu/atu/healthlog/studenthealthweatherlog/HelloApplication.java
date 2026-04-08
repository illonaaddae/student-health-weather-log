package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import javafx.application.Application;
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
        AppRouter.setPrimaryStage(stage);
        AppRouter.showAuth();
    }

    public static void main(String[] args) {
        launch();
    }
}
