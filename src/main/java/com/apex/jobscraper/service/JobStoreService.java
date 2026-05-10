package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class JobStoreService {
    private final Map<String, Job> store = new ConcurrentHashMap<>()
}
