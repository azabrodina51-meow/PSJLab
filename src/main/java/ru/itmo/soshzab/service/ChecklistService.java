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

    public ChecklistItem toggleItemDone(long id) {
        ChecklistItem item = getItemById(id);
        item.setDone(!item.isDone());
        item.setUpdatedAt(Instant.now());
        return item;
    }

    public void deleteItemsByTaskId(long taskId) {
        items.values().removeIf(item -> item.getTaskId() == taskId);
    }

    public Map<Long, ChecklistItem> getItemsMap() { return items; }

    public void replaceAllItems(Map<Long, ChecklistItem> newItems) {
        items.clear();
        items.putAll(newItems);
    }

    public void syncIdGenerator() {
        long maxId = items.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        idGenerator.setCurrentId(maxId + 1);
    }
}