package com.example.chatbot.service.impl;

import com.example.chatbot.service.api.EmbeddingProvider;
import com.example.chatbot.service.api.client.OpenAIFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("openai")
public class OpenAIEmbeddingProvider implements EmbeddingProvider {

    private final OpenAIFeignClient client;
    private final String apiKey;

    public OpenAIEmbeddingProvider(OpenAIFeignClient client, @Value("${openai.api.key}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public float[] getEmbedding(String text) {
        Map<String,Object> request = Map.of(
                "model", "text-embedding-3-small",
                "input", text
        );

        Map<String,Object> response = client.getEmbedding(request, "Bearer " + apiKey);
        List<Double> embedding = (List<Double>) ((Map<String,Object>) ((List<Object>) response.get("data")).get(0)).get("embedding");

        float[] floatEmbedding = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) floatEmbedding[i] = embedding.get(i).floatValue();
        return floatEmbedding;
    }

    @Override
    public String getName() {
        return "openai";
    }
}
