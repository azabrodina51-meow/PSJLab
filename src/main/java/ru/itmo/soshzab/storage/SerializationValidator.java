package ru.itmo.soshzab.storage;

import ru.itmo.soshzab.domain.*;

import java.util.*;

public class SerializationValidator {

    public static void validate(SerializationStorage.LabData data) {
        if (data == null || data.tasks == null || data.items == null) {
            throw new IllegalArgumentException("Ошибка загрузки: структура файла повреждена (отсутствуют коллекции)");
        }

        Set<Long> taskIds = new HashSet<>();
        for (Task task : data.tasks.values()) {
            if (task == null) continue;
            if (!taskIds.add(task.getId())) {
                throw new IllegalArgumentException("Ошибка загрузки: обнаружен дублирующийся id задачи=" + task.getId());
            }
            validateTaskFields(task);
        }

        Set<Long> itemIds = new HashSet<>();
        for (ChecklistItem item : data.items.values()) {
            if (item == null) continue;
            if (!itemIds.add(item.getId())) {
                throw new IllegalArgumentException("Ошибка загрузки: обнаружен дублирующийся id пункта=" + item.getId());
            }
            validateChecklistItemFields(item);

            if (!data.tasks.containsKey(item.getTaskId())) {
                throw new IllegalArgumentException("Ошибка загрузки: пункт чек-листа id=" + item.getId() +
                        " ссылается на несуществующую задачу taskId=" + item.getTaskId());
            }
        } }

    private static void validateTaskFields(Task task) {
        if (task.getText() == null || task.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка загрузки: текст задачи не может быть пустым (id=" + task.getId() + ")");
        }
        if (task.getText().length() > 256) {
            throw new IllegalArgumentException("Ошибка загрузки: текст задачи слишком длинный (макс. 256) (id=" + task.getId() + ")");
        }
        if (task.getPriority() == null) {
            throw new IllegalArgumentException("Ошибка загрузки: приоритет задачи не может быть null (id=" + task.getId() + ")");
        }
        if (task.getStatus() == null) {
            throw new IllegalArgumentException("Ошибка загрузки: статус задачи не может быть null (id=" + task.getId() + ")");
        }
        if (task.getOwnerUsername() == null || task.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка загрузки: владелец задачи не может быть пустым (id=" + task.getId() + ")");
        }
    }

    private static void validateChecklistItemFields(ChecklistItem item) {
        if (item.getText() == null || item.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка загрузки: текст пункта чек-листа не может быть пустым (id=" + item.getId() + ")");
        }
        if (item.getText().length() > 256) {
            throw new IllegalArgumentException("Ошибка загрузки: текст пункта чек-листа слишком длинный (макс. 256) (id=" + item.getId() + ")");
        }
        if (item.getTaskId() <= 0) {
            throw new IllegalArgumentException("Ошибка загрузки: ID задачи в пункте чек-листа должен быть положительным (id=" + item.getId() + ")");
        }
    }
}
