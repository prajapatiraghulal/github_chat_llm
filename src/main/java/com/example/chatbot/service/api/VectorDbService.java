package com.example.chatbot.service.api;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VectorDbService {
    List<String> searchRelevantDocs(float[] queryEmbedding);
}
