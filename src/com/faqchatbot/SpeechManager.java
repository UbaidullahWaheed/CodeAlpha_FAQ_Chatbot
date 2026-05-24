package com.faqchatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SpeechManager {

    private static boolean enabled = false; // disabled by default
    private static boolean available = false;
    private static Process currentProcess;

    public static void initialize() {
        // Check if PowerShell is available (Windows)
        try {
            Process test = Runtime.getRuntime().exec(
                new String[]{"powershell", "-Command", "echo test"});
            test.waitFor();
            available = true;
            enabled = false;
        } catch (Exception e) {
            available = false;
        }
    }

    public static void speak(String text) {
        if (!available || !enabled) return;

        Thread speechThread = new Thread(() -> {
            try {
                stop(); // Stop any current speech

                // Clean text for speech
                String cleanText = text
                    .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                    .replaceAll("\\*(.+?)\\*", "$1")
                    .replaceAll("`(.+?)`", "$1")
                    .replaceAll("#+ ", "")
                    .replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1")
                    .replaceAll("[^a-zA-Z0-9\\s.,!?;:'-]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

                // Limit length
                if (cleanText.length() > 500) {
                    cleanText = cleanText.substring(0, 500) + "...";
                }

                // Use PowerShell text-to-speech (Windows built-in)
                String psScript =
                    "Add-Type -AssemblyName System.Speech;" +
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer;" +
                    "$speak.Rate = 1;" +
                    "$speak.Volume = 100;" +
                    "$speak.Speak('" + cleanText.replace("'", " ") + "');";

                currentProcess = Runtime.getRuntime().exec(
                    new String[]{"powershell", "-Command", psScript});
                currentProcess.waitFor();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        speechThread.setDaemon(true);
        speechThread.start();
    }

    public static void stop() {
        try {
            if (currentProcess != null && currentProcess.isAlive()) {
                currentProcess.destroyForcibly();
                // Kill any remaining powershell speech processes
                Runtime.getRuntime().exec(
                    new String[]{"powershell", "-Command",
                    "Get-Process -Name powershell | Stop-Process -Force"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setEnabled(boolean val) { enabled = val; }
    public static boolean isEnabled() { return enabled; }
    public static boolean isAvailable() { return available; }
    public static void shutdown() { stop(); }
}