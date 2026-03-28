package com.kingsley;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/views/main.fxml")
        );
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setTitle("Student Health & Wellness Log");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main( String[] args ) {
        launch(args);
    }
}
