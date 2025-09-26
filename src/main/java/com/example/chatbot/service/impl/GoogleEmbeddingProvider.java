package com.example.chatbot.service.impl;

import com.example.chatbot.model.GeminiEmbeddingRequest;
import com.example.chatbot.service.api.EmbeddingProvider;
import com.example.chatbot.service.api.client.GoogleFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("google")
public class GoogleEmbeddingProvider implements EmbeddingProvider {

    private final GoogleFeignClient client;
    private final String apiKey;

    public GoogleEmbeddingProvider(GoogleFeignClient client,
                                   @Value("${google.api.key}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public float[] getEmbedding(String text) {
        // Build request body
        GeminiEmbeddingRequest request = GeminiEmbeddingRequest.builder()
                .model("models/gemini-embedding-001")
                .content(GeminiEmbeddingRequest.Content.builder()
                        .parts(List.of(new GeminiEmbeddingRequest.Part(text)))
                        .build())
                .build();

        // Call API
        Map<String, Object> response = client.getEmbedding(request, apiKey);

        // Navigate response: embedding.values
        Map<String, Object> embeddingObj = (Map<String, Object>) response.get("embedding");
        List<Double> values = (List<Double>) embeddingObj.get("values");

        // Convert List<Double> → float[]
        float[] floatEmbedding = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            floatEmbedding[i] = values.get(i).floatValue();
        }

        return floatEmbedding;
    }

    @Override
    public String getName() {
        return "google";
    }
}

