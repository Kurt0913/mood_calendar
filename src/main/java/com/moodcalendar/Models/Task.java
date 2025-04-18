package com.moodcalendar.Models;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.text.ParseException;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;  // Optional: version control for serialization

    private String taskName;
    private Date deadline;
    private String status;
    private String priority;

    // Constructor that accepts String for deadline
    public Task(String taskName, String deadlineString, String status, String priority) throws ParseException {
        this.taskName = taskName;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.deadline = dateFormat.parse(deadlineString);
        this.status = status;
        this.priority = priority;
    }

    // Constructor that accepts Date for deadline
    public Task(String taskName, LocalDateTime deadline, String status, String priority) {
        this.taskName = taskName;
        this.deadline = java.util.Date.from(deadline.atZone(java.time.ZoneId.systemDefault()).toInstant());
        this.status = status;
        this.priority = priority;
    }

    // Getters and setters
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
