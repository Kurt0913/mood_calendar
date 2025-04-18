package com.moodcalendar.GUI;

import com.moodcalendar.Managers.MoodManager;
import com.moodcalendar.Managers.TaskManager;
import com.moodcalendar.Models.MoodEntry;
import com.moodcalendar.Models.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import com.toedter.calendar.JCalendar;

public class CalendarGUI {
    private JFrame frame;
    private JCalendar calendar;
    private JTextPane taskTextPane;
    private JTextPane moodTextPane;
    private TaskManager taskManager;
    private MoodManager moodManager;

    public CalendarGUI(TaskManager taskManager, MoodManager moodManager) {
        this.taskManager = taskManager;
        this.moodManager = moodManager;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Mood & Task Calendar");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
    
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(mainPanel);
    
        // Calendar on the left
        calendar = new JCalendar();
        calendar.setPreferredSize(new Dimension(300, 300));
        mainPanel.add(calendar, BorderLayout.WEST);
    
        // Info panel for tasks and moods
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.add(infoPanel, BorderLayout.CENTER);
    
        // Task section
        JPanel taskPanel = new JPanel(new BorderLayout());
        JLabel taskLabel = new JLabel("Tasks");
        taskLabel.setFont(new Font("Arial", Font.BOLD, 16));
        taskPanel.add(taskLabel, BorderLayout.NORTH);
        taskTextPane = new JTextPane();
        taskTextPane.setEditable(false);
        taskTextPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taskPanel.add(new JScrollPane(taskTextPane), BorderLayout.CENTER);
        infoPanel.add(taskPanel);
    
        // Mood section
        JPanel moodPanel = new JPanel(new BorderLayout());
        JLabel moodLabel = new JLabel("Moods");
        moodLabel.setFont(new Font("Arial", Font.BOLD, 16));
        moodPanel.add(moodLabel, BorderLayout.NORTH);
        moodTextPane = new JTextPane();
        moodTextPane.setEditable(false);
        moodTextPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moodPanel.add(new JScrollPane(moodTextPane), BorderLayout.CENTER);
        infoPanel.add(moodPanel);
    
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        JButton addTaskButton = new JButton("Add Task");
        JButton addMoodButton = new JButton("Add Mood");
        JButton manageTaskButton = new JButton("Manage Tasks");
    
        addTaskButton.addActionListener((ActionEvent e) -> addTaskEntry());
        addMoodButton.addActionListener((ActionEvent e) -> addMoodEntry());
        manageTaskButton.addActionListener((ActionEvent e) -> manageTasks());
    
        buttonPanel.add(addTaskButton);
        buttonPanel.add(addMoodButton);
        buttonPanel.add(manageTaskButton);
    
        // Update display when a date is selected
        calendar.getDayChooser().addPropertyChangeListener("day", e -> updateDisplay());
    
        // Removed the initial call to updateDisplay()
    }

    private void addTaskEntry() {
        String taskName = JOptionPane.showInputDialog(frame, "Enter task name:");
        if (taskName == null || taskName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Task name cannot be empty.");
            return;
        }
    
        Date selectedDate = calendar.getDate();
        LocalDate localSelectedDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
        String timeString = JOptionPane.showInputDialog(frame, "Enter task time (hh:mm AM/PM):");
        if (timeString == null || timeString.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Task time cannot be empty.");
            return;
        }
    
        try {
            // Normalize and parse the time string
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime taskTime = LocalTime.parse(timeString.trim().toUpperCase(), timeFormatter);
    
            // Combine the date and time
            LocalDateTime taskDateTime = LocalDateTime.of(localSelectedDate, taskTime);
    
            String status = JOptionPane.showInputDialog(frame, "Enter task status:");
            if (status == null || status.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Task status cannot be empty.");
                return;
            }
    
            String[] priorities = {"High", "Medium", "Low"};
            String priority = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select task priority:",
                    "Task Priority",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    priorities,
                    priorities[1]
            );
            if (priority == null) {
                JOptionPane.showMessageDialog(frame, "Task priority must be selected.");
                return;
            }
    
            // Add the task to the task manager
            taskManager.addTask(taskName, taskDateTime, status, priority);
            taskManager.saveTasks();
            updateDisplay();
    
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(frame, "Invalid time format. Please use hh:mm AM/PM.");
        }
    }

