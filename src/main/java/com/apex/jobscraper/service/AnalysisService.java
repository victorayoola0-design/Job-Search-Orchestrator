package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

public class AnalysisService {
    private final ChatClient chatClient;
    public String summarizeDescription(Job job){
        return chatClient.prompt().user("Summarize the following job description in 3 bullet points, " +
                "focusing on the tech stack: " + job.getDescription()).call().content();
    }
}
