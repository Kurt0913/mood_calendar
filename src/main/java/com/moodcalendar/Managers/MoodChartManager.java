package com.moodcalendar.Managers;

import com.moodcalendar.Models.MoodEntry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MoodChartManager {
    private MoodManager moodManager;

    public MoodChartManager(MoodManager moodManager) {
        this.moodManager = moodManager;
    }

    public void showMonthlyMoodChart(Date selectedDate) {
        Map<String, Integer> moodCountMap = getMoodCountForMonth(selectedDate);

        // Parameterize DefaultPieDataset with String for mood and Integer for count
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : moodCountMap.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Mood Distribution for the Month",
                dataset,
                true, true, false);

        // Style the pie chart
        PiePlot<String> plot = createPiePlot(chart);

        plot.setSectionPaint("Happy", new Color(144, 238, 144));
        plot.setSectionPaint("Sad", new Color(173, 216, 230));
        plot.setSectionPaint("Angry", new Color(255, 99, 71));
        plot.setSectionPaint("Anxious", new Color(255, 204, 102));

        // Create chart panel and frame
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Mood Chart");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showMoodMessage(moodCountMap);
    }

    // This method suppresses the raw type warning when casting PiePlot
    @SuppressWarnings("unchecked")
    private PiePlot<String> createPiePlot(JFreeChart chart) {
        return (PiePlot<String>) chart.getPlot(); // Parameterized PiePlot with <String>
    }

    private Map<String, Integer> getMoodCountForMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int selectedMonth = cal.get(Calendar.MONTH);
        int selectedYear = cal.get(Calendar.YEAR);

        Map<String, Integer> moodCount = new HashMap<>();
        if (moodManager == null || moodManager.getAllMoods() == null) {
            return moodCount;  // Return empty map if no moods available
        }

        for (MoodEntry entry : moodManager.getAllMoods()) {
            cal.setTime(entry.getDate());
            int entryMonth = cal.get(Calendar.MONTH);
            int entryYear = cal.get(Calendar.YEAR);

            if (entryMonth == selectedMonth && entryYear == selectedYear) {
                String mood = entry.getMood();
                moodCount.put(mood, moodCount.getOrDefault(mood, 0) + 1);
            }
        }
        return moodCount;
    }

    private void showMoodMessage(Map<String, Integer> moodCountMap) {
        if (moodCountMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No mood data available for this month.", "Monthly Mood Reflection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String dominantMood = Collections.max(moodCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();

        String message;
        switch (dominantMood.toLowerCase()) {
            case "sad":
            case "angry":
            case "anxious":
                message = "It seems this month has been tough. Take time to rest and take care of yourself 💙";
                break;
            default:
                message = "Great job maintaining a positive mindset! Keep it up 🌟";
                break;
        }

        JOptionPane.showMessageDialog(null, message, "Monthly Mood Reflection", JOptionPane.INFORMATION_MESSAGE);
    }
}
