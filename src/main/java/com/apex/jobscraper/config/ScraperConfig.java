package com.apex.jobscraper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "apex.scraper")
public class ScraperConfig {

    private List<String> keywords = List.of("Software engineer", "AI engineer", "java developer", "python developer",
            "Cloud engineer", "Solutions architect", "project planner");

    private long delayMinMs = 2000;
    private long delayMaxMs = 5000;
    private int maxRetries = 3;
    private String outputDir = "output";
    private String scheduleCron = "0 0 */6 * * *";

    private Map<String, Boolean> boards = Map.of("remoteok", true, "weworkremotely", true,
    "remotive", true);

    public static final List<String> USER_AGENTS = List.of(
            "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/122",
            "\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/122",
            "\"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/122");
}
