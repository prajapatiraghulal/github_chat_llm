package com.example.chatbot.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtil {
    /**
     *  Split text into chunks of chunkSize and overlap (overlap words)
     */
    public static List<String> extractAndChunk(Document doc, int chunkSize, int overlap) {
        Elements paragraphs = doc.select("p");
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int wordCount = 0;

        for (var p : paragraphs) {
            String[] words = p.text().split("\\s+");
            for (String word : words) {
                currentChunk.append(word).append(" ");
                wordCount++;
                if (wordCount >= chunkSize) {
                    chunks.add(currentChunk.toString().trim());
                    // reset builder but keep overlap
                    String[] overlapWords = currentChunk.toString().split("\\s+");
                    currentChunk = new StringBuilder();
                    int start = Math.max(0, overlapWords.length - overlap);
                    for (int i = start; i < overlapWords.length; i++) {
                        currentChunk.append(overlapWords[i]).append(" ");
                    }
                    wordCount = overlap;
                }
            }
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }


    /**
     *  Convert float[] to Postgres vector format: [0.1,0.2,...]
     */
    public static String getString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Crawl all internal links starting from a seed URL
     */
    public static Set<String> crawlAllLinks(String seedUrl) throws Exception {
        Set<String> visited = new HashSet<>();
        crawl(seedUrl, visited, seedUrl);
        return visited;
    }

    private static void crawl(String url, Set<String> visited, String domainPrefix) throws Exception {
        if (visited.contains(url)|| visited.size() > 200) return;

        visited.add(url);

        Document doc = Jsoup.connect(url).get();

        // extract all <a> links
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absUrl = link.absUrl("href");
            System.out.println("Link: "+ domainPrefix + absUrl);
            // only follow internal links under the same domain/prefix
            if (absUrl.startsWith(domainPrefix) && !visited.contains(absUrl)) {
                crawl(absUrl, visited, domainPrefix);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Crawling all links");
        Set<String> allUrls = crawlAllLinks("https://handbook.gitlab.com/handbook/");
        System.out.println("Found " + allUrls.size() + " pages:");
        allUrls.forEach(System.out::println);
    }

}
