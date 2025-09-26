package com.example.chatbot.service.impl;

import com.example.chatbot.config.EmbeddingConfig;
import com.example.chatbot.service.api.EmbeddingProvider;
import com.example.chatbot.service.api.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmbeddingServiceImpl implements EmbeddingService{

    private final Map<String, EmbeddingProvider> providers;
    private final EmbeddingConfig config;

    @Autowired
    public EmbeddingServiceImpl(List<EmbeddingProvider> providerList, EmbeddingConfig config) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(EmbeddingProvider::getName, Function.identity()));
        this.config = config;
    }

    public float[] getEmbedding(String text) {
        String providerName = config.getProvider().toLowerCase();
        EmbeddingProvider provider = providers.get(providerName);
        if (provider == null) throw new RuntimeException("Unknown provider: " + providerName);
        return provider.getEmbedding(text);
    }
}

