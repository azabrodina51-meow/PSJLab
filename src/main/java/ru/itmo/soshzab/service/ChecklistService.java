package ru.itmo.soshzab.service;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.validation.ChecklistItemValidator;
import java.time.Instant;
import java.util.*;

public class ChecklistService {
    private final Map<Long, ChecklistItem> items = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public ChecklistItem addChecklistItem(long taskId, String text) {
        ChecklistItemValidator.validateChecklistItem(taskId, text);
        ChecklistItem item = new ChecklistItem(taskId, text);
        item.setId(idGenerator.nextId());
        items.put(item.getId(), item);
        return item;
    }

    // ✅ Обязательный метод менеджера (п.3)
    public ChecklistItem getItemById(long id) {
        ChecklistItem item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Ошибка: пункт чек-листа с id=" + id + " не найден");
        }
        return item;
    }

    public List<ChecklistItem> getItemsByTaskId(long taskId) {
        List<ChecklistItem> result = new ArrayList<>();
        for (ChecklistItem item : items.values()) {
            if (item.getTaskId() == taskId) {
                result.add(item);
            }
        }
        return result;
    }

    public int getItemCountByTaskId(long taskId) {
        int count = 0;
        for (ChecklistItem item : items.values()) {
            if (item.getTaskId() == taskId) {
                count++;
            }
        }
        return count;
    }

    public void deleteItem(long id) {
        if (!items.containsKey(id)) {
            throw new IllegalArgumentException("Ошибка: пункт чек-листа с id=" + id + " не найден");
        }
        items.remove(id);
    }

    public void updateItemText(long id, String newText) {
        ChecklistItem item = getItemById(id);
        ChecklistItemValidator.validateText(newText);
        item.setText(newText);
        item.setUpdatedAt(Instant.now());
    }

    public ChecklistItem toggleItemDone(long id) {
        ChecklistItem item = getItemById(id);
        item.setDone(!item.isDone());
        item.setUpdatedAt(Instant.now());
        return item;
    }

    public void deleteItemsByTaskId(long taskId) {
        items.values().removeIf(item -> item.getTaskId() == taskId);
    }
}