package com.faqchatbot;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class Sidebar extends VBox {

    private VBox sessionList;
    private Consumer<ChatSession> onSessionSelected;
    private Consumer<Void> onNewChat;
    private ChatSession activeSession;

    public Sidebar(Consumer<ChatSession> onSessionSelected, Consumer<Void> onNewChat) {
        this.onSessionSelected = onSessionSelected;
        this.onNewChat = onNewChat;
        buildUI();
        refresh();
    }

    private void buildUI() {
        this.setPrefWidth(220);
        this.setMinWidth(220);
        this.setMaxWidth(220);
        this.getStyleClass().add("sidebar");
        this.setSpacing(0);

        // Header
        VBox header = new VBox(6);
        header.setPadding(new Insets(20, 14, 14, 14));
        header.getStyleClass().add("sidebar-header");

        Label appTitle = new Label("🤖 AI Assistant");
        appTitle.getStyleClass().add("sidebar-app-title");

        Label appVersion = new Label("Pro · International Edition");
        appVersion.getStyleClass().add("sidebar-app-version");

        // New Chat button
        Button newChatBtn = new Button("＋  New Chat");
        newChatBtn.getStyleClass().add("new-chat-button");
        newChatBtn.setMaxWidth(Double.MAX_VALUE);
        newChatBtn.setOnAction(e -> onNewChat.accept(null));

        header.getChildren().addAll(appTitle, appVersion, newChatBtn);

        // Sessions label
        Label sessionsLabel = new Label("RECENT CHATS");
        sessionsLabel.getStyleClass().add("sidebar-section-label");
        sessionsLabel.setPadding(new Insets(14, 14, 6, 14));

        // Session list
        sessionList = new VBox(2);
        sessionList.setPadding(new Insets(0, 8, 8, 8));

        ScrollPane scrollPane = new ScrollPane(sessionList);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("sidebar-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Bottom buttons
        VBox bottomSection = new VBox(4);
        bottomSection.setPadding(new Insets(8, 8, 12, 8));
        bottomSection.getStyleClass().add("sidebar-bottom");

        this.getChildren().addAll(header, sessionsLabel, scrollPane, bottomSection);
    }

    public void refresh() {
        sessionList.getChildren().clear();
        for (ChatSession session : SessionManager.getSessions()) {
            sessionList.getChildren().add(createSessionItem(session));
        }
    }

    private HBox createSessionItem(ChatSession session) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 10, 8, 10));
        item.getStyleClass().add("session-item");

        if (session == activeSession) {
            item.getStyleClass().add("session-item-active");
        }

        Label icon = new Label("💬");
        icon.setStyle("-fx-font-size: 13px;");

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label titleLabel = new Label(session.getTitle());
        titleLabel.getStyleClass().add("session-title");
        titleLabel.setMaxWidth(130);

        Label dateLabel = new Label(session.getCreatedAt());
        dateLabel.getStyleClass().add("session-date");

        textBox.getChildren().addAll(titleLabel, dateLabel);

        Button deleteBtn = new Button("✕");
        deleteBtn.getStyleClass().add("session-delete-btn");
        deleteBtn.setOnAction(e -> {
            SessionManager.deleteSession(session);
            refresh();
        });

        item.getChildren().addAll(icon, textBox, deleteBtn);
        item.setOnMouseClicked(e -> {
            activeSession = session;
            onSessionSelected.accept(session);
            refresh();
        });

        return item;
    }

    public void setActiveSession(ChatSession session) {
        this.activeSession = session;
        refresh();
    }
}