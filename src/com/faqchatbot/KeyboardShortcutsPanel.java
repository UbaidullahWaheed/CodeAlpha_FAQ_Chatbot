package com.faqchatbot;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * KeyboardShortcutsPanel — Feature 10: Keyboard Shortcuts Panel
 *
 * Displays all available keyboard shortcuts in a clean modal dialog.
 * Open it with Ctrl+? or from the Settings panel.
 */
public class KeyboardShortcutsPanel extends Stage {

    public KeyboardShortcutsPanel() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("⌨️ Keyboard Shortcuts");
        this.setResizable(false);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(0);
        root.getStyleClass().add("settings-root");
        root.setPrefWidth(480);

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("settings-header");
        header.setPadding(new Insets(18, 24, 18, 24));
        Label title = new Label("⌨️  Keyboard Shortcuts");
        title.getStyleClass().add("settings-title");
        header.getChildren().add(title);

        // Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(16, 24, 16, 24));

        content.getChildren().add(buildGroup("💬 Chat", new String[][]{
            {"Enter",         "Send message"},
            {"Shift + Enter", "New line in input"},
            {"Ctrl + Enter",  "Send message (alternative)"},
            {"Ctrl + R",      "Regenerate last response"},
            {"Ctrl + L",      "Clear chat"},
            {"Escape",        "Cancel / close dialog"},
        }));

        content.getChildren().add(buildGroup("🔍 Navigation", new String[][]{
            {"Ctrl + F",      "Search all chats"},
            {"Ctrl + N",      "New chat"},
            {"Ctrl + P",      "Open pinned messages"},
            {"Ctrl + ,",      "Open settings"},
            {"Ctrl + ?",      "Show this shortcuts panel"},
            {"Ctrl + E",      "Export chat"},
        }));

        content.getChildren().add(buildGroup("🎙 Voice & Sound", new String[][]{
            {"Ctrl + M",      "Start / stop mic recording"},
            {"Ctrl + S",      "Toggle notification sound"},
            {"Ctrl + T",      "Toggle text-to-speech"},
        }));

        content.getChildren().add(buildGroup("🌐 Language", new String[][]{
            {"Ctrl + 1",      "Switch to English"},
            {"Ctrl + 2",      "Switch to Arabic (عربي)"},
            {"Ctrl + 3",      "Switch to Urdu (اردو)"},
            {"Ctrl + 4",      "Switch to French (Français)"},
        }));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("settings-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Footer
        HBox footer = new HBox();
        footer.getStyleClass().add("settings-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 24, 12, 24));
        Button closeBtn = new Button("✓  Got it");
        closeBtn.getStyleClass().add("settings-save-btn");
        closeBtn.setOnAction(e -> this.close());
        footer.getChildren().add(closeBtn);

        root.getChildren().addAll(header, scroll, footer);

        Scene scene = new Scene(root, 480, 600);
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        this.setScene(scene);

        // Close on Escape
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) this.close();
        });
    }

    private VBox buildGroup(String groupTitle, String[][] shortcuts) {
        VBox section = new VBox(0);
        section.getStyleClass().add("settings-section");
        section.setPadding(new Insets(14, 16, 10, 16));

        Label titleLbl = new Label(groupTitle);
        titleLbl.getStyleClass().add("settings-section-title");
        titleLbl.setPadding(new Insets(0, 0, 8, 0));
        section.getChildren().add(titleLbl);

        for (String[] pair : shortcuts) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 0, 4, 0));

            // Key badge(s)
            HBox keyBox = new HBox(4);
            keyBox.setAlignment(Pos.CENTER_LEFT);
            keyBox.setMinWidth(160);
            for (String part : pair[0].split("\\+")) {
                Label kbd = new Label(part.trim());
                kbd.setStyle(
                    "-fx-background-color: rgba(88,166,255,0.12);" +
                    "-fx-border-color: rgba(88,166,255,0.35);" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;" +
                    "-fx-padding: 2px 7px;" +
                    "-fx-font-family: 'Consolas', monospace;" +
                    "-fx-font-size: 12px;"
                );
                keyBox.getChildren().add(kbd);
                if (!part.trim().equals(pair[0].split("\\+")[pair[0].split("\\+").length - 1].trim())) {
                    Label plus = new Label("+");
                    plus.getStyleClass().add("settings-desc");
                    keyBox.getChildren().add(plus);
                }
            }

            Label desc = new Label(pair[1]);
            desc.getStyleClass().add("settings-desc");

            row.getChildren().addAll(keyBox, desc);
            section.getChildren().add(row);
        }

        return section;
    }
}
