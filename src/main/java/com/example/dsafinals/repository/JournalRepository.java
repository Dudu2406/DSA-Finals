package com.example.dsafinals.repository;

import com.example.dsafinals.model.JournalEntry;
import com.example.dsafinals.util.SettingsManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class JournalRepository {
    private static final String ENTRIES_FILE = "journal_entries.json";
    private final Gson gson;
    private List<JournalEntry> entries;
    private boolean loaded = false;

    public JournalRepository() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();
        this.entries = new ArrayList<>();
    }

    private File getEntriesFile() {
        String dir = SettingsManager.loadDirectoryPath();
        if (dir == null || dir.isEmpty()) {
            dir = System.getProperty("user.home") + File.separator + ".dsa-journal";
        }
        File journalDir = new File(dir);
        if (!journalDir.exists()) {
            journalDir.mkdirs();
        }
        return new File(journalDir, ENTRIES_FILE);
    }

    private void load() {
        if (loaded) return;
        File file = getEntriesFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<JournalEntry>>() {}.getType();
                List<JournalEntry> loaded = gson.fromJson(reader, listType);
                if (loaded != null) {
                    entries = loaded;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loaded = true;
    }

    private void save() {
        File file = getEntriesFile();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<JournalEntry> getAll() {
        load();
        return new ArrayList<>(entries);
    }

    public List<JournalEntry> getByDate(LocalDate date) {
        load();
        return entries.stream()
            .filter(e -> e.getDate() != null && e.getDate().equals(date))
            .collect(Collectors.toList());
    }

    public Optional<JournalEntry> getById(String id) {
        load();
        return entries.stream()
            .filter(e -> e.getId().equals(id))
            .findFirst();
    }

    public void saveEntry(JournalEntry entry) {
        load();
        Optional<JournalEntry> existing = entries.stream()
            .filter(e -> e.getId().equals(entry.getId()))
            .findFirst();
        if (existing.isPresent()) {
            int index = entries.indexOf(existing.get());
            entries.set(index, entry);
        } else {
            entries.add(entry);
        }
        save();
    }

    public void deleteEntry(String id) {
        load();
        entries.removeIf(e -> e.getId().equals(id));
        save();
    }

    public List<JournalEntry> search(String query) {
        load();
        if (query == null || query.isEmpty()) return getAll();
        String q = query.toLowerCase();
        return entries.stream()
            .filter(e -> (e.getTitle() != null && e.getTitle().toLowerCase().contains(q))
                || (e.getContent() != null && e.getContent().toLowerCase().contains(q))
                || (e.getTags() != null && e.getTags().toLowerCase().contains(q)))
            .collect(Collectors.toList());
    }
}
