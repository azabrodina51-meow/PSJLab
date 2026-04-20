package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;

public class TaskDeleteCommand implements Command{
    private final TaskService taskService;
    private final ChecklistService checklistService;

    public TaskDeleteCommand(TaskService taskService, ChecklistService checklistService){
        this.taskService = taskService;
        this.checklistService = checklistService;
    }
    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи");
        }
        long id = Long.parseLong(args[1]);

        checklistService.deleteItemsByTaskId(id);
        taskService.deleteTask(id);
        return "OK задача " + id + " удалена";
    }
}
