package com.moodcalendar.Models;

import java.io.Serializable;
import java.util.Date;

public class MoodEntry implements Serializable {
    private String mood;
    private Date date;

    // Updated Constructor: MoodEntry(Date date, String mood)
    public MoodEntry(Date date, String mood) {
        this.date = date;
        this.mood = mood;
    }

    // Getter and setter methods
    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Mood: " + mood + " on " + date.toString();
    }
}
