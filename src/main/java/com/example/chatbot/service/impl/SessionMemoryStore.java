package com.example.chatbot.service.impl;

import com.example.chatbot.domain.ChatMessage;
import com.example.chatbot.domain.ChatSession;
import com.example.chatbot.domain.User;
import com.example.chatbot.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

import com.example.chatbot.repository.ChatMessageRepository;
import com.example.chatbot.repository.ChatSessionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SessionMemoryStore{

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public SessionMemoryStore(ChatSessionRepository chatSessionRepository,
                              ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getHistory(ChatSession session, int page, int size) {
        return chatMessageRepository.findByChatSessionOrderByCreatedOnDesc(session, PageRequest.of(page,size));
    }

    @Transactional
    public ChatMessage saveMessage(ChatSession session, String msg, String userType, User user) {
        if(session == null){
            session = new ChatSession();
            session.setTitle(msg.length()>20? msg.substring(0,20): msg);
            session.setUser(user);
            session = chatSessionRepository.save(session);
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(userType)
                .content(msg)
                .chatSession(session)
                .createdOn(new Date())
                .build();

        return chatMessageRepository.save(chatMessage);
    }
}
