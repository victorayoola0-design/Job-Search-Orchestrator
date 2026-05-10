package com.apex.jobscraper.scraper;

import com.apex.jobscraper.config.ScraperConfig;
import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
public abstract class BaseScraper {

    protected final ScraperConfig config;
    private final Random rng = new Random();

    protected BaseScraper(ScraperConfig config){
        this.config = config;
    }
    public abstract String getName();
    public abstract List<Job> scrape(String keyword, int pages);

    protected Document fetchDocument(String url) throws IOException {
        politeDelay();
        return Jsoup.connect(url)
                .userAgent(randomAgent())
                .header("Accept language", "en-US, en;q=0.9").timeout(15000)
                .get();
    }
    protected String fetchText(String url) throws IOException{
        politeDelay();
        return Jsoup.connect(url)
                .userAgent(randomAgent())
                .header("Accept", "application/json")
                .ignoreContentType(true).timeout(15000)
                .execute().body();
    }
    protected String fetchText(String url, String...queryPairs) throws IOException{
        politeDelay();
        Connection conn = Jsoup.connect(url)
                .userAgent(randomAgent()).header("Accept", "application/json")
                .ignoreContentType(true).timeout(15000);

        for (int i = 0; i + 1 < queryPairs.length; i += 2){
            conn.data(queryPairs[i], queryPairs[i + 1]);
        }
        return conn.execute().body();
    }
    protected String fetchWithRetry(String url){
        int attempts = 0;
        while(attempts < config.getMaxRetries()){
            try {
                return fetchText(url);
            } catch (IOException e) {
                attempts++;
                long backoff = (long) Math.pow(2, attempts) * 1000L;
                log.warn("[{}] Attempt {}/{} failed: {}. Retrying in {}ms", getName(),
                        attempts, config.getMaxRetries(), e.getMessage(),backoff);
                try {
                    Thread.sleep(backoff);
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        log.error("[{}] All retries exhausted for {}", getName(), url);
        return null;
    }
    private void politeDelay(){
        long delay = config.getDelayMinMs() + (long)(rng.nextDouble()
        * (config.getDelayMaxMs() - config.getDelayMinMs()));
        try {
            Thread.sleep(delay);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    private String randomAgent(){
        List<String> agents = ScraperConfig.USER_AGENTS;
        return agents.get(rng.nextInt(agents.size()));
    }
    protected String cap(String s, int max){
        if (s == null) return "";
        return s.length() > max ? s.substring(0,max) : s;
    }
    protected List<Job> empty(String reason){
        log.warn("[{}] Returning empty: {}", getName(), reason);
        return Collections.emptyList();
    }
}
