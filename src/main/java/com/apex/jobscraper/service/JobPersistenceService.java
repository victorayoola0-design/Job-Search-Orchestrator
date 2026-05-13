package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPersistenceService {
    private final JobRepository jobRepository;

    @Transactional
    public void saveAll(List<Job> jobs){
        for (Job job : jobs){
            jobRepository.findByUrl(job.getUrl()).ifPresentOrElse(existing ->{
                existing.setScrapedAt(Instant.now());
                jobRepository.save(existing);
            }, () -> jobRepository.save(job));
        }
        log.info("Persistence: synced {} jobs to database", jobs.size());
    }
}
