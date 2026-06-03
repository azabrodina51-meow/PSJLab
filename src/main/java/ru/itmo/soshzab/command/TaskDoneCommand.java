package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.TaskService;

public class TaskDoneCommand implements Command{
    private final TaskService taskService;

    public TaskDoneCommand(TaskService taskService){
        this.taskService = taskService;
    }
    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи");
        }
        long id = Long.parseLong(args[1]);

        taskService.markTaskDone(id);
        return "OK задача " + id + " выполнена";
    }
}
