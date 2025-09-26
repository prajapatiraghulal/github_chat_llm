package com.example.chatbot.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatResponse {
    private String message;
    private Date date;
    private String sender;
}