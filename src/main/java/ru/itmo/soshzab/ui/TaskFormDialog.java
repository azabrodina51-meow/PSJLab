package ru.itmo.soshzab.ui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ru.itmo.soshzab.domain.*;
import ru.itmo.soshzab.service.*;
import ru.itmo.soshzab.validation.*;

import java.time.*;
import java.util.*;

public class TaskFormDialog {
    private final TaskService taskService;

    public TaskFormDialog(TaskService taskService) {
        this.taskService = taskService;
    }

    public boolean showNewTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Добавить задачу");
        dialog.setHeaderText("Создание новой задачи");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField = new TextField();
        textField.setPromptText("Текст задачи");
        ComboBox<TaskPriority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Arrays.asList(TaskPriority.values()));
        priorityBox.setValue(TaskPriority.MEDIUM);

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("YYYY-MM-DD (необязательно)");

        TextField assigneeField = new TextField();
        assigneeField.setPromptText("Исполнитель (необязательно)");

        grid.add(new Label("Текст задачи:"), 0, 0);
        grid.add(textField, 1, 0);
        grid.add(new Label("Приоритет:"), 0, 1);
        grid.add(priorityBox, 1, 1);
        grid.add(new Label("Дедлайн:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Исполнитель:"), 0, 3);
        grid.add(assigneeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String text = textField.getText().trim();
                    TaskValidator.validateText(text);

                    TaskPriority priority = priorityBox.getValue();
                    if (priority == null) {
                        throw new IllegalArgumentException("Выберите приоритет");
                    }

                    Instant deadline = null;
                    LocalDate date = datePicker.getValue();
                    if (date != null) {
                        deadline = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
                        TaskValidator.validateDeadline(deadline);
                    }

                    String assignee = assigneeField.getText().trim();
                    if (!assignee.isEmpty()) {
                        TaskValidator.validateAssignee(assignee);
                    }

                    return taskService.addTask(text, priority, TaskStatus.NEW, deadline, assignee, "SYSTEM");
                } catch (IllegalArgumentException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
                    return null;
                }
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        return result.isPresent();
    }

    public boolean showEditTaskDialog(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Изменить задачу");
        dialog.setHeaderText("Редактирование задачи #" + task.getId());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField = new TextField(task.getText());
        textField.setPromptText("Текст задачи");

        ComboBox<TaskPriority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Arrays.asList(TaskPriority.values()));
        priorityBox.setValue(task.getPriority());

        ComboBox<TaskStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Arrays.asList(TaskStatus.values()));
        statusBox.setValue(task.getStatus());

        DatePicker datePicker;
        if (task.getDeadlineAt() != null) {
            LocalDate date = task.getDeadlineAt().atZone(ZoneId.systemDefault()).toLocalDate();
            datePicker = new DatePicker(date);
        } else {
            datePicker = new DatePicker();
        }
        datePicker.setPromptText("YYYY-MM-DD");

        TextField assigneeField = new TextField(task.getAssigneeUsername() != null ? task.getAssigneeUsername() : "");
        assigneeField.setPromptText("Исполнитель");

        grid.add(new Label("Текст задачи:"), 0, 0);
        grid.add(textField, 1, 0);
        grid.add(new Label("Приоритет:"), 0, 1);
        grid.add(priorityBox, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusBox, 1, 2);
        grid.add(new Label("Дедлайн:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Исполнитель:"), 0, 4);
        grid.add(assigneeField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String text = textField.getText().trim();
                    if (!text.isEmpty()) {
                        TaskValidator.validateText(text);
                    }

                    TaskPriority priority = priorityBox.getValue();
                    TaskStatus status = statusBox.getValue();

                    Instant deadline = null;
                    LocalDate date = datePicker.getValue();
                    if (date != null) {
                        deadline = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
                        TaskValidator.validateDeadline(deadline);
                    }

                    String assignee = assigneeField.getText().trim();
                    if (!assignee.isEmpty()) {
                        TaskValidator.validateAssignee(assignee);
                    }

                    taskService.updateTask(task.getId(),
                            text.isEmpty() ? null : text,
                            priority,
                            status,
                            deadline,
                            assignee.isEmpty() ? null : assignee
                    );

                    return task;
                } catch (IllegalArgumentException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
                    return null;
                }
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        return result.isPresent();
    }
}
