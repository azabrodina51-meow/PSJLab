package ru.itmo.soshzab.command;

import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.storage.SerializationStorage;
import ru.itmo.soshzab.storage.SerializationValidator;

import java.io.IOException;

public class LoadCommand implements Command {
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final SerializationStorage storage;

    public LoadCommand(TaskService taskService, ChecklistService checklistService, SerializationStorage storage) {
        this.taskService = taskService;
        this.checklistService = checklistService;
        this.storage = storage;
    }

    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите путь к файлу (load <path>)");
        }
        String path = args[1];
        try {
            SerializationStorage.LabData data = storage.load(path);
            SerializationValidator.validate(data);
            taskService.replaceAllTasks(data.tasks);
            checklistService.replaceAllItems(data.items);
            taskService.syncIdGenerator();
            checklistService.syncIdGenerator();
            return "OK данные загружены из " + path;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка чтения файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
