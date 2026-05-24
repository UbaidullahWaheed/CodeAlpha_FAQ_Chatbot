package com.faqchatbot;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static final String SAVE_DIR = System.getProperty("user.home") + "/AIAssistantPro/sessions/";
    private static List<ChatSession> sessions = new ArrayList<>();
    private static ChatSession currentSession;

    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            loadAllSessions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChatSession createNewSession() {
        ChatSession session = new ChatSession();
        sessions.add(0, session);
        currentSession = session;
        return session;
    }

    public static void saveSession(ChatSession session) {
        try {
            String filePath = SAVE_DIR + session.getId() + ".json";
            Files.writeString(Paths.get(filePath), session.toJSON().toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSession(ChatSession session) {
        try {
            Files.deleteIfExists(Paths.get(SAVE_DIR + session.getId() + ".json"));
            sessions.remove(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAllSessions() {
        sessions.clear();
        File dir = new File(SAVE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return;
        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                ChatSession session = ChatSession.fromJSON(new JSONObject(content));
                sessions.add(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sessions.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
    }

    public static List<ChatSession> getSessions() { return sessions; }
    public static ChatSession getCurrentSession() { return currentSession; }
    public static void setCurrentSession(ChatSession session) { currentSession = session; }
}