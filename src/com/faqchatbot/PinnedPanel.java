package com.faqchatbot;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * PinnedPanel — Feature 4: Pin Messages
 *
 * A modal window listing all pinned messages. Opens from the header bar.
 */
public class PinnedPanel extends Stage {

    private VBox listBox;

    public PinnedPanel() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("📍 Pinned Messages");
        this.setResizable(false);
        buildUI();
        // Live-refresh when pins change while window is open
        PinManager.addListener(this::refresh);
        this.setOnHidden(e -> PinManager.removeListener(this::refresh));
    }

    private void buildUI() {
        VBox root = new VBox(0);
        root.getStyleClass().add("settings-root");
        root.setPrefWidth(480);

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("settings-header");
        header.setPadding(new Insets(18, 24, 18, 24));
        Label title = new Label("📍  Pinned Messages");
        title.getStyleClass().add("settings-title");
        header.getChildren().add(title);

        // List
        listBox = new VBox(10);
        listBox.setPadding(new Insets(16, 20, 16, 20));

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("settings-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Footer
        HBox footer = new HBox();
        footer.getStyleClass().add("settings-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 24, 12, 24));
        Button closeBtn = new Button("✕  Close");
        closeBtn.getStyleClass().add("settings-save-btn");
        closeBtn.setOnAction(e -> this.close());
        footer.getChildren().add(closeBtn);

        root.getChildren().addAll(header, scroll, footer);

        Scene scene = new Scene(root, 480, 520);
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        this.setScene(scene);

        refresh();
    }

    private void refresh() {
        listBox.getChildren().clear();

        if (PinManager.count() == 0) {
            Label empty = new Label("No pinned messages yet.\nClick 📌 on any message to pin it.");
            empty.getStyleClass().add("settings-desc");
            empty.setAlignment(Pos.CENTER);
            empty.setWrapText(true);
            empty.setMaxWidth(Double.MAX_VALUE);
            listBox.getChildren().add(empty);
            return;
        }

        for (PinManager.PinnedMessage pm : PinManager.getPins()) {
            listBox.getChildren().add(createPinCard(pm));
        }
    }

    private VBox createPinCard(PinManager.PinnedMessage pm) {
        VBox card = new VBox(6);
        card.getStyleClass().add("settings-section");
        card.setPadding(new Insets(12, 14, 12, 14));

        // Role badge + time
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label roleBadge = new Label(pm.type == ChatBubble.BubbleType.USER ? "👤 You" : "🤖 AI");
        roleBadge.getStyleClass().add("settings-section-title");
        Label timeLabel = new Label("Pinned at " + pm.pinnedAt);
        timeLabel.getStyleClass().add("session-date");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Unpin button
        Button unpinBtn = new Button("✕ Unpin");
        unpinBtn.getStyleClass().add("session-delete-btn");
        unpinBtn.setOnAction(e -> PinManager.unpin(pm.content));

        topRow.getChildren().addAll(roleBadge, timeLabel, spacer, unpinBtn);

        // Content preview (max 3 lines)
        String preview = pm.content.length() > 200
                ? pm.content.substring(0, 200) + "…"
                : pm.content;
        Label contentLabel = new Label(preview);
        contentLabel.getStyleClass().add("settings-desc");
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(topRow, contentLabel);
        return card;
    }
}
