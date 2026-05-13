package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class NotificationService {
    private static final String SLACK_WEBHOOK = "https://hooks.slack.com/services/T00/B00/XXX";
    public void notifyHighPriorityJob(Job job){
        log.info("Sending alert for: {}", job.getTitle());

        Map<String, String> payLoad = Map.of("text", "New High-Priority Job Found: " + job.getTitle() + "at " + job.getCompany());
        try {
            log.info("Notification sent successfully.");
        }catch (Exception e){
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}
