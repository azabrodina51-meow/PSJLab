package ru.itmo.soshzab.validation;

import ru.itmo.soshzab.domain.TaskPriority;
import ru.itmo.soshzab.domain.TaskStatus;
import java.time.Instant;

public class TaskValidator {

    public static void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: текст задачи не может быть пустым");
        }
        if (text.length() > 256) {
            throw new IllegalArgumentException("Ошибка: текст задачи слишком длинный (макс. 256 символов)");
        }
    }

    public static void validatePriority(TaskPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Ошибка: приоритет не может быть пустым");
        }
    }

    public static void validateStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Ошибка: статус не может быть пустым");
        }
    }

    public static void validateDeadline(Instant deadline) {
        if (deadline != null && deadline.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Ошибка: дедлайн не может быть в прошлом");
        }
    }

    public static void validateAssignee(String assignee) {
        if (assignee != null && assignee.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: имя исполнителя не может быть пустым");
        }
    }

    public static void validateTask(String text, TaskPriority priority, TaskStatus status,
                                    Instant deadline, String assignee) {
        validateText(text);
        validatePriority(priority);
        validateStatus(status);
        validateDeadline(deadline);
        validateAssignee(assignee);
    }

    public static void validateTaskUpdate(String text, TaskPriority priority, TaskStatus status,
                                          Instant deadline, String assignee) {
        if (text != null) {
            validateText(text);
        }
        if (priority != null) {
            validatePriority(priority);
        }
        if (status != null) {
            validateStatus(status);
        }
        if (deadline != null) {
            validateDeadline(deadline);
        }
        if (assignee != null) {
            validateAssignee(assignee);
        }
    }
}