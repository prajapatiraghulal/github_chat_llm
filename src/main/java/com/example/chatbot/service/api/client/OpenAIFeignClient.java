package com.example.chatbot.service.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "embeddingClient", url = "${openai.api.baseUrl}")
public interface OpenAIFeignClient {

    @PostMapping("${openai.api.embedding}")
    Map<String, Object> getEmbedding(@RequestBody Map<String, Object> request,
                                     @RequestHeader("Authorization") String authHeader);
}