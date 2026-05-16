package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.*;
import ru.itmo.soshzab.storage.*;

import java.io.IOException;

public class SaveCommand implements Command{
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final SerializationStorage storage;

    public SaveCommand(TaskService taskService, ChecklistService checklistService, SerializationStorage storage) {
        this.taskService = taskService;
        this.checklistService = checklistService;
        this.storage = storage;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите путь к файлу (save <path>)");
        }
        String path = args[1];
        try {
            storage.save(path, taskService.getTasksMap(), checklistService.getItemsMap());
            return "OK данные сохранены в " + path;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения: " + e.getMessage());
        }
    }
}
