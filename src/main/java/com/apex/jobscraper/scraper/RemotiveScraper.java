package com.apex.jobscraper.scraper;

import com.apex.jobscraper.config.ScraperConfig;
import com.apex.jobscraper.model.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RemotiveScraper extends BaseScraper {
    private static final String API_URL = "https://remotive.com/api/remote-jobs";
    private final ObjectMapper objectMapper;

    public RemotiveScraper(ScraperConfig config, ObjectMapper objectMapper){
        super(config);
        this.objectMapper = objectMapper;
    }
    @Override
    public String getName(){
        return "remotive";
    }
    @Override
    public List<Job> scrape (String keyword, int pages){
        log.info("[remotive] Scraping with keyword='{}'", keyword);
        List<Job> jobs = new ArrayList<>();
        try {
            String body = keyword.isBlank()
                    ? fetchWithRetry(API_URL)
                    : fetchText(API_URL, "search", keyword);

            if (body == null) return empty("HTTP fetch returned null");
            JsonNode root = objectMapper.readTree(body);
            JsonNode items = root.path("jobs");
            if (!items.isArray()) return empty("No jobs array in response");
            for (JsonNode item : items) {
                jobs.add(Job.builder()
                        .title(item.path("title").asText(""))
                        .company(item.path("company_name").asText("Unknown"))
                        .location(item.path("candidate_required_location").asText("Remote"))
                        .salary(nonBlank(item.path("salary").asText(""), "Not specified"))
                        .tags(parseTags(item)).url(item.path("url").asText("")).source(getName())
                        .postedDate(item.path("publication_date").asText(""))
                        .description(cap(stripHtml(item.path("description").asText("")), 400)).build());
            }
            log.info("[remotive] Found {} jobs", jobs.size());
        }catch (Exception e){
            log.error("[remotive] Error: {}", e.getMessage());
        }
        return jobs;
    }
    private List<String> parseTags(JsonNode item){
        List<String> tags = new ArrayList<>();
        JsonNode tagsNode = item.path("tags");
        if (tagsNode.isArray()){
            tagsNode.forEach(t -> tags.add(t.asText()));
        }
        return tags;
    }
    private String stripHtml(String html){
        if (html == null) return "";
        return html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ")
                .strip();
    }
    private String nonBlank(String value, String fallback){
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
