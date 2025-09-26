package com.example.chatbot.service;

import com.example.chatbot.service.api.EmbeddingService;
import com.example.chatbot.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class KnowledgeBaseCrawler {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public KnowledgeBaseCrawler(JdbcTemplate jdbcTemplate,EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

//    public void crawlAndStore(List<String> seedUrls, int maxDepth) {
//        Set<String> visited = new HashSet<>();
//        Queue<UrlDepthPair> queue = new LinkedList<>();
//
//        // Initialize queue
//        for (String url : seedUrls) {
//            queue.add(new UrlDepthPair(url, 0));
//        }
//
//        // Selenium setup
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
//        WebDriver driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
//
//        while (!queue.isEmpty()) {
//            UrlDepthPair pair = queue.poll();
//            String url = pair.url;
//            int depth = pair.depth;
//
//            if (visited.contains(url) || depth > maxDepth) continue;
//
//            try {
//                driver.get(url);
//                Thread.sleep(1000); // wait for dynamic content
//
//                // Get page source (dynamic content included)
//                String pageSource = driver.getPageSource();
//                Document doc = Jsoup.parse(pageSource);
//                visited.add(url);
//
//                // Extract main content paragraphs
//                Elements paragraphs = doc.select("p");
//                StringBuilder pageText = new StringBuilder();
//                for (Element p : paragraphs) {
//                    String text = p.text().trim();
//                    if (!text.isEmpty()) pageText.append(text).append("\n");
//                }
//
//                // Split into chunks
//                List<String> chunks = chunkText(pageText.toString(), 1000, 50); // 200 words per chunk, 50 overlap
//                for (String chunk : chunks) {
//                    if(chunk.length()<100) continue;
//                    float[] embedding = embeddingService.getEmbedding(chunk);
//                    saveDocument(url, chunk,embedding);
//                }
//
//                log.info("Stored {} chunks from {}", chunks.size(), url);
//
//                // Queue child links
//                Elements links = doc.select("a[href]");
//                for (Element link : links) {
//                    String absUrl = link.absUrl("href");
//                    if (absUrl.startsWith(seedUrls.get(0))) {
//                        queue.add(new UrlDepthPair(absUrl, depth + 1));
//                    }
//                }
//
//            } catch (Exception e) {
//                log.error("Failed to crawl {}", url, e);
//            }
//        }
//
//        driver.quit();
//    }

    private void saveChunkToDb(String sourceUrl, String chunk) {
        String sql = "INSERT INTO document_chunks (source_url, content) VALUES (?, ?)";
        jdbcTemplate.update(sql, sourceUrl, chunk);
    }

    private List<String> chunkText(String text, int wordsPerChunk, int overlapWords) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        int step = wordsPerChunk - overlapWords;
        if (step <= 0) step = wordsPerChunk / 2;

        for (int i = 0; i < words.length; i += step) {
            int end = Math.min(words.length, i + wordsPerChunk);
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < end; j++) {
                sb.append(words[j]).append(" ");
            }
            chunks.add(sb.toString().trim());
            if (end == words.length) break;
        }
        return chunks;
    }

    private static class UrlDepthPair {
        String url;
        int depth;
        UrlDepthPair(String url, int depth) { this.url = url; this.depth = depth; }
    }

    private void saveDocument(String path,String content, float[] embedding) {
        String vectorStr = CommonUtil.getString(embedding);
        String sql = "INSERT INTO documents (path, content, embedding) VALUES (?, ?, ?::vector)";
        jdbcTemplate.update(sql,path, content, vectorStr);
    }
}
