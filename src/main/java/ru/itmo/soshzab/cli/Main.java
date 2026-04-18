package ru.itmo.soshzab.cli;

import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.domain.Task;
import ru.itmo.soshzab.domain.TaskPriority;
import ru.itmo.soshzab.domain.TaskStatus;
import ru.itmo.soshzab.domain.ChecklistItem;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final TaskService taskService = new TaskService();
    private static final ChecklistService checklistService = new ChecklistService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Лабораторная информационная система");
        System.out.println("Введите help для списка команд");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "exit":
                        System.out.println("До свидания!");
                        return;
                    case "help":
                        printHelp();
                        break;
                    case "task_add":
                        taskAdd();
                        break;
                    case "task_list":
                        taskList(parts);
                        break;
                    case "task_show":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id задачи");
                        } else {
                            taskShow(Long.parseLong(parts[1]));
                        }
                        break;
                    case "task_update":
                        taskUpdate(parts);
                        break;
                    case "task_assign":
                        taskAssign(parts);
                        break;
                    case "task_done":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id задачи");
                        } else {
                            taskDone(Long.parseLong(parts[1]));
                        }
                        break;
                    case "task_delete":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id задачи");
                        } else {
                            taskDelete(Long.parseLong(parts[1]));
                        }
                        break;
                    case "check_add":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id задачи");
                        } else {
                            checkAdd(Long.parseLong(parts[1]));
                        }
                        break;
                    case "check_list":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id задачи");
                        } else {
                            checkList(Long.parseLong(parts[1]));
                        }
                        break;
                    case "check_toggle":
                        if (parts.length < 2) {
                            System.out.println("Ошибка: укажите id пункта");
                        } else {
                            checkToggle(Long.parseLong(parts[1]));
                        }
                        break;
                    default:
                        System.out.println("Неизвестная команда. Введите help");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: id должен быть числом");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  help - показать список команд");
        System.out.println("  exit - выйти из программы");
        System.out.println("  task_add - добавить новую задачу");
        System.out.println("  task_list [--status NEW|IN_PROGRESS|DONE] - показать список задач");
        System.out.println("  task_show <id> - показать подробности задачи");
        System.out.println("  task_update <id> field=value... - обновить поля задачи");
        System.out.println("  task_assign <id> <username> - назначить исполнителя");
        System.out.println("  task_done <id> - отметить задачу как выполненную");
        System.out.println("  task_delete <id> - удалить задачу");
        System.out.println("  check_add <task_id> - добавить пункт чек-листа");
        System.out.println("  check_list <task_id> - показать пункты чек-листа");
        System.out.println("  check_toggle <item_id> - переключить статус пункта");
    }

    private static void taskAdd() {
        System.out.print("Текст задачи: ");
        String text = scanner.nextLine();

        System.out.print("Приоритет (LOW/MEDIUM/HIGH): ");
        String priorityStr = scanner.nextLine().toUpperCase();
        TaskPriority priority;
        try {
            priority = TaskPriority.valueOf(priorityStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка: приоритет должен быть LOW, MEDIUM или HIGH");
        }

        System.out.print("Дедлайн (YYYY-MM-DD, можно оставить пустым): ");
        String deadlineStr = scanner.nextLine().trim();
        Instant deadline = null;
        if (!deadlineStr.isEmpty()) {
            try {
                deadline = LocalDate.parse(deadlineStr).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ошибка: неверный формат даты. Используйте YYYY-MM-DD");
            }
        }

        Task task = taskService.addTask(text, priority, TaskStatus.NEW, deadline, null, "SYSTEM");
        System.out.println("OK task_id=" + task.getId());
    }

    private static void taskList(String[] parts) {
        List<Task> tasks;
        if (parts.length >= 3 && parts[1].equals("--status")) {
            try {
                TaskStatus status = TaskStatus.valueOf(parts[2].toUpperCase());
                tasks = taskService.getTasksByStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ошибка: статус должен быть NEW, IN_PROGRESS или DONE");
            }
        } else {
            tasks = taskService.getAllTasks();
        }

        if (tasks.isEmpty()) {
            System.out.println("Нет задач");
            return;
        }

        System.out.println("ID | Статус | Приоритет | Текст");
        System.out.println("---|--------|-----------|------");
        for (Task task : tasks) {
            System.out.printf("%d | %s | %s | %s%n",
                    task.getId(), task.getStatus(), task.getPriority(), task.getText());
        }
    }

    private static void taskShow(long id) {
        Task task = taskService.getTaskById(id);
        System.out.println("Задача #" + task.getId());
        System.out.println("Текст: " + task.getText());
        System.out.println("Приоритет: " + task.getPriority());
        System.out.println("Статус: " + task.getStatus());
        System.out.println("Дедлайн: " + (task.getDeadlineAt() != null ? task.getDeadlineAt() : "не задан"));
        System.out.println("Исполнитель: " + (task.getAssigneeUsername() != null ? task.getAssigneeUsername() : "не назначен"));
        System.out.println("Создатель: " + task.getOwnerUsername());
        System.out.println("Создано: " + task.getCreatedAt());
        System.out.println("Обновлено: " + task.getUpdatedAt());

        List<ChecklistItem> items = checklistService.getItemsByTaskId(id);
        System.out.println("Чек-лист (" + items.size() + "):");
        for (ChecklistItem item : items) {
            System.out.println("  [" + (item.isDone() ? "✓" : " ") + "] " + item.getId() + ": " + item.getText());
        }
    }

    private static void taskUpdate(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Ошибка: укажите id и хотя бы одно поле (field=value)");
            return;
        }

        long id = Long.parseLong(parts[1]);

        String text = null;
        TaskPriority priority = null;
        TaskStatus status = null;
        Instant deadline = null;

        for (int i = 2; i < parts.length; i++) {
            String[] kv = parts[i].split("=", 2);
            if (kv.length != 2) {
                throw new IllegalArgumentException("Ошибка: неверный формат аргумента '" + parts[i] + "'. Используйте field=value");
            }

            String key = kv[0].toLowerCase();
            String value = kv[1];

            switch (key) {
                case "text":
                    text = value;
                    break;
                case "priority":
                    try {
                        priority = TaskPriority.valueOf(value.toUpperCase());
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
        System.out.println("OK задача " + id + " обновлена");
    }

    private static void taskAssign(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Ошибка: укажите id задачи и username исполнителя");
            return;
        }

        long id = Long.parseLong(parts[1]);
        String username = parts[2];

        taskService.updateTask(id, null, null, null, null, username);
        System.out.println("OK задача " + id + " назначена на " + username);
    }

    private static void taskDone(long id) {
        taskService.markTaskDone(id);
        System.out.println("OK задача " + id + " выполнена");
    }

    private static void taskDelete(long id) {
        checklistService.deleteItemsByTaskId(id);
        taskService.deleteTask(id);
        System.out.println("OK задача " + id + " удалена");
    }

    private static void checkAdd(long taskId) {
        taskService.getTaskById(taskId);
        System.out.print("Текст пункта: ");
        String text = scanner.nextLine();
        ChecklistItem item = checklistService.addChecklistItem(taskId, text);
        System.out.println("OK item_id=" + item.getId());
    }

    private static void checkList(long taskId) {
        taskService.getTaskById(taskId);
        List<ChecklistItem> items = checklistService.getItemsByTaskId(taskId);
        if (items.isEmpty()) {
            System.out.println("Нет пунктов чек-листа");
            return;
        }

        System.out.println("ID | Выполнен | Текст | Создано");
        System.out.println("---|----------|-------|--------");
        for (ChecklistItem item : items) {
            System.out.printf("%d | %s | %s | %s%n",
                    item.getId(), (item.isDone() ? "✓" : " "), item.getText(), item.getCreatedAt());
        }
    }

    private static void checkToggle(long itemId) {
        ChecklistItem item = checklistService.toggleItemDone(itemId);
        System.out.println("OK пункт " + itemId + " теперь " + (item.isDone() ? "ВЫПОЛНЕН" : "НЕ ВЫПОЛНЕН"));
    }
}