package ru.itmo.soshzab.domain;

import java.time.Instant;

public final class ChecklistItem {
    private long id;
    private final long taskId;
    private final String text;
    private boolean done;
    private final Instant createdAt;
    private Instant updatedAt;

    public ChecklistItem(long taskId, String text) {
        this.taskId = taskId;
        this.text = text;
        this.done = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() { return id; }
    public long getTaskId() { return taskId; }
    public String getText() { return text; }
    public boolean isDone() { return done; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setDone(boolean done) { this.done = done; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}