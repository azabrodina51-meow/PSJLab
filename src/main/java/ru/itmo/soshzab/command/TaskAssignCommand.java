package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.TaskService;

public class TaskAssignCommand implements Command{
    private final TaskService taskService;

    public TaskAssignCommand(TaskService taskService){
        this.taskService = taskService;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Ошибка: укажите id задачи и username исполнителя");
        }

        long id = Long.parseLong(args[1]);
        String username = args[2];

        taskService.updateTask(id, null, null, null, null, username);
        return "OK задача " + id + " назначена на " + username;
    }
}
