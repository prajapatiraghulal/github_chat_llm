package com.example.chatbot.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KnowledgeBaseRunner {

    private final KnowledgeBasePopulator populator;
    private final KnowledgeBaseCrawler baseCrawler;

    public KnowledgeBaseRunner(KnowledgeBasePopulator populator,KnowledgeBaseCrawler crawler) {
        this.populator = populator;
        this.baseCrawler = crawler;
    }

    public void run() {
        List<String> handbookUrls = List.of(
                "https://handbook.gitlab.com/",
                "https://about.gitlab.com/direction/"

        );

        baseCrawler.crawlAndStore(handbookUrls,1);
    }
}
