package com.apex.jobscraper.model;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockJobProvider implements JobProvider{


    @Override
    public List<Job> fetchJobs(String query, String location) {
        return List.of(new Job("Software Engineer", "Google", "Dubai"));
    }

    @Override
    public String getProviderName() {
        return "MockProvider";
    }
}
