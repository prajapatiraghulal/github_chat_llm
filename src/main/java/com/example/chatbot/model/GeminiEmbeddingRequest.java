package com.example.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiEmbeddingRequest {
    private String model;
    private Content content;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        private String text;
    }
}

