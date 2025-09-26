package com.example.chatbot.repository;

import com.example.chatbot.domain.ChatMessage;
import com.example.chatbot.domain.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatSessionOrderByCreatedOnDesc(ChatSession chatSession, Pageable pageable);
}
