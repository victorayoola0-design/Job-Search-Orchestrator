package com.apex.jobscraper.model;

import java.util.List;

public interface JobProvider {
    List<Job> fetchJobs(String query, String location);
        String getProviderName();
}
