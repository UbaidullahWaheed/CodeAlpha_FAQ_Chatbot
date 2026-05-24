package com.faqchatbot;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ChatSearchPanel — Feature 3: Chat Search
 *
 * A modal window that searches across ALL sessions' messages in real-time.
 * Clicking a result loads that session into the main ChatController.
 */
public class ChatSearchPanel extends Stage {

    private TextField searchField;
    private VBox resultsList;
    private Label resultCount;
    private final Consumer<ChatSession> onSessionSelected;

    public ChatSearchPanel(Consumer<ChatSession> onSessionSelected) {
        this.onSessionSelected = onSessionSelected;
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(I18nManager.t("search.title"));
        this.setResizable(false);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(0);
        root.getStyleClass().add("settings-root");
        root.setPrefWidth(540);

        // Header
        HBox header = new HBox(10);
        header.getStyleClass().add("settings-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        Label title = new Label(I18nManager.t("search.title"));
        title.getStyleClass().add("settings-title");
        resultCount = new Label("");
        resultCount.getStyleClass().add("session-date");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, resultCount);

        // Search bar
        HBox searchRow = new HBox(8);
        searchRow.setPadding(new Insets(14, 20, 10, 20));
        searchRow.setAlignment(Pos.CENTER);
        searchField = new TextField();
        searchField.setPromptText(I18nManager.t("search.placeholder"));
        searchField.getStyleClass().add("settings-input");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        if (I18nManager.isRTL()) {
            searchField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
        searchField.textProperty().addListener((obs, ov, nv) -> doSearch(nv.trim()));
        searchRow.getChildren().add(searchField);

        // Results
        resultsList = new VBox(8);
        resultsList.setPadding(new Insets(4, 20, 16, 20));

        ScrollPane scroll = new ScrollPane(resultsList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("settings-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Footer
        HBox footer = new HBox();
        footer.getStyleClass().add("settings-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 24, 12, 24));
        Button closeBtn = new Button("✕  " + I18nManager.t("pinned.empty").split("\n")[0].substring(0,0) + "Close");
        closeBtn.setText("✕  Close");
        closeBtn.getStyleClass().add("settings-save-btn");
        closeBtn.setOnAction(e -> this.close());
        footer.getChildren().add(closeBtn);

        root.getChildren().addAll(header, searchRow, scroll, footer);

        Scene scene = new Scene(root, 540, 560);
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        this.setScene(scene);

        // Show all sessions by default
        doSearch("");
        javafx.application.Platform.runLater(() -> searchField.requestFocus());
    }

    // ── Search logic ──────────────────────────────────────────────────────────
    private void doSearch(String query) {
        resultsList.getChildren().clear();
        List<SearchResult> hits = new ArrayList<>();

        for (ChatSession session : SessionManager.getSessions()) {
            for (ChatSession.Message msg : session.getMessages()) {
                if (query.isEmpty() || containsIgnoreCase(msg.content, query)) {
                    hits.add(new SearchResult(session, msg, query));
                }
            }
        }

        resultCount.setText(hits.isEmpty() ? "" : hits.size() + " result" + (hits.size() == 1 ? "" : "s"));

        if (hits.isEmpty()) {
            Label none = new Label(query.isEmpty() ? "Type to search across all chats." 
                                                   : I18nManager.t("search.noresult"));
            none.getStyleClass().add("settings-desc");
            none.setWrapText(true);
            resultsList.getChildren().add(none);
            return;
        }

        for (SearchResult r : hits) {
            resultsList.getChildren().add(createResultCard(r, query));
        }
    }

    private boolean containsIgnoreCase(String haystack, String needle) {
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }

    private VBox createResultCard(SearchResult r, String query) {
        VBox card = new VBox(5);
        card.getStyleClass().add("settings-section");
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setCursor(javafx.scene.Cursor.HAND);

        // Session title + role badge
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label sessionLbl = new Label("💬 " + r.session.getTitle());
        sessionLbl.getStyleClass().add("settings-section-title");
        Label roleLbl = new Label(r.msg.role.equals("user") ? "👤" : "🤖");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label dateLbl = new Label(r.msg.timestamp);
        dateLbl.getStyleClass().add("session-date");
        topRow.getChildren().addAll(roleLbl, sessionLbl, sp, dateLbl);

        // Highlighted snippet
        String snippet = buildSnippet(r.msg.content, query, 120);
        Label snippetLbl = new Label(snippet);
        snippetLbl.getStyleClass().add("settings-desc");
        snippetLbl.setWrapText(true);
        snippetLbl.setMaxWidth(Double.MAX_VALUE);
        if (I18nManager.isRTL()) {
            snippetLbl.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        card.getChildren().addAll(topRow, snippetLbl);
        card.setOnMouseClicked(e -> {
            onSessionSelected.accept(r.session);
            this.close();
        });

        // Hover highlight
        card.setOnMouseEntered(ev -> card.setStyle("-fx-background-color: rgba(31,111,235,0.1); -fx-background-radius: 8px;"));
        card.setOnMouseExited(ev  -> card.setStyle(""));

        return card;
    }

    /** Build a short snippet centred around the first occurrence of the query. */
    private String buildSnippet(String text, String query, int maxLen) {
        if (query.isEmpty()) {
            return text.length() > maxLen ? text.substring(0, maxLen) + "…" : text;
        }
        int idx = text.toLowerCase().indexOf(query.toLowerCase());
        if (idx < 0) return text.length() > maxLen ? text.substring(0, maxLen) + "…" : text;
        int start = Math.max(0, idx - 30);
        int end   = Math.min(text.length(), idx + query.length() + 90);
        String snip = (start > 0 ? "…" : "") + text.substring(start, end) + (end < text.length() ? "…" : "");
        // Simple uppercase of match (JavaFX Label doesn't support inline styling)
        return snip.replace(
                text.substring(idx, idx + query.length()),
                "【" + text.substring(idx, idx + query.length()) + "】");
    }

    // ── Inner result model ────────────────────────────────────────────────────
    private static class SearchResult {
        final ChatSession session;
        final ChatSession.Message msg;
        final String query;

        SearchResult(ChatSession session, ChatSession.Message msg, String query) {
            this.session = session;
            this.msg     = msg;
            this.query   = query;
        }
    }
}
