package com.example.chatbot.service.impl;

import com.example.chatbot.service.api.LlmService;
import com.example.chatbot.service.api.client.GoogleFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LlmServiceImpl implements LlmService {

    private final GoogleFeignClient googleFeignClient;


    private final String apiKey;

    public LlmServiceImpl(GoogleFeignClient googleFeignClient, @Value("${google.api.key}") String apiKey) {
        this.googleFeignClient = googleFeignClient;
        this.apiKey = apiKey;
    }

    @Override
    public String askLLM(String prompt) {
        // Build request body
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        // Call Feign client
        Map<String, Object> response = googleFeignClient.generateContent(apiKey, body);

        // Extract text
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return parts.get(0).get("text").toString().trim();
    }
}
