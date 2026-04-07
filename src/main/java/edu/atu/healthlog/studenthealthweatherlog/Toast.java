package edu.atu.healthlog.studenthealthweatherlog;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Toast - Lightweight transient notification helper for JavaFX.
 */
public final class Toast {
    private Toast() {
    }

    public static void show(Node owner, String message, boolean isError) {
        if (owner == null || owner.getScene() == null || owner.getScene().getWindow() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
            return;
        }

        Window window = owner.getScene().getWindow();
        Label label = new Label(message);
        String background = isError ? "#d32f2f" : "#2e7d32";
        label.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 14;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;"
        );

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.getContent().add(label);
        popup.show(window);

        label.applyCss();
        label.layout();
        double x = window.getX() + window.getWidth() - label.getWidth() - 20;
        double y = window.getY() + window.getHeight() - label.getHeight() - 40;
        popup.setX(Math.max(window.getX() + 10, x));
        popup.setY(Math.max(window.getY() + 10, y));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        PauseTransition wait = new PauseTransition(Duration.seconds(2.5));
        SequentialTransition seq = new SequentialTransition(wait, fadeOut);
        seq.setOnFinished(e -> popup.hide());
        seq.play();
    }
}

