package com.faqchatbot;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileHandler {

    // Export chat as TXT
    public static void exportAsTXT(ChatSession session, Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Chat as TXT");
        chooser.setInitialFileName("chat_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("═══════════════════════════════════════");
            writer.println("  AI Assistant Pro — Chat Export");
            writer.println("  Session: " + session.getTitle());
            writer.println("  Date: " + session.getCreatedAt());
            writer.println("  Model: " + session.getModel());
            writer.println("═══════════════════════════════════════\n");

            for (ChatSession.Message msg : session.getMessages()) {
                String role = msg.role.equals("user") ? "You" : "AI Assistant";
                writer.println("[" + msg.timestamp + "] " + role + ":");
                writer.println(msg.content);
                writer.println("───────────────────────────────────────\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Export chat as HTML
    public static void exportAsHTML(ChatSession session, Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Chat as HTML");
        chooser.setInitialFileName("chat_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("HTML Files", "*.html"));

        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("<!DOCTYPE html><html><head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Chat Export — " + session.getTitle() + "</title>");
            writer.println("<style>");
            writer.println("body{font-family:'Segoe UI',sans-serif;background:#0d1117;color:#e6edf3;max-width:800px;margin:0 auto;padding:20px;}");
            writer.println("h1{color:#58a6ff;border-bottom:1px solid #30363d;padding-bottom:10px;}");
            writer.println(".meta{color:#8b949e;font-size:12px;margin-bottom:20px;}");
            writer.println(".msg{margin:16px 0;padding:12px 16px;border-radius:12px;}");
            writer.println(".user{background:#1f6feb;margin-left:80px;}");
            writer.println(".bot{background:#161b22;border:1px solid #30363d;margin-right:80px;}");
            writer.println(".role{font-weight:bold;font-size:11px;margin-bottom:6px;color:#8b949e;}");
            writer.println(".time{font-size:10px;color:#484f58;margin-top:6px;}");
            writer.println("code{background:#0d1117;color:#79c0ff;padding:2px 6px;border-radius:4px;}");
            writer.println("pre{background:#0d1117;padding:12px;border-radius:8px;overflow-x:auto;}");
            writer.println("</style></head><body>");
            writer.println("<h1>🤖 AI Assistant Pro</h1>");
            writer.println("<div class='meta'>Session: " + session.getTitle() +
                    " &nbsp;|&nbsp; " + session.getCreatedAt() +
                    " &nbsp;|&nbsp; Model: " + session.getModel() + "</div>");

            for (ChatSession.Message msg : session.getMessages()) {
                String cssClass = msg.role.equals("user") ? "user" : "bot";
                String role = msg.role.equals("user") ? "👤 You" : "🤖 AI Assistant";
                writer.println("<div class='msg " + cssClass + "'>");
                writer.println("<div class='role'>" + role + "</div>");
                writer.println("<div>" + msg.content.replace("\n", "<br>") + "</div>");
                writer.println("<div class='time'>" + msg.timestamp + "</div>");
                writer.println("</div>");
            }

            writer.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read uploaded text file
    public static String readTextFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return null;
        }
    }

    // Read uploaded PDF (basic text extraction)
    public static String readFileForAI(File file, Stage stage) {
        if (file == null) return null;
        String name = file.getName().toLowerCase();
        if (name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".java")
                || name.endsWith(".py") || name.endsWith(".json") || name.endsWith(".csv")) {
            return readTextFile(file);
        }
        return null;
    }

    // Show file picker
    public static File pickFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Files",
                        "*.txt", "*.md", "*.java", "*.py", "*.json", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return chooser.showOpenDialog(stage);
    }
}