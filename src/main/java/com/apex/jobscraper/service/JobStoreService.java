package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class JobStoreService {
    private final Map<String, Job> store = new ConcurrentHashMap<>();

    public int addAll(List<Job> jobs){
        int before = store.size();
        for (Job job : jobs){
            store.putIfAbsent(job.dedupeKey(), job);
        }
        int added = store.size() - before;
        log.info("Store: +{} new jobs | total={}", added, store.size());
        return added;
    }
    public List<Job> getAll(){
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }
    public void clear(){
        store.clear();
        log.info("Job store cleared");
    }
    public int size(){
        return store.size();
    }
}
