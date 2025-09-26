package com.example.chatbot.service;

import com.example.chatbot.service.api.EmbeddingService;
import com.example.chatbot.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class KnowledgeBasePopulator {

    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;

    public KnowledgeBasePopulator(EmbeddingService embeddingService, JdbcTemplate jdbcTemplate) {
        this.embeddingService = embeddingService;
        this.jdbcTemplate = jdbcTemplate;
    }

//    public void populateKnowledgeBase(List<String> urls) {
//        for (String url : urls) {
//            try {
//                Document doc = Jsoup.connect(url).get();
//                List<String> chunks = CommonUtil.extractAndChunk(doc, 300, 50); // 300 words, 50 overlap
//
//                for (String chunk : chunks) {
////                    float[] embedding = embeddingService.getEmbedding(chunk);
////                    saveDocument(chunk, embedding);
//                    System.out.println(chunk);
//                }
//
//                log.info("Inserted {} chunks from {}", chunks.size(), url);
//
//            } catch (Exception e) {
//                log.error("Failed to process {}", url, e);
//            }
//        }
//    }


    public void populateKnowledgeBase(List<String> seedUrls) throws Exception {
        Set<String> allUrls = new HashSet<>();
        for (String seed : seedUrls) {
            allUrls.addAll(CommonUtil.crawlAllLinks(seed));
        }

        for (String url : allUrls) {
            try {
                Document doc = Jsoup.connect(url).get();
                List<String> chunks = CommonUtil.extractAndChunk(doc, 300, 50); // 300 words, 50 overlap
                for (String chunk : chunks) {
//                    float[] embedding = embeddingService.getEmbedding(chunk);
//                    saveChunk(url, chunk, embedding);
                    System.out.println(chunk);
                }
                log.info("Inserted {} chunks from {}", chunks.size(), url);
            } catch (Exception e) {
                log.error("Failed for URL {}", url, e);
            }
        }
    }








}
