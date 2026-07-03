package com.example.dsafinals.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class JournalEntry {
    private String id;
    private String title;
    private String content;
    private String tags;
    private LocalDate date;
    private LocalDateTime createdAt;

    public JournalEntry() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public JournalEntry(String title, String content, String tags, LocalDate date) {
        this();
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
