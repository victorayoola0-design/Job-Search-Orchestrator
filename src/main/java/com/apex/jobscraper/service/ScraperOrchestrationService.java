package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import com.apex.jobscraper.model.JobProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperOrchestrationService {
    private final List<JobProvider> providers;
    private final FilterService filterService;
    private final JobPersistenceService persistenceService;
    private final NotificationService notificationService;

    public void runDiscovery(String role, String location){
        for (JobProvider provider : providers){
            List<Job> rawJobs = provider.fetchJobs(role, location);
            List<Job> filteredJobs = filterService.filter(rawJobs, 10000, List.of("Java"),
                    List.of("Senior"));
            persistenceService.saveAll(filteredJobs);
            filteredJobs.stream().findFirst().ifPresent(notificationService::notifyHighPriorityJob);
        }
    }
}
