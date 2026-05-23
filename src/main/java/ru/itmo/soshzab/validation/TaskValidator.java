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

    public static void validatePriority(String priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Ошибка: приоритет не может быть null");
        }
        try {
            TaskPriority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимое значение приоритета");
        }
    }

    public static void validateStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Ошибка: статус не может быть null");
        }
        try {
            TaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимое значение статуса");
        }
    }

    public static void validateDeadline(Instant deadline) {
        if (deadline != null && deadline.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Ошибка: дедлайн не может быть в прошлом");
        }
    }

    public static void validateAssignee(String assignee) {
         if (assignee == null || assignee.trim().isEmpty()) {
            return;
        }

        if (assignee.length() > 64) {
            throw new IllegalArgumentException("Ошибка: имя исполнителя слишком длинное (макс. 64 символа)");
        }
    }

    public static void validateTask(String text, String priority, String status,
                                    Instant deadline, String assignee) {
        validateText(text);
        validatePriority(priority);
        validateStatus(status);
        validateDeadline(deadline);
        validateAssignee(assignee);
    }

    public static void validateTaskUpdate(String text, String priority, String status,
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