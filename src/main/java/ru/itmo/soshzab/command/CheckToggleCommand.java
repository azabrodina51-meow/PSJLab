package ru.itmo.soshzab.command;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.service.ChecklistService;

public class CheckToggleCommand implements Command{
    private final ChecklistService checklistService;

    public CheckToggleCommand(ChecklistService checklistService){
        this.checklistService = checklistService;
    }
    @Override
    public String execute(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Ошибка: укажите id пункта");
        }
        long itemId = Long.parseLong(args[1]);

        ChecklistItem item = checklistService.toggleItemDone(itemId);
        return "OK пункт " + itemId + " теперь " + (item.isDone() ? "ВЫПОЛНЕН" : "НЕ ВЫПОЛНЕН");
    }
}
