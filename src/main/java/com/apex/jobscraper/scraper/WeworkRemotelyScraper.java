package com.apex.jobscraper.scraper;

import com.apex.jobscraper.config.ScraperConfig;
import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WeworkRemotelyScraper extends BaseScraper {
    private static final String RSS_URL = "https://weworkremotely.com/remote-jobs.rss";

    public WeworkRemotelyScraper(ScraperConfig config){
        super(config);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<Job> scrape(String keyword, int pages){
        log.info("[weworkremotely] Scraping with keyword='{}'", keyword);
        List<Job> jobs = new ArrayList<>();

        String body = fetchWithRetry(RSS_URL);
        if (body == null) return empty("RSS fetch returned null");
        try {
            Document doc = Jsoup.parse(body, "", Parser.xmlParser());

            Elements items = doc.select("item");
            for (Element item : items){

                String rawTitle = item.selectFirst("title") != null
                        ? item.selectFirst("title").text() : "";
                String link = item.selectFirst("link") != null
                        ? item.selectFirst("link").text() : "";
                String desc = item.selectFirst("description") != null
                        ? item.selectFirst("description").text() : "";
                String pubDate = item.selectFirst("pubDate") != null
                        ? item.selectFirst("pubDate").text() : "";

                if (rawTitle.isBlank()) continue;

                if (!keyword.isBlank() && !rawTitle.toLowerCase()
                        .contains(keyword.toLowerCase())){
                    continue;
                }
                String company, jobTitle;
                int colonIdx = rawTitle.indexOf(':');
                if (colonIdx > 0){
                    company = rawTitle.substring(0, colonIdx).strip();
                    jobTitle = rawTitle.substring(colonIdx + 1).strip();
                }else {
                    company = "unknown";
                    jobTitle = rawTitle.strip();
                }
                jobs.add(Job.builder()
                        .title(jobTitle).company(company).location("Remote").url(link).source(getName())
                        .postedDate(pubDate).description(cap(Jsoup.parse(desc).text(), 400)).build());
                }
            log.info("[weworkremotely] Found{} jobs", jobs.size());
            }catch (Exception e){
            log.error("[weworkremotely] Parse error: {}", e.getMessage());
        }
        return jobs;
    }
}
