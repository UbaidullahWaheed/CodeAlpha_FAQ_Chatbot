package com.faqchatbot;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ChatBubble — Enhanced with:
 *   Feature 4  : Pin Messages (pin button + pinned visual state)
 *   Feature 7  : Word count & reading time badge on every message
 */
public class ChatBubble extends HBox {

    public enum BubbleType { USER, BOT }
    private static int fontSize = 13;

    // ── Feature 4: pin tracking ──────────────────────────────────────────────
    private boolean pinned = false;
    private Button pinBtn;
    private final String rawMessage;
    private final BubbleType bubbleType;

    public ChatBubble(String message, BubbleType type) {
        super();
        this.rawMessage  = message;
        this.bubbleType  = type;

        // Timestamp
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("timestamp-label");

        // ── Feature 7: Word count & reading time ─────────────────────────────
        int wordCount   = countWords(message);
        int readSec     = Math.max(1, (int) Math.ceil(wordCount / 3.5)); // ~210 wpm
        String readInfo = wordCount + " words · " + formatReadTime(readSec);
        Label statsLabel = new Label(readInfo);
        statsLabel.getStyleClass().add("message-stats-label");

        // Avatar
        Label avatar = new Label();
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setAlignment(Pos.CENTER);

        // Copy button
        Button copyBtn = new Button("⎘");
        copyBtn.getStyleClass().add("copy-button");
        copyBtn.setTooltip(new Tooltip("Copy to clipboard"));
        copyBtn.setOnAction(e -> {
            Clipboard cb = Clipboard.getSystemClipboard();
            ClipboardContent cc = new ClipboardContent();
            cc.putString(message);
            cb.setContent(cc);
            copyBtn.setText("✓");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> copyBtn.setText("⎘"));
            pause.play();
        });

        // ── Feature 4: Pin button ─────────────────────────────────────────────
        pinBtn = new Button("📌");
        pinBtn.getStyleClass().add("copy-button");
        pinBtn.setTooltip(new Tooltip("Pin message"));
        pinBtn.setOnAction(e -> togglePin());

        if (type == BubbleType.USER) {
            avatar.setText("👤");
            avatar.getStyleClass().add("user-avatar");

            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(440);
            messageLabel.setPadding(new Insets(12, 16, 12, 16));
            messageLabel.getStyleClass().add("user-bubble");
            messageLabel.setStyle("-fx-font-size: " + fontSize + "px;");

            VBox bubbleBox = new VBox(4);
            bubbleBox.setAlignment(Pos.CENTER_RIGHT);

            HBox topRow = new HBox(6);
            topRow.setAlignment(Pos.CENTER_RIGHT);
            topRow.getChildren().addAll(statsLabel, timeLabel, pinBtn, copyBtn);

            bubbleBox.getChildren().addAll(topRow, messageLabel);

            this.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().addAll(bubbleBox, avatar);
            this.setPadding(new Insets(4, 10, 4, 80));

        } else {
            avatar.setText("🤖");
            avatar.getStyleClass().add("bot-avatar");

            // Speak button
            Button speakBtn = new Button("🔊");
            speakBtn.getStyleClass().add("copy-button");
            speakBtn.setTooltip(new Tooltip("Read aloud"));
            speakBtn.setOnAction(e -> SpeechManager.speak(message));

            // Stop button
            Button stopBtn = new Button("⏹");
            stopBtn.getStyleClass().add("copy-button");
            stopBtn.setTooltip(new Tooltip("Stop reading"));
            stopBtn.setOnAction(e -> SpeechManager.stop());

            String cleanMessage = cleanMarkdown(message);

            Label messageLabel = new Label(cleanMessage);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(460);
            messageLabel.setPadding(new Insets(12, 16, 12, 16));
            messageLabel.getStyleClass().add("bot-bubble");
            messageLabel.setStyle("-fx-font-size: " + fontSize + "px;");

            VBox bubbleBox = new VBox(4);
            bubbleBox.setAlignment(Pos.CENTER_LEFT);

            HBox topRow = new HBox(6);
            topRow.setAlignment(Pos.CENTER_LEFT);
            topRow.getChildren().addAll(timeLabel, copyBtn, speakBtn, stopBtn, pinBtn, statsLabel);

            bubbleBox.getChildren().addAll(topRow, messageLabel);

            this.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().addAll(avatar, bubbleBox);
            this.setPadding(new Insets(4, 80, 4, 10));
        }

        this.getStyleClass().add("chat-bubble-row");

        // Slide-in animation
        this.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), this);
        slide.setFromY(20);
        slide.setToY(0);
        slide.play();
    }

    // ── Feature 4: pin / unpin ────────────────────────────────────────────────
    private void togglePin() {
        pinned = !pinned;
        if (pinned) {
            pinBtn.setText("📍");
            pinBtn.setTooltip(new Tooltip("Unpin message"));
            this.getStyleClass().add("pinned-bubble");
            PinManager.pin(rawMessage, bubbleType);
        } else {
            pinBtn.setText("📌");
            pinBtn.setTooltip(new Tooltip("Pin message"));
            this.getStyleClass().remove("pinned-bubble");
            PinManager.unpin(rawMessage);
        }
    }

    public boolean isPinned() { return pinned; }

    // ── Feature 7 helpers ─────────────────────────────────────────────────────
    private static int countWords(String text) {
        String clean = text.trim().replaceAll("\\s+", " ");
        if (clean.isEmpty()) return 0;
        return clean.split(" ").length;
    }

    private static String formatReadTime(int seconds) {
        if (seconds < 60) return seconds + "s read";
        return (seconds / 60) + "m " + (seconds % 60) + "s read";
    }

    // ── Markdown cleaner ──────────────────────────────────────────────────────
    private String cleanMarkdown(String text) {
        return text
                .replaceAll("#{1,6}\\s*", "")
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                .replaceAll("\\*(.+?)\\*", "$1")
                .replaceAll("__(.+?)__", "$1")
                .replaceAll("_(.+?)_", "$1")
                .replaceAll("`{3}[\\w]*\\n?", "")
                .replaceAll("`(.+?)`", "$1")
                .replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1")
                .replaceAll("^[-*+]\\s+", "• ")
                .replaceAll("\\n[-*+]\\s+", "\n• ")
                .replaceAll("^\\d+\\.\\s+", "")
                .replaceAll(">\\s*", "")
                .replaceAll("---+", "─────────────────")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    public static void setFontSize(int size) { fontSize = size; }
}
