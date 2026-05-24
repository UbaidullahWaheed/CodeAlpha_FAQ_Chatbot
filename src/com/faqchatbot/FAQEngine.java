package com.faqchatbot;

import java.util.*;

public class FAQEngine {

    private Map<String, String> faqs;
    private List<String> questions;
    private List<Map<String, Double>> tfidfVectors;
    private Map<String, Double> idfScores;

    // Common stopwords to ignore
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "it", "in", "on", "at", "to", "for",
            "of", "and", "or", "but", "are", "was", "were", "be", "been",
            "has", "have", "had", "do", "does", "did", "will", "would",
            "can", "could", "should", "may", "might", "i", "my", "me",
            "we", "our", "you", "your", "he", "she", "they", "their",
            "this", "that", "what", "how", "when", "where", "which", "who"
    ));

    public FAQEngine() {
        this.faqs = FAQData.getFAQs();
        this.questions = new ArrayList<>(faqs.keySet());
        this.tfidfVectors = new ArrayList<>();
        this.idfScores = new HashMap<>();

        buildTFIDF();
    }

    // ---------- Tokenizer ----------
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
        for (String word : words) {
            if (!word.isEmpty() && !STOPWORDS.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    // ---------- Term Frequency ----------
    private Map<String, Double> computeTF(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        for (String token : tokens) {
            tf.put(token, tf.getOrDefault(token, 0.0) + 1.0);
        }
        int total = tokens.size();
        if (total > 0) {
            for (String key : tf.keySet()) {
                tf.put(key, tf.get(key) / total);
            }
        }
        return tf;
    }

    // ---------- Inverse Document Frequency ----------
    private void computeIDF(List<List<String>> allTokens) {
        int totalDocs = allTokens.size();
        Map<String, Integer> docCount = new HashMap<>();

        for (List<String> tokens : allTokens) {
            Set<String> unique = new HashSet<>(tokens);
            for (String token : unique) {
                docCount.put(token, docCount.getOrDefault(token, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : docCount.entrySet()) {
            idfScores.put(entry.getKey(),
                    Math.log((double) totalDocs / (1.0 + entry.getValue())));
        }
    }

    // ---------- Build TF-IDF for all FAQs ----------
    private void buildTFIDF() {
        List<List<String>> allTokens = new ArrayList<>();

        for (String question : questions) {
            allTokens.add(tokenize(question));
        }

        computeIDF(allTokens);

        for (List<String> tokens : allTokens) {
            Map<String, Double> tf = computeTF(tokens);
            Map<String, Double> tfidf = new HashMap<>();
            for (Map.Entry<String, Double> entry : tf.entrySet()) {
                double idf = idfScores.getOrDefault(entry.getKey(), 0.0);
                tfidf.put(entry.getKey(), entry.getValue() * idf);
            }
            tfidfVectors.add(tfidf);
        }
    }

    // ---------- Cosine Similarity ----------
    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            if (vec2.containsKey(entry.getKey())) {
                dotProduct += entry.getValue() * vec2.get(entry.getKey());
            }
            norm1 += entry.getValue() * entry.getValue();
        }

        for (double val : vec2.values()) {
            norm2 += val * val;
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // ---------- Get Best Answer ----------
    public String getBestAnswer(String userInput) {
        List<String> userTokens = tokenize(userInput);

        if (userTokens.isEmpty()) {
            return "Please type a proper question so I can help you! 😊";
        }

        // Build TF-IDF vector for user input
        Map<String, Double> userTF = computeTF(userTokens);
        Map<String, Double> userVector = new HashMap<>();
        for (Map.Entry<String, Double> entry : userTF.entrySet()) {
            double idf = idfScores.getOrDefault(entry.getKey(), 0.0);
            userVector.put(entry.getKey(), entry.getValue() * idf);
        }

        // Find best matching FAQ
        double bestScore = -1.0;
        int bestIndex = -1;

        for (int i = 0; i < tfidfVectors.size(); i++) {
            double score = cosineSimilarity(userVector, tfidfVectors.get(i));
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        // Threshold — if no good match found
        if (bestScore < 0.1 || bestIndex == -1) {
            return "I'm sorry, I couldn't find a matching answer. Please try rephrasing your question or contact the university helpdesk. 📞";
        }

        return faqs.get(questions.get(bestIndex));
    }
}