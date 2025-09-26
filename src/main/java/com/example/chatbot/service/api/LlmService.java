package com.example.chatbot.service.api;

import org.springframework.stereotype.Service;

@Service
public interface LlmService {
    String askLLM(String prompt);
}
