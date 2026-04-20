package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.domain.Task;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;

import java.util.List;

public class TaskShowCommand implements Command{
    private final TaskService taskService;
    private final ChecklistService checklistService;

    public TaskShowCommand(TaskService taskService, ChecklistService checklistService){

        this.taskService = taskService;
        this.checklistService = checklistService;
    }
    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи");
        }
        long id = Long.parseLong(args[1]);

        Task task = taskService.getTaskById(id);

        StringBuilder sb = new StringBuilder();
        sb.append("Задача #").append(task.getId()).append("\n");
        sb.append("Текст: ").append(task.getText()).append("\n");
        sb.append("Приоритет: ").append(task.getPriority()).append("\n");
        sb.append("Статус: ").append(task.getStatus()).append("\n");
        sb.append("Дедлайн: ").append(task.getDeadlineAt() != null ? task.getDeadlineAt() : "не задан").append("\n");
        sb.append("Исполнитель: ").append(task.getAssigneeUsername() != null ? task.getAssigneeUsername() : "не назначен").append("\n");
        sb.append("Создатель: ").append(task.getOwnerUsername()).append("\n");
        sb.append("Создано: ").append(task.getCreatedAt()).append("\n");
        sb.append("Обновлено: ").append(task.getUpdatedAt()).append("\n");

        List<ChecklistItem> items = checklistService.getItemsByTaskId(id);
        sb.append("Чек-лист (").append(items.size()).append("):\n");
        for (ChecklistItem item : items) {
            sb.append("  [").append(item.isDone() ? "✓" : " ").append("] ").append(item.getId()).append(": ").append(item.getText()).append("\n");
        }

        return sb.toString();
    }
}
