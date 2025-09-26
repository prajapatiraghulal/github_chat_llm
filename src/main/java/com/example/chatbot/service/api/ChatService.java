package com.example.chatbot.service.api;

import com.example.chatbot.domain.ChatMessage;
import com.example.chatbot.model.ChatRequest;
import com.example.chatbot.model.ChatResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
    ChatMessage handleChat(ChatRequest request);
    Page<ChatMessage> getChatHistory(Long sessionId, Integer page, Integer size);

}
