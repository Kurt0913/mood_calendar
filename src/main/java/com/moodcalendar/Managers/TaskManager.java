package com.moodcalendar.Managers;

import com.moodcalendar.Models.Task;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private final String TASK_FILE = "tasks.ser";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks(); // Load saved tasks when initialized
    }

    // Add a task to the task list
    public void addTask(String taskName, LocalDateTime deadline, String status, String priority) {
        Task newTask = new Task(taskName, deadline, status, priority);
        tasks.add(newTask);
        saveTasks(); // Save tasks after adding
    }

    // Retrieve tasks for a specific date
    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();

        for (Task task : tasks) {
            if (dateFormatter.format(task.getDeadline().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()).equals(dateFormatter.format(date))) {
                result.add(task);
            }
        }
        return result;
    }

    // Delete a task by name and date
    public boolean deleteTaskByNameAndDate(String name, LocalDate date) {
        Iterator<Task> iterator = tasks.iterator();
        boolean deleted = false;

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getTaskName().equalsIgnoreCase(name) && task.getDeadline().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(date)) {
                iterator.remove();
                deleted = true;
            }
        }

        if (deleted) {
            saveTasks();
        }

        return deleted;
    }

    // Save tasks to a file
    public void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TASK_FILE))) {
            oos.writeObject(tasks);
            System.out.println("Tasks saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // Load tasks from a file
    @SuppressWarnings("unchecked")
    public void loadTasks() {
        File file = new File(TASK_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (List<Task>) ois.readObject();
                System.out.println("Tasks loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }
    }
}