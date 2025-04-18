package com.moodcalendar;

import com.moodcalendar.GUI.CalendarGUI;
import com.moodcalendar.Managers.MoodManager;
import com.moodcalendar.Managers.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        MoodManager moodManager = new MoodManager();

        // Launch the calendar GUI
        CalendarGUI gui = new CalendarGUI(taskManager, moodManager);

        // Refresh display to show loaded data
        gui.updateDisplay();

        gui.show();
    }
}