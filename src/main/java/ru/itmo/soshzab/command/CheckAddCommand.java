package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.validation.ChecklistItemValidator;

import java.util.Scanner;

public class CheckAddCommand implements Command{
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final Scanner scanner;

    public CheckAddCommand(TaskService taskService, ChecklistService checklistService, Scanner scanner){
      this.taskService = taskService;
      this.checklistService = checklistService;
      this.scanner = scanner;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи");
        }
        long taskId = Long.parseLong(args[1]);

        taskService.getTaskById(taskId);

        System.out.print("Текст пункта: ");
        String text = scanner.nextLine();
        ChecklistItemValidator.validateText(text);

        ChecklistItem item = checklistService.addChecklistItem(taskId, text);
        return "OK item_id=" + item.getId();
    }
}
