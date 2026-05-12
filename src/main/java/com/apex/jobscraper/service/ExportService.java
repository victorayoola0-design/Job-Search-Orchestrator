package com.apex.jobscraper.service;

import com.apex.jobscraper.config.ScraperConfig;
import com.apex.jobscraper.model.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.of("UTC"));
    private static final String[] CSV_HEADERS = {"title", "company", "location", "salary", "tags", "url", "source", "posted_date", "description", "scraped_at"};

    private final ScraperConfig config;
    private final ObjectMapper objectMapper;

    public void saveSnapshot(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            log.warn("Export aborted: Job list is null or empty");
            return;
        }
        String timestamp = TS_FMT.format(Instant.now());
        try {
            Path outputDir = ensureOutputDir();
            exportToCsv(jobs, outputDir.resolve("jobs_" + timestamp + ".csv"));
            exportToJson(jobs, outputDir.resolve("jobs_" + timestamp + ".json"));
        } catch (IOException e) {
            log.error("Critical I/O failure during export orchestration: {}", e.getMessage(), e);
        }
    }

    private void exportToCsv(List<Job> jobs, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(CSV_HEADERS);

            for (Job job : jobs) {
                csvWriter.writeNext(mapToCsvRow(job));
            }
            log.info("CSV snapshot successfully persisted: {}", path.getFileName());
        }
    }

    private void exportToJson(List<Job> jobs, Path path) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), jobs);
        log.info("JSON snapshot successfully persisted: {}", path.getFileName());
    }

    private Path ensureOutputDir() throws IOException {
        Path dir = Path.of(config.getOutputDir());
        Files.createDirectories(dir);
        return dir;
    }

    private String[] mapToCsvRow(Job job) {
        return new String[]{
                Objects.toString(job.getTitle(), ""),
                Objects.toString(job.getCompany(), ""),
                Objects.toString(job.getLocation(), ""),
                Objects.toString(job.getSalary(), ""),
                Objects.toString(job.getTagsFlat(), ""),
                Objects.toString(job.getUrl(), ""),
                Objects.toString(job.getSource(), ""),
                Objects.toString(job.getPostedDate(), ""),
                Objects.toString(job.getDescription(), ""),
                job.getScrapedAt() != null ? job.getScrapedAt().toString() : ""};
    }
}
