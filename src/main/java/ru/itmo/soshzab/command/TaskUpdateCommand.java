package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.TaskPriority;
import ru.itmo.soshzab.domain.TaskStatus;
import ru.itmo.soshzab.service.TaskService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskUpdateCommand implements Command{
    private final TaskService taskService;

    public TaskUpdateCommand(TaskService taskService){
        this.taskService = taskService;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Ошибка: укажите id и хотя бы одно поле (field=value)");
        }

        long id = Long.parseLong(args[1]);

        String text = null;
        TaskPriority priority = null;
        TaskStatus status = null;
        Instant deadline = null;

        for (int i = 2; i < args.length; i++) {
            String[] kv = args[i].split("=", 2);
            if (kv.length != 2) {
                throw new IllegalArgumentException("Ошибка: неверный формат аргумента '" + args[i] + "'. Используйте field=value");
            }

            String key = kv[0].toLowerCase();
            String value = kv[1];

            switch (key) {
                case "text":
                    text = value;
                    break;
                case "priority":
                    try {
                        priority = TaskPriority.valueOf(value.toUpperCase()) ;
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Ошибка: приоритет должен быть LOW, MEDIUM или HIGH");
                    }
                    break;
                case "status":
                    try {
                        status = TaskStatus.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Ошибка: статус должен быть NEW, IN_PROGRESS или DONE");
                    }
                    break;
                case "deadline":
                    try {
                        deadline = LocalDate.parse(value).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Ошибка: неверный формат даты. Используйте YYYY-MM-DD");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Ошибка: неизвестное поле '" + key + "'. Разрешены: text, priority, status, deadline");
            }
        }

        taskService.updateTask(id, text, priority, status, deadline, null);
        return "OK задача " + id + " обновлена";
    }
}
