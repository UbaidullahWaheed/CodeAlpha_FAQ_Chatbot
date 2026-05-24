package com.faqchatbot;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatSession {

    public static class Message {
        public String role;
        public String content;
        public String timestamp;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("hh:mm a · MMM dd"));
        }

        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.put("role", role);
            obj.put("content", content);
            obj.put("timestamp", timestamp);
            return obj;
        }

        public static Message fromJSON(JSONObject obj) {
            Message msg = new Message(
                    obj.getString("role"),
                    obj.getString("content"));
            msg.timestamp = obj.optString("timestamp", "");
            return msg;
        }
    }

    private String id;
    private String title;
    private String createdAt;
    private List<Message> messages;
    private String model;
    private String persona;

    public ChatSession() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a"));
        this.messages = new ArrayList<>();
        this.title = "New Chat";
        this.model = "llama-3.3-70b-versatile";
        this.persona = "General Assistant";
    }

    public void addMessage(String role, String content) {
        messages.add(new Message(role, content));
        // Auto generate title from first user message
        if (messages.size() == 1 && role.equals("user")) {
            title = content.length() > 40
                    ? content.substring(0, 40) + "..."
                    : content;
        }
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("createdAt", createdAt);
        obj.put("model", model);
        obj.put("persona", persona);
        JSONArray msgsArray = new JSONArray();
        for (Message msg : messages) {
            msgsArray.put(msg.toJSON());
        }
        obj.put("messages", msgsArray);
        return obj;
    }

    public static ChatSession fromJSON(JSONObject obj) {
        ChatSession session = new ChatSession();
        session.id = obj.getString("id");
        session.title = obj.getString("title");
        session.createdAt = obj.optString("createdAt", "");
        session.model = obj.optString("model", "llama-3.3-70b-versatile");
        session.persona = obj.optString("persona", "General Assistant");
        session.messages = new ArrayList<>();
        JSONArray msgsArray = obj.optJSONArray("messages");
        if (msgsArray != null) {
            for (int i = 0; i < msgsArray.length(); i++) {
                session.messages.add(Message.fromJSON(msgsArray.getJSONObject(i)));
            }
        }
        return session;
    }

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCreatedAt() { return createdAt; }
    public List<Message> getMessages() { return messages; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getPersona() { return persona; }
    public void setPersona(String persona) { this.persona = persona; }

    public void clearMessages() {
        messages.clear();
        title = "New Chat";
    }
}