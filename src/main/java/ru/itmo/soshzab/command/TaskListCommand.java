package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.domain.*;
import java.util.List;


public class TaskListCommand implements Command{
    private final TaskService taskService;

    public TaskListCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String execute(String[] args) throws Exception {
        List<Task> tasks;
        if (args.length >= 3 && args[1].equals("--status")) {
            try {
                TaskStatus status = TaskStatus.valueOf(args[2].toUpperCase());
                tasks = taskService.getTasksByStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ошибка: статус должен быть NEW, IN_PROGRESS или DONE");
            }
        } else {
            tasks = taskService.getAllTasks();
        }

        if (tasks.isEmpty()) {
            return "Нет задач";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID | Статус | Приоритет | Текст\n");
        sb.append("---|--------|-----------|------\n");
        for (Task task : tasks) {
            sb.append(String.format("%d | %s | %s | %s%n",
                    task.getId(), task.getStatus(), task.getPriority(), task.getText()));
        }
        return sb.toString();

    }
}
