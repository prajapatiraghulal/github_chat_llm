package com.example.chatbot.controller;

import com.example.chatbot.domain.ChatMessage;
import com.example.chatbot.model.ChatRequest;
import com.example.chatbot.model.ChatResponse;
import com.example.chatbot.service.api.ChatService;
import com.example.chatbot.service.KnowledgeBaseRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final KnowledgeBaseRunner knowledgeBaseRunner;

    // --- Existing ---
    @PostMapping
    public ChatMessage chat(@RequestBody ChatRequest request) {
        return chatService.handleChat(request);
    }

    @PostMapping("/populate")
    public ResponseEntity<Void> populateEmbeddings() {
        knowledgeBaseRunner.run();
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{sessionId}/history")
    public ResponseEntity<Page<ChatMessage>> getHistory(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(sessionId, page, size));
    }


}
