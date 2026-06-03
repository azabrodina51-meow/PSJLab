package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.Task;
import ru.itmo.soshzab.domain.TaskPriority;
import ru.itmo.soshzab.domain.TaskStatus;
import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.validation.TaskValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TaskAddCommand implements Command{
    private final TaskService taskService;
    private final Scanner scanner;

    public TaskAddCommand(TaskService taskService, Scanner scanner){

        this.taskService = taskService;
        this.scanner = scanner;
    }


    @Override
    public String execute(String[] args) throws Exception {
        System.out.print("Установите текст задачи: ");
        String text = scanner.nextLine();
        TaskValidator.validateText(text);

        System.out.print("Приоритет (LOW/MEDIUM/HIGH): ");
        String priority = scanner.nextLine().toUpperCase();
        TaskValidator.validatePriority(priority);

        System.out.print("Дедлайн (YYYY-MM-DD, можно оставить пустым): ");
        String deadlineStr = scanner.nextLine().trim();
        Instant deadline = null;
        if (!deadlineStr.isEmpty()) {
            try {
                deadline = LocalDate.parse(deadlineStr).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ошибка: неверный формат даты. Используйте YYYY-MM-DD");
            }
        } else TaskValidator.validateDeadline(Instant.parse(deadlineStr));


        Task task = taskService.addTask(text, TaskPriority.valueOf(priority), TaskStatus.NEW, deadline, null, "SYSTEM");
        return "OK task_id=" + task.getId();
    }
}