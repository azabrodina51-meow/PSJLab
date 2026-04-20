package ru.itmo.soshzab.service;

import ru.itmo.soshzab.domain.Task;
import ru.itmo.soshzab.domain.TaskPriority;
import ru.itmo.soshzab.domain.TaskStatus;
import ru.itmo.soshzab.validation.TaskValidator;
import java.time.Instant;
import java.util.*;

public class TaskService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public Task addTask(String text, TaskPriority priority, TaskStatus status,
                        Instant deadline, String assignee, String owner) {
        TaskValidator.validateTask(text, String.valueOf(priority) , String.valueOf(status), deadline, assignee);
        Task task = new Task(text, priority, status, deadline, assignee, owner);
        task.setId(idGenerator.nextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task getTaskById(long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Ошибка: задача с id=" + id + " не найдена");
        }
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getStatus() == status) {
                result.add(task);
            }
        }
        return result;
    }

    public void updateTask(long id, String text, TaskPriority priority,
                           TaskStatus status, Instant deadline, String assignee) {
        Task task = getTaskById(id);
        TaskValidator.validateTaskUpdate(text, String.valueOf(priority), String.valueOf(status), deadline, assignee);
        if (text != null) task.setText(text);
        if (priority != null) task.setPriority(priority);
        if (status != null) task.setStatus(status);
        if (deadline != null) task.setDeadlineAt(deadline);
        if (assignee != null) task.setAssigneeUsername(assignee);
        task.setUpdatedAt(Instant.now());
    }

    public void deleteTask(long id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Ошибка: задача с id=" + id + " не найдена");
        }
        tasks.remove(id);
    }

    public void markTaskDone(long id) {
        Task task = getTaskById(id);
        task.setStatus(TaskStatus.DONE);
        task.setUpdatedAt(Instant.now());
    }

}