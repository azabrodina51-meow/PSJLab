package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;

import java.util.List;

public class CheckListCommand implements Command{
    private final TaskService taskService;
    private final ChecklistService checklistService;

    public CheckListCommand(TaskService taskService, ChecklistService checklistService){
        this.taskService = taskService;
        this.checklistService = checklistService;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи");
        }
        long taskId = Long.parseLong(args[1]);

        taskService.getTaskById(taskId);

        List<ChecklistItem> items = checklistService.getItemsByTaskId(taskId);
        if (items.isEmpty()) {
            return "Нет пунктов чек-листа";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID | Выполнен | Текст | Создано\n");
        sb.append("---|----------|-------|--------\n");
        for (ChecklistItem item : items) {
            sb.append(String.format("%d | %s | %s | %s%n",
                    item.getId(), (item.isDone() ? "✓" : " "), item.getText(), item.getCreatedAt()));
        }
        return sb.toString();
    }
}
