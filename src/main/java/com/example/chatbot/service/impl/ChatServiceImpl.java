package com.example.chatbot.service.impl;
import com.example.chatbot.domain.ChatMessage;
import com.example.chatbot.domain.ChatSession;
import com.example.chatbot.domain.User;
import com.example.chatbot.model.ChatRequest;
import com.example.chatbot.model.ChatResponse;
import com.example.chatbot.repository.ChatSessionRepository;
import com.example.chatbot.repository.UserRepository;
import com.example.chatbot.service.api.ChatService;
import com.example.chatbot.service.api.EmbeddingService;
import com.example.chatbot.service.api.LlmService;
import com.example.chatbot.service.api.VectorDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final EmbeddingService embeddingService;
    private final VectorDbService vectorDbService;
    private final LlmService llmService;
    private final SessionMemoryStore sessionMemoryStore; // custom memory store
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
    @Override
    public ChatMessage handleChat(ChatRequest request) {
        ChatSession session = chatSessionRepository.findById(request.getSessionId()).orElse(null);
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        // 1. Get chat history
        List<ChatMessage> history = sessionMemoryStore.getHistory(session,0,10).getContent();

        sessionMemoryStore.saveMessage(session, request.getUserMessage(),"user", user);


        // 2. Embed user query
        float[] queryEmbedding = embeddingService.getEmbedding(request.getUserMessage());

        // 3. Retrieve context from vector DB
        List<String> docs = vectorDbService.searchRelevantDocs(queryEmbedding);

        // 4. Build prompt
        String prompt = buildPrompt(history, docs, request.getUserMessage());

        // 5. Call LLM
        String answer = llmService.askLLM(prompt);

        // 6. Save conversation

        return sessionMemoryStore.saveMessage(session, answer,"assistant",user);
    }

    @Override
    public Page<ChatMessage> getChatHistory(Long sessionId, Integer page, Integer size) {
        ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow();
        return sessionMemoryStore.getHistory(session,0,20);
    }

    private String buildPrompt(List<ChatMessage> history, List<String> docs, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("System: You are an assistant that allows users — the employees or aspiring employees to easily access information from GitLab’s Handbook and Direction pages");
        sb.append("You must only answer based on the provided handbook context or user gitlab's handbook or directions documentation scraping");
        sb.append("If the answer is not explicitly found in the context, respond with ");
        sb.append("\"I'm sorry, I couldn't find relevant information in the handbook context.\" ");
        sb.append("Do not make assumptions or provide information outside the given context.\n\n");
        sb.append("Respond like an assistant with greetings if required and user previous conversations to determine users request context.\n\n");

        sb.append("Context from Handbook:\n");
        docs.forEach(doc -> sb.append("- ").append(doc).append("\n"));

        sb.append("\nConversation so far:\n");
        history.forEach(msg ->
                sb.append(msg.getSender()).append(": ").append(msg.getContent()).append(", timestamp: ").append(msg.getCreatedOn()).append("\n")
        );

        sb.append("User: ").append(userMessage).append("\nAssistant:");
        return sb.toString();
    }


}