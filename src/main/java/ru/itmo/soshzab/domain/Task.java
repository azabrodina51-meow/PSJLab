package ru.itmo.soshzab.domain;

import java.time.Instant;

public final class Task {
    private long id;
    private String text;
    private TaskPriority priority;
    private TaskStatus status;
    private Instant deadlineAt;
    private String assigneeUsername;
    private final String ownerUsername;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task(String text, TaskPriority priority, TaskStatus status,
                Instant deadlineAt, String assigneeUsername, String ownerUsername) {
        this.text = text;
        this.priority = priority;
        this.status = status;
        this.deadlineAt = deadlineAt;
        this.assigneeUsername = assigneeUsername;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() { return id; }
    public String getText() { return text; }
    public TaskPriority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public Instant getDeadlineAt() { return deadlineAt; }
    public String getAssigneeUsername() { return assigneeUsername; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(long id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDeadlineAt(Instant deadlineAt) { this.deadlineAt = deadlineAt; }
    public void setAssigneeUsername(String assigneeUsername) { this.assigneeUsername = assigneeUsername; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}