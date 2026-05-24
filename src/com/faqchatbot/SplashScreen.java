package com.faqchatbot;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen extends Stage {

    private Runnable onComplete;

    public SplashScreen(Runnable onComplete) {
        this.onComplete = onComplete;
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #1f6feb;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;"
        );
        root.setPrefSize(420, 280);

        Label icon = new Label("🤖");
        icon.setStyle("-fx-font-size: 56px;");

        Label title = new Label("AI Assistant Pro");
        title.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #e6edf3;" +
            "-fx-font-family: 'Segoe UI';"
        );

        Label subtitle = new Label("Powered by Groq · Llama 3");
        subtitle.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-font-family: 'Segoe UI';"
        );

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(280);
        progressBar.setStyle(
            "-fx-accent: #1f6feb;" +
            "-fx-background-color: #21262d;" +
            "-fx-background-radius: 4px;" +
            "-fx-pref-height: 4px;"
        );

        Label loadingLabel = new Label("Initializing...");
        loadingLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #484f58;" +
            "-fx-font-family: 'Segoe UI';"
        );

        Label version = new Label("v2.0.0 International Edition");
        version.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-text-fill: #30363d;" +
            "-fx-font-family: 'Segoe UI';"
        );

        root.getChildren().addAll(icon, title, subtitle, progressBar, loadingLabel, version);

        Scene scene = new Scene(root, 420, 280);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setAlwaysOnTop(true);

        // Animate icon
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), icon);
        scale.setFromX(0.5); scale.setToX(1.0);
        scale.setFromY(0.5); scale.setToY(1.0);
        scale.play();

        // Animate progress
        String[] steps = {
            "Loading AI models...",
            "Initializing sessions...",
            "Setting up themes...",
            "Preparing interface...",
            "Ready!"
        };

        Timeline timeline = new Timeline();
        for (int i = 0; i <= 10; i++) {
            final double progress = i / 10.0;
            final String stepText = steps[Math.min(i / 2, steps.length - 1)];
            KeyFrame frame = new KeyFrame(Duration.millis(i * 200), e -> {
                progressBar.setProgress(progress);
                loadingLabel.setText(stepText);
            });
            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(400), root);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(ev -> {
                this.close();
                onComplete.run();
            });
            fade.play();
        });

        timeline.play();
    }
}