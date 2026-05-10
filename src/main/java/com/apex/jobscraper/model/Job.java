package com.apex.jobscraper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private String title;
    private String company;

    @Builder.Default
    private String location = "Remote";

    @Builder.Default
    private String salary = "not specified";

    private List<String> tags;
    private String url;
    private String source;
    private String postedDate;
    private String description;

    @Builder.Default
    private Instant scrapedAt = Instant.now();

    public String getTagsFlat(){
        if (tags == null) return "";
        return String.join(", ", tags);
    }
    public String dedupeKey(){
        return (title == null ? "" : title.toLowerCase().strip()) +
                "|" + (company == null ? "" : company.toLowerCase().strip());
    }

}
