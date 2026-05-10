package com.apex.jobscraper.scraper;

import com.apex.jobscraper.config.ScraperConfig;
import com.apex.jobscraper.model.Job;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RemoteOkScraper extends BaseScraper{
    private static final String API_URL = "https://remoteok.com/api";
    private final ObjectMapper objectMapper;

    public RemoteOkScraper( ScraperConfig config, ObjectMapper objectMapper){
        super(config);
        this.objectMapper = objectMapper;
    }
    @Override
    public String getName(){
        return "remoteok";
    }
    @Override
    public List<Job> scrape(String keyword, int pages) {
        log.info("[remoteok] Scraping with with keyword='{}'", keyword);
        List<Job> jobs = new ArrayList<>();

        String body = fetchWithRetry(API_URL);
        if (body == null) return empty("HTTP fetch returned null");

        try {
            JsonNode root = objectMapper.readTree(body);

            if (!root.isArray()) return empty("Unexpected JSON structure");

            for (JsonNode item : root) {
                if (!item.has("position")) continue;

                String title = item.path("position").asText("");
                String tags = tagsAsString(item);

                if (!keyword.isBlank()
                        && !title.toLowerCase().contains(keyword.toLowerCase())
                        && !tags.toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }
                String url = item.path("url").asText("");
                if (!url.startsWith("http")) {
                    url = "https://remoteok.com" + url;
                }
                jobs.add(Job.builder()
                        .title(title).company(item.path("company").asText("Unknown"))
                        .location("Remote")
                        .salary(nonBlank(item.path("salary").asText(""), "Not specified"))
                        .tags(parseTags(item))
                        .url(url)
                        .source(getName())
                        .postedDate(item.path("date").asText(""))
                        .description(cap(item.path("description").asText(""), 400))
                        .build());
            }
            log.info("[remoteok] Found {} jobs", jobs.size());
        } catch (Exception e) {
            log.error("[remoteok] Parse error: {}", e.getMessage());
        }
        return jobs;
    }
    private List<String> parseTags(JsonNode item){
        List<String> tags = new ArrayList<>();
        JsonNode tagsNode = item.path("tags");
        if (tagsNode.isArray()) {
            tagsNode.forEach(t -> tags.add(t.asText()));
        }
        return tags;
    }
    private String tagsAsString(JsonNode item){
        return String.join(" ", parseTags(item));
    }
    private String nonBlank(String value, String fallback){
        return (value == null || value.isBlank()) ? fallback : value;
    }
}

