package com.faqchatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SplashScreen splash = new SplashScreen(() ->
                Platform.runLater(() -> launchMainWindow(primaryStage)));
        splash.show();
    }

    private void launchMainWindow(Stage primaryStage) {

        // ── Initialize all managers ──────────────────────────────────────────
        I18nManager.initialize();                // Feature 9 – load saved language
        ThemeManager.initialize();
        SessionManager.initialize();
        SpeechManager.initialize();
        VoiceInputManager.initialize();          // Feature 1 – check mic availability
        // NotificationSoundManager has no init step (generates tones on the fly)

        // ── Build UI ─────────────────────────────────────────────────────────
        ChatController chatController = new ChatController(primaryStage);

        Sidebar sidebar = new Sidebar(
                session -> chatController.loadSession(session),
                unused  -> chatController.loadSession(SessionManager.createNewSession())
        );

        chatController.setSidebar(sidebar);

        HBox root = new HBox(0);
        root.getChildren().addAll(sidebar, chatController);
        HBox.setHgrow(chatController, Priority.ALWAYS);

        Scene scene = new Scene(root, 1060, 680);

        // Load CSS
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        ThemeManager.applyTheme(scene);

        // App icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/assets/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Icon not found, skipping.");
        }

        primaryStage.setTitle("🤖 " + I18nManager.t("app.title") + " — International Edition");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(560);
        primaryStage.setMaximized(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> SpeechManager.shutdown());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
