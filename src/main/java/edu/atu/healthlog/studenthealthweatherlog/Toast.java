package edu.atu.healthlog.studenthealthweatherlog;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.DialogPane;
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
        boolean darkTheme = owner.getScene().getRoot() != null
                && owner.getScene().getRoot().getStyleClass().contains("dark-theme");
        Label label = new Label(message);
        String background = darkTheme
                ? (isError ? "#8d4a4a" : "#335c4f")
                : (isError ? "#d32f2f" : "#2e7d32");
        String textColor = darkTheme ? "#f5f7fa" : "white";
        label.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-padding: 10 14;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 12px;" +
                "-fx-border-color: rgba(255,255,255,0.12);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
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

    public static void styleAlert(Alert alert, Node owner, boolean isError) {
        if (alert == null) {
            return;
        }

        boolean darkTheme = owner != null
                && owner.getScene() != null
                && owner.getScene().getRoot() != null
                && owner.getScene().getRoot().getStyleClass().contains("dark-theme");

        DialogPane pane = alert.getDialogPane();
        String background = darkTheme ? "#2b2f33" : "#ffffff";
        String textColor = darkTheme ? "#edf0f2" : "#2d3435";
        String borderColor = darkTheme ? (isError ? "#8d4a4a" : "#5f6b72") : (isError ? "#f2b8b5" : "#d4e3ff");
        pane.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );
        pane.getStyleClass().add("theme-alert");
        if (pane.getScene() != null) {
            pane.getScene().getRoot().setStyle("-fx-background-color: transparent;");
        }
        pane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: " + textColor + ";"));
    }
}

