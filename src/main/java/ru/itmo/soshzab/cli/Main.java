package ru.itmo.soshzab.cli;

import ru.itmo.soshzab.command.*;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final TaskService taskService = new TaskService();
    private static final ChecklistService checklistService = new ChecklistService();
    private static final Scanner scanner = new Scanner(System.in);

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_MAP.put("task_add", new TaskAddCommand(taskService, scanner));
        COMMAND_MAP.put("task_assign", new TaskAssignCommand(taskService));
        COMMAND_MAP.put("task_delete", new TaskDeleteCommand(taskService,checklistService));
        COMMAND_MAP.put("task_done", new TaskDoneCommand(taskService));
        COMMAND_MAP.put("task_list", new TaskListCommand(taskService));
        COMMAND_MAP.put("task_show", new TaskShowCommand(taskService, checklistService));
        COMMAND_MAP.put("task_update", new TaskUpdateCommand(taskService));
        COMMAND_MAP.put("check_add", new CheckAddCommand(taskService, checklistService, scanner));
        COMMAND_MAP.put("check_list", new CheckListCommand(taskService, checklistService));
        COMMAND_MAP.put("check_toggle", new CheckToggleCommand(checklistService));
    }

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
            String commandName = parts[0].toLowerCase();

            try {
                if (commandName.equals("exit")) {
                    System.out.println("До свидания!");
                    return;
                } else if (commandName.equals("help")) {
                    printHelp();
                    continue;
                }

                Command commandToExecute = COMMAND_MAP.get(commandName);
                if (commandToExecute != null) {
                    String result = commandToExecute.execute(parts);
                    System.out.println(result);
                } else {
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

    }