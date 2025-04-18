package com.moodcalendar.Managers;

import com.moodcalendar.Models.MoodEntry;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MoodManager {
    private List<MoodEntry> moodEntries;
    private static final String MOODS_FILE = "moods.dat"; // File to save moods

    public MoodManager() {
        this.moodEntries = new ArrayList<>();
        loadMoods(); // Load moods when the manager is initialized
    }

    // Add a mood entry for a specific date
    public void addMoodEntry(Date date, String mood) {
        // Normalize the date to remove the time portion
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date normalizedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Add the mood entry
        MoodEntry moodEntry = new MoodEntry(normalizedDate, mood);
        moodEntries.add(moodEntry);
    }

    // Retrieve moods for a specific date
    public List<MoodEntry> getMoodsForDate(Date date) {
        // Normalize the date to remove the time portion
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date normalizedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Filter moods for the normalized date
        return moodEntries.stream()
                .filter(entry -> entry.getDate().equals(normalizedDate))
                .collect(Collectors.toList());
    }

    // Save moods to a file
    public void saveMoods() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MOODS_FILE))) {
            oos.writeObject(moodEntries);
            System.out.println("Moods saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving moods: " + e.getMessage());
        }
    }

    // Load moods from a file
    @SuppressWarnings("unchecked")
    public void loadMoods() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MOODS_FILE))) {
            moodEntries = (List<MoodEntry>) ois.readObject();
            System.out.println("Moods loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved moods found. Starting fresh.");
            moodEntries = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading moods: " + e.getMessage());
            moodEntries = new ArrayList<>();
        }
    }
}