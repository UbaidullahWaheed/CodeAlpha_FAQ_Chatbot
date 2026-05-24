package com.faqchatbot;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * SettingsPanel — Enhanced with:
 *   Feature 6  : Notification Sound toggle
 *   Feature 9  : Language selector (English / Arabic / Urdu / French)
 *   Feature 10 : "View keyboard shortcuts" link
 */
public class SettingsPanel extends Stage {

    private ChatController chatController;
    private Scene mainScene;

    public SettingsPanel(ChatController chatController, Scene mainScene) {
        this.chatController = chatController;
        this.mainScene = mainScene;
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(I18nManager.t("settings.title"));
        this.setResizable(false);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(0);
        root.getStyleClass().add("settings-root");
        root.setPrefWidth(520);

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("settings-header");
        header.setPadding(new Insets(20, 24, 20, 24));
        Label title = new Label(I18nManager.t("settings.title"));
        title.getStyleClass().add("settings-title");
        header.getChildren().add(title);

        // Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 24, 20, 24));

        content.getChildren().add(createSection("🔑 API Configuration", createAPIKeySection()));
        content.getChildren().add(createSection("🧠 AI Model",          createModelSection()));
        content.getChildren().add(createSection("🎭 AI Persona",        createPersonaSection()));
        content.getChildren().add(createSection("🎨 Theme",             createThemeSection()));
        content.getChildren().add(createSection("🔊 Text to Speech",    createSpeechSection()));
        content.getChildren().add(createSection(I18nManager.t("settings.notif"), createNotifSoundSection())); // Feature 6
        content.getChildren().add(createSection("🔠 Font Size",         createFontSection()));
        content.getChildren().add(createSection(I18nManager.t("settings.lang"), createLanguageSection()));    // Feature 9

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("settings-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Footer
        HBox footer = new HBox(12);
        footer.getStyleClass().add("settings-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(14, 24, 14, 24));

        // Feature 10: keyboard shortcuts link
        Button shortcutsBtn = new Button("⌨️ Keyboard Shortcuts");
        shortcutsBtn.getStyleClass().add("regen-button");
        shortcutsBtn.setOnAction(e -> new KeyboardShortcutsPanel().show());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button(I18nManager.t("settings.save"));
        closeBtn.getStyleClass().add("settings-save-btn");
        closeBtn.setOnAction(e -> this.close());

        footer.getChildren().addAll(shortcutsBtn, spacer, closeBtn);

        root.getChildren().addAll(header, scrollPane, footer);

        Scene scene = new Scene(root, 520, 620);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        this.setScene(scene);
    }

    private VBox createSection(String title, Node content) {
        VBox section = new VBox(10);
        section.getStyleClass().add("settings-section");
        section.setPadding(new Insets(16));
        Label label = new Label(title);
        label.getStyleClass().add("settings-section-title");
        section.getChildren().addAll(label, content);
        return section;
    }

    // ── API Key ───────────────────────────────────────────────────────────────
    private Node createAPIKeySection() {
        VBox box = new VBox(8);
        Label desc = new Label("Enter your Groq API key to enable AI responses.");
        desc.getStyleClass().add("settings-desc");

        PasswordField keyField = new PasswordField();
        keyField.setText(GroqClient.getApiKey());
        keyField.getStyleClass().add("settings-input");
        keyField.setPromptText("gsk_...");

        TextField visibleField = new TextField(GroqClient.getApiKey());
        visibleField.getStyleClass().add("settings-input");
        visibleField.setVisible(false);
        visibleField.setManaged(false);

        Button showBtn = new Button("👁 Show");
        showBtn.getStyleClass().add("settings-small-btn");
        showBtn.setOnAction(e -> {
            boolean showing = visibleField.isVisible();
            visibleField.setVisible(!showing);
            visibleField.setManaged(!showing);
            keyField.setVisible(showing);
            keyField.setManaged(showing);
            showBtn.setText(showing ? "👁 Show" : "🙈 Hide");
        });

        keyField.textProperty().addListener((o, ov, nv) -> GroqClient.setApiKey(nv));
        visibleField.textProperty().addListener((o, ov, nv) -> {
            GroqClient.setApiKey(nv);
            keyField.setText(nv);
        });

        HBox row = new HBox(8);
        HBox.setHgrow(keyField, Priority.ALWAYS);
        HBox.setHgrow(visibleField, Priority.ALWAYS);
        row.getChildren().addAll(keyField, visibleField, showBtn);
        box.getChildren().addAll(desc, row);
        return box;
    }

    // ── Model ─────────────────────────────────────────────────────────────────
    private Node createModelSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Choose the AI model for responses.");
        desc.getStyleClass().add("settings-desc");
        box.getChildren().add(desc);

        ToggleGroup group = new ToggleGroup();
        for (String model : GroqClient.MODELS) {
            RadioButton rb = new RadioButton(model);
            rb.setToggleGroup(group);
            rb.getStyleClass().add("settings-radio");
            if (model.equals(chatController.getCurrentModel())) rb.setSelected(true);
            rb.setOnAction(e -> chatController.setModel(model));
            box.getChildren().add(rb);
        }
        return box;
    }

    // ── Persona ───────────────────────────────────────────────────────────────
    private Node createPersonaSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Select the AI personality/role.");
        desc.getStyleClass().add("settings-desc");

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(GroqClient.PERSONAS.keySet());
        combo.setValue(chatController.getCurrentPersona());
        combo.getStyleClass().add("settings-combo");
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setOnAction(e -> chatController.setPersona(combo.getValue()));
        box.getChildren().addAll(desc, combo);
        return box;
    }

    // ── Theme ─────────────────────────────────────────────────────────────────
    private Node createThemeSection() {
        VBox box = new VBox(8);
        ToggleGroup group = new ToggleGroup();

        RadioButton darkBtn  = new RadioButton("🌙 Dark");
        darkBtn.setToggleGroup(group);
        darkBtn.getStyleClass().add("settings-radio");
        darkBtn.setSelected(ThemeManager.isDark());

        RadioButton lightBtn = new RadioButton("☀️ Light");
        lightBtn.setToggleGroup(group);
        lightBtn.getStyleClass().add("settings-radio");
        lightBtn.setSelected(!ThemeManager.isDark());

        darkBtn.setOnAction(e  -> ThemeManager.applyTheme(mainScene, ThemeManager.Theme.DARK));
        lightBtn.setOnAction(e -> ThemeManager.applyTheme(mainScene, ThemeManager.Theme.LIGHT));

        box.getChildren().addAll(darkBtn, lightBtn);
        return box;
    }

    // ── TTS ───────────────────────────────────────────────────────────────────
    private Node createSpeechSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Read AI responses aloud automatically.");
        desc.getStyleClass().add("settings-desc");

        CheckBox toggle = new CheckBox("Enable Text to Speech");
        toggle.setSelected(SpeechManager.isEnabled());
        toggle.getStyleClass().add("settings-radio");
        toggle.selectedProperty().addListener((o, ov, nv) -> SpeechManager.setEnabled(nv));
        box.getChildren().addAll(desc, toggle);
        return box;
    }

    // ── Feature 6: Notification Sound ─────────────────────────────────────────
    private Node createNotifSoundSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Play a short ping when AI finishes responding.");
        desc.getStyleClass().add("settings-desc");

        CheckBox toggle = new CheckBox("Enable notification ping");
        toggle.setSelected(NotificationSoundManager.isEnabled());
        toggle.getStyleClass().add("settings-radio");
        toggle.selectedProperty().addListener((o, ov, nv) ->
                NotificationSoundManager.setEnabled(nv));

        Button testBtn = new Button("▶ Test sound");
        testBtn.getStyleClass().add("settings-small-btn");
        testBtn.setOnAction(e -> NotificationSoundManager.ping());

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(toggle, testBtn);

        box.getChildren().addAll(desc, row);
        return box;
    }

    // ── Font size ─────────────────────────────────────────────────────────────
    private Node createFontSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Adjust the chat text size.");
        desc.getStyleClass().add("settings-desc");

        Slider slider = new Slider(11, 20, 13);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(3);
        slider.setSnapToTicks(true);

        Label sizeLabel = new Label("13px");
        sizeLabel.getStyleClass().add("settings-desc");

        slider.valueProperty().addListener((obs, ov, nv) -> {
            int size = nv.intValue();
            sizeLabel.setText(size + "px");
            chatController.setFontSize(size);
        });

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(slider, Priority.ALWAYS);
        row.getChildren().addAll(slider, sizeLabel);

        box.getChildren().addAll(desc, row);
        return box;
    }

    // ── Feature 9: Language ───────────────────────────────────────────────────
    private Node createLanguageSection() {
        VBox box = new VBox(8);
        Label desc = new Label("Choose the UI language. RTL scripts are supported.");
        desc.getStyleClass().add("settings-desc");

        ToggleGroup group = new ToggleGroup();
        for (I18nManager.Language lang : I18nManager.getAllLanguages()) {
            RadioButton rb = new RadioButton(lang.displayName
                    + (lang.rtl ? "  ⟵ RTL" : ""));
            rb.setToggleGroup(group);
            rb.getStyleClass().add("settings-radio");
            if (lang == I18nManager.getLanguage()) rb.setSelected(true);
            rb.setOnAction(e -> I18nManager.setLanguage(lang));
            box.getChildren().add(rb);
        }

        box.getChildren().add(0, desc);
        return box;
    }
}
