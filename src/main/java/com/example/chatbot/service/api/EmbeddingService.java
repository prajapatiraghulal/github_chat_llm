package com.example.chatbot.service.api;

import org.springframework.stereotype.Service;

@Service
public interface EmbeddingService {
    float[] getEmbedding(String text);
}
