package ru.itmo.soshzab.validation;

public class ChecklistItemValidator {

    public static void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: текст пункта не может быть пустым");
        }
        if (text.length() > 256) {
            throw new IllegalArgumentException("Ошибка: текст пункта слишком длинный (макс. 256 символов)");
        }
    }

    public static void validateTaskId(long taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Ошибка: ID задачи должен быть положительным числом");
        }
    }

    public static void validateChecklistItem(long taskId, String text) {
        validateTaskId(taskId);
        validateText(text);
    }
}