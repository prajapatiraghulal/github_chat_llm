package com.example.chatbot.service.api;

public interface EmbeddingProvider {
    float[] getEmbedding(String text);
    String getName(); // "openai", "google", etc.
}

