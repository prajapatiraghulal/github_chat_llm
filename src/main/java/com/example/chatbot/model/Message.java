package com.example.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String role;   // "user" or "assistant" or "system"
    private String content;
}