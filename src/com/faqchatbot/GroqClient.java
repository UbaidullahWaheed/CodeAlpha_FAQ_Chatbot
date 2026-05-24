package com.faqchatbot;

import com.google.gson.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class GroqClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
   
    private static final String API_KEY = "YOUR_API_KEY";
    // Available models
    public static final String[] MODELS = {
        "llama-3.3-70b-versatile",
        "llama-3.1-8b-instant",
        "mixtral-8x7b-32768",
        "gemma2-9b-it"
    };

    // AI Personas
    public static final Map<String, String> PERSONAS = new LinkedHashMap<>();
    static {
        PERSONAS.put("General Assistant",
                "You are an intelligent, friendly, and professional AI assistant. " +
                "Answer any question clearly, helpfully, and in detail.");
        PERSONAS.put("coding Expert",
                "You are an expert software engineer and coding assistant. " +
                "Provide clean, well-commented code with detailed explanations. " +
                "Always suggest best practices and point out potential issues.");
        PERSONAS.put("Science Teacher",
                "You are an enthusiastic science teacher who explains complex " +
                "scientific concepts in simple, engaging ways with real-world examples.");
        PERSONAS.put("Math Tutor",
                "You are a patient math tutor. Solve problems step by step, " +
                "explain each step clearly, and help the student understand the concept.");
        PERSONAS.put("Creative Writer",
                "You are a creative writing expert. Help with stories, poems, " +
                "scripts, and creative content with vivid imagination and style.");
        PERSONAS.put("Language Translator",
                "You are a professional multilingual translator. Translate accurately " +
                "while preserving tone, context, and cultural nuances.");
        PERSONAS.put("Doctor Advisor",
                "You are a medical information advisor. Provide general health " +
                "information clearly. Always remind users to consult a real doctor " +
                "for personal medical advice.");
        PERSONAS.put("Legal Advisor",
                "You are a legal information assistant. Explain legal concepts " +
                "clearly. Always remind users to consult a licensed attorney " +
                "for personal legal advice.");
        PERSONAS.put("Career Coach",
                "You are a professional career coach. Help with resumes, " +
                "interviews, career planning, and professional development advice.");
        PERSONAS.put("Fitness Trainer",
                "You are a certified fitness trainer and nutritionist. " +
                "Provide workout plans, nutrition advice, and healthy lifestyle tips.");
    }

    private List<Map<String, String>> conversationHistory;
    private String currentModel;
    private String currentPersona;

    public GroqClient() {
        this.currentModel = MODELS[0];
        this.currentPersona = "General Assistant";
        this.conversationHistory = new ArrayList<>();
        initializeSystemPrompt();
    }

    private void initializeSystemPrompt() {
        conversationHistory.clear();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", PERSONAS.get(currentPersona));
        conversationHistory.add(systemMessage);
    }

    public String sendMessage(String userMessage) {
        try {
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            conversationHistory.add(userMsg);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", currentModel);
            requestBody.addProperty("max_tokens", 2048);
            requestBody.addProperty("temperature", 0.7);

            JsonArray messages = new JsonArray();
            for (Map<String, String> msg : conversationHistory) {
                JsonObject msgObj = new JsonObject();
                msgObj.addProperty("role", msg.get("role"));
                msgObj.addProperty("content", msg.get("content"));
                messages.add(msgObj);
            }
            requestBody.add("messages", messages);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

            if (jsonResponse.has("error")) {
                return "⚠️ API Error: " + jsonResponse.get("error")
                        .getAsJsonObject().get("message").getAsString();
            }

            String reply = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            Map<String, String> assistantMsg = new HashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", reply);
            conversationHistory.add(assistantMsg);

            return reply;

        } catch (Exception e) {
            return "⚠️ Connection error: " + e.getMessage() +
                   "\nPlease check your internet connection and API key.";
        }
    }

    public void clearHistory() {
        initializeSystemPrompt();
    }

    public void setModel(String model) {
        this.currentModel = model;
    }

    public void setPersona(String persona) {
        this.currentPersona = persona;
        initializeSystemPrompt();
    }

    public String getCurrentModel() { return currentModel; }
    public String getCurrentPersona() { return currentPersona; }

    public static void setApiKey(String key) {
        API_KEY = key;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public String regenerateLastResponse() {
        // Remove last assistant message and resend
        if (conversationHistory.size() >= 2) {
            String lastRole = conversationHistory.get(conversationHistory.size() - 1).get("role");
            if (lastRole.equals("assistant")) {
                conversationHistory.remove(conversationHistory.size() - 1);
                String lastUserMsg = conversationHistory.get(conversationHistory.size() - 1).get("content");
                conversationHistory.remove(conversationHistory.size() - 1);
                return sendMessage(lastUserMsg);
            }
        }
        return "Nothing to regenerate.";
    }
}