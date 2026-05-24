package com.faqchatbot;

import javafx.scene.Scene;
import java.util.prefs.Preferences;

public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static final Preferences prefs =
            Preferences.userNodeForPackage(ThemeManager.class);
    private static Theme currentTheme = Theme.DARK;

    public static void initialize() {
        String saved = prefs.get("theme", "DARK");
        try {
            currentTheme = Theme.valueOf(saved);
        } catch (Exception e) {
            currentTheme = Theme.DARK;
        }
    }

    public static void applyTheme(Scene scene, Theme theme) {
        currentTheme = theme;
        prefs.put("theme", theme.name());
        scene.getStylesheets().clear();
        String css = ThemeManager.class
                .getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        if (theme == Theme.LIGHT) {
            String lightCss = ThemeManager.class
                    .getResource("/light.css").toExternalForm();
            scene.getStylesheets().add(lightCss);
        }
    }

    public static void applyTheme(Scene scene) {
        applyTheme(scene, currentTheme);
    }

    public static Theme getCurrentTheme() { return currentTheme; }
    public static boolean isDark() { return currentTheme == Theme.DARK; }
}