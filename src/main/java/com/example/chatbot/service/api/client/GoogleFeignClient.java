package com.example.chatbot.service.api.client;

import com.example.chatbot.model.GeminiEmbeddingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "googleClient", url = "${google.api.baseUrl}")
public interface GoogleFeignClient {
    @PostMapping("${google.api.embedding}")
    Map<String, Object> getEmbedding(@RequestBody GeminiEmbeddingRequest request,
                                     @RequestHeader("x-goog-api-key") String apiKey);


    @PostMapping(value = "${google.api.chat}", consumes = "application/json")
    Map<String, Object> generateContent(
            @RequestHeader("X-goog-api-key") String apiKey,
            @RequestBody Map<String, Object> body
    );
}