private void addMoodEntry() {
    // Explicitly retrieve and normalize the selected date
    Date selectedDate = calendar.getDate();
    LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    // Debugging: Print the normalized selected date
    System.out.println("Normalized Selected Date: " + selectedDate);

    List<MoodEntry> moodsForDate = moodManager.getMoodsForDate(selectedDate);

    if (!moodsForDate.isEmpty()) {
        StringBuilder moodList = new StringBuilder();
        for (int i = 0; i < moodsForDate.size(); i++) {
            moodList.append(i + 1).append(". ").append(moodsForDate.get(i).getMood()).append("\n");
        }

        String[] options = {"Add New Mood", "Edit Existing Mood"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Moods for the selected date:\n" + moodList + "\nWhat would you like to do?",
                "Manage Moods",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Add a new mood
            addNewMood(selectedDate);
        } else if (choice == 1) {
            // Edit an existing mood
            String moodChoice = JOptionPane.showInputDialog(frame, "Select a mood to edit (1-" + moodsForDate.size() + "):\n" + moodList);
            if (moodChoice != null && !moodChoice.trim().isEmpty()) {
                try {
                    int index = Integer.parseInt(moodChoice.trim()) - 1;
                    if (index >= 0 && index < moodsForDate.size()) {
                        MoodEntry moodEntry = moodsForDate.get(index);

                        // Predefined list of moods with emojis
                        String[] predefinedMoods = {"😊 Happy", "😢 Sad", "😎 Cool", "😡 Angry", "😴 Tired", "🤔 Thinking"};
                        String newMood = (String) JOptionPane.showInputDialog(
                                frame,
                                "Edit mood:",
                                "Edit Mood",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                predefinedMoods,
                                moodEntry.getMood()
                        );

                        if (newMood != null && !newMood.trim().isEmpty()) {
                            moodEntry.setMood(newMood);
                            moodManager.saveMoods();
                            updateDisplay();
                        } else if (newMood != null) {
                            JOptionPane.showMessageDialog(frame, "Mood cannot be empty.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.");
                }
            }
        }
    } else {
        // No existing moods, add a new mood
        addNewMood(selectedDate);
    }
}

private void addNewMood(Date selectedDate) {
    // Predefined list of moods with emojis
    String[] predefinedMoods = {"😊 Happy", "😢 Sad", "😎 Cool", "😡 Angry", "😴 Tired", "🤔 Thinking"};
    String mood = (String) JOptionPane.showInputDialog(
            frame,
            "Enter your mood for the selected date:",
            "Add Mood",
            JOptionPane.QUESTION_MESSAGE,
            null,
            predefinedMoods,
            predefinedMoods[0]
    );

    if (mood != null && !mood.trim().isEmpty()) {
        moodManager.addMoodEntry(selectedDate, mood); // Pass the normalized date
        moodManager.saveMoods();
        updateDisplay();
    } else if (mood != null) {
        JOptionPane.showMessageDialog(frame, "Mood cannot be empty.");
    }
}

private void manageTasks() {
    Date selectedDate = calendar.getDate();
    if (selectedDate == null) {
        JOptionPane.showMessageDialog(frame, "No date selected.");
        return;
    }

    LocalDate localSelectedDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    List<Task> tasksForDate = taskManager.getTasksForDate(localSelectedDate);

    if (tasksForDate.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No tasks for the selected date.");
        return;
    }

    String[] taskOptions = new String[tasksForDate.size()];
    for (int i = 0; i < tasksForDate.size(); i++) {
        Task t = tasksForDate.get(i);
        taskOptions[i] = t.getTaskName() + " [" + t.getStatus() + "] - Priority: " + t.getPriority();
    }

    String selectedTaskStr = (String) JOptionPane.showInputDialog(
            frame,
            "Select a task to manage:",
            "Manage Tasks",
            JOptionPane.PLAIN_MESSAGE,
            null,
            taskOptions,
            taskOptions[0]
    );

    if (selectedTaskStr != null) {
        int index = Arrays.asList(taskOptions).indexOf(selectedTaskStr);
        Task selectedTask = tasksForDate.get(index);

        String[] options = {"Edit Task", "Mark as Completed", "Delete Task", "Cancel"};
        int action = JOptionPane.showOptionDialog(
                frame,
                "What do you want to do with this task?",
                "Task Action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (action == 0) {
            // Edit Task
            editTask(selectedTask);
        } else if (action == 1) {
            // Mark as Completed
            selectedTask.setStatus("Completed");
            taskManager.saveTasks();
            updateDisplay();
        } else if (action == 2) {
            // Delete Task
            boolean deleted = taskManager.deleteTaskByNameAndDate(selectedTask.getTaskName(), localSelectedDate);
            if (deleted) {
                JOptionPane.showMessageDialog(frame, "Task deleted.");
                updateDisplay();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not delete the task.");
            }
        }
    }
}

private void editTask(Task selectedTask) {
    String newTaskName = JOptionPane.showInputDialog(frame, "Edit task name:", selectedTask.getTaskName());
    if (newTaskName == null) return;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String newDateString = JOptionPane.showInputDialog(frame, "Edit task date (yyyy-MM-dd):", dateFormat.format(selectedTask.getDeadline()));
    if (newDateString == null) return;

    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    String newTimeString = JOptionPane.showInputDialog(frame, "Edit task time (hh:mm AM/PM):", timeFormat.format(selectedTask.getDeadline()));
    if (newTimeString == null) return;

    String newStatus = JOptionPane.showInputDialog(frame, "Edit task status:", selectedTask.getStatus());
    if (newStatus == null) return;

    String[] priorities = {"High", "Medium", "Low"};
    String newPriority = (String) JOptionPane.showInputDialog(
            frame,
            "Select new task priority:",
            "Task Priority",
            JOptionPane.QUESTION_MESSAGE,
            null,
            priorities,
            selectedTask.getPriority()
    );
    if (newPriority == null) return;

    try {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        LocalDate localDate = LocalDate.parse(newDateString, dateFormatter);
        LocalTime localTime = LocalTime.parse(newTimeString.trim().toUpperCase(), timeFormatter);

        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Date newDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        selectedTask.setTaskName(newTaskName);
        selectedTask.setDeadline(newDateTime);
        selectedTask.setStatus(newStatus);
        selectedTask.setPriority(newPriority);

        taskManager.saveTasks();
        updateDisplay();
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(frame, "Invalid date/time format. Please use yyyy-MM-dd for the date and hh:mm AM/PM for the time.");
    }
}

    public void updateDisplay() {
        Date selectedDate = calendar.getDate();
        if (selectedDate == null) {
            taskTextPane.setText("No date selected.");
            moodTextPane.setText("No date selected.");
            return;
        }
    
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    
        // Update tasks with colored priorities
        LocalDate localSelectedDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Task> tasks = taskManager.getTasksForDate(localSelectedDate);
        StyledDocument doc = taskTextPane.getStyledDocument();
        taskTextPane.setText("");  // Clear previous content
    
        for (Task task : tasks) {
            try {
                // Split the task text into parts
                String taskPrefix = "• " + task.getTaskName() +
                              " [" + task.getStatus() + "] - " +
                              displayFormat.format(task.getDeadline()) +
                              " - Priority: ";
                
                String priorityText = task.getPriority();
                String newline = "\n";
                
                // Insert normal text first
                doc.insertString(doc.getLength(), taskPrefix, null);
                
                // Create style for priority
                Style style = taskTextPane.addStyle("priorityStyle", null);
                
                // Apply color based on priority
                switch (task.getPriority().toLowerCase()) {
                    case "high":
                        StyleConstants.setForeground(style, Color.RED);
                        StyleConstants.setBold(style, true);
                        break;
                    case "medium":
                        StyleConstants.setForeground(style, Color.ORANGE);
                        break;
                    case "low":
                        StyleConstants.setForeground(style, Color.GREEN.darker());
                        break;
                    default:
                        StyleConstants.setForeground(style, Color.BLACK);
                }
                
                // Insert priority text with style
                doc.insertString(doc.getLength(), priorityText, style);
                
                // Insert newline
                doc.insertString(doc.getLength(), newline, null);
                
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    
        if (tasks.isEmpty()) {
            taskTextPane.setText(""); // First clear everything
            try {
                // Use the existing doc variable instead of declaring a new one
                // Use null style (default black text) for the "no tasks" message
                doc.insertString(0, "No tasks for this day.", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    
        // Update moods
        StringBuilder moodList = new StringBuilder();
        List<MoodEntry> moods = moodManager.getMoodsForDate(selectedDate);
        for (MoodEntry mood : moods) {
            moodList.append("• ").append(mood.getMood()).append("\n");
        }
        moodTextPane.setText(moodList.length() > 0 ? moodList.toString() : "No mood entries for this day.");
    }

    public void show() {
        frame.setVisible(true);
    }
}