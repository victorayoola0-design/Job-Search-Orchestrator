package com.apex.jobscraper.service;

import com.apex.jobscraper.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilterService {
    private static final Pattern SALARY_PATTERN = Pattern.compile("\\d[\\d,]{3,}");

    public List<Job> filter(List<Job> jobs, long minSalary,
                            List<String> requireTags, List<String> excludeWords) {
        if (jobs == null) return List.of();

        List<Job> result = jobs.stream()
                .filter(j -> passesSalary(j, minSalary))
                .filter(j -> passesTags(j, requireTags))
                .filter(j -> passesExcludes(j, excludeWords))
                .collect(Collectors.toList());
        log.info("Filter complete: {}/{} jobs matched criteria", result.size(), jobs.size());
        return result;
    }
    private boolean passesSalary(Job job, long minSalary){
        if (minSalary <= 0) return true;
        if (job.getSalary() == null || job.getSalary().isBlank()) return false;
        long floor = parseSalaryFloor(job.getSalary());
        return floor >= minSalary;
    }
    public long parseSalaryFloor(String salary) {
        if (salary == null || salary.isBlank() || "Not specified".equalsIgnoreCase(salary)) {
            return 0;
        }
        Matcher matcher = SALARY_PATTERN.matcher(salary.replace(",", ""));
        long minFound = Long.MAX_VALUE;
        boolean found = false;
        while (matcher.find()) {
            try {
                long val = Long.parseLong(matcher.group());
                if (val >= 1000){
                    minFound = Math.min(minFound, val);
                    found = true;
                }
            }catch (NumberFormatException ignored) {}
        }
        return found ? minFound : 0;
    }
    private boolean passesTags(Job job, List<String> requireTags){
        if (requireTags == null || requireTags.isEmpty()) return true;
        if (job.getTags() == null) return false;
        return requireTags.stream()
                .anyMatch(req -> job.getTags().stream()
                        .anyMatch(tag -> tag.equalsIgnoreCase(req)));
    }
    private boolean passesExcludes(Job job, List<String> excludeWords){
        if (excludeWords == null || excludeWords.isEmpty()) return true;

        String title = job.getTitle() != null ? (job.getTitle().toLowerCase()) : "";
        String desc = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        String combinedText = title + " " + desc;

        return excludeWords.stream()
                .filter(Objects::nonNull)
                .noneMatch(word -> combinedText.contains(word.toLowerCase()));
    }
}
