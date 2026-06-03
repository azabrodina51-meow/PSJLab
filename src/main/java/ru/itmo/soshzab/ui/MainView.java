package ru.itmo.soshzab.ui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import ru.itmo.soshzab.domain.*;
import ru.itmo.soshzab.service.*;
import ru.itmo.soshzab.storage.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class MainView {
    private final BorderPane root = new BorderPane();
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final TaskFormDialog taskFormDialog;
    private final SerializationStorage storage;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private final TableView<Task> tableView = new TableView<>(taskList);

    public MainView(TaskService taskService, ChecklistService checklistService, SerializationStorage storage) {
        this.taskService = taskService;
        this.checklistService = checklistService;
        this.storage = storage;
        this.taskFormDialog = new TaskFormDialog(taskService);

        setupTableColumns();
        setupButtonPanel();

        root.setCenter(tableView);
        refreshTable();
    }


    private void setupTableColumns() {
        TableColumn<Task, Long> colId = createSimpleColumn("ID", "id", 60);
        TableColumn<Task, String> colText = createSimpleColumn("Текст задачи", "text", 250);
        TableColumn<Task, String> colPriority = createSimpleColumn("Приоритет", "priority", 100);
        TableColumn<Task, String> colStatus = createSimpleColumn("Статус", "status", 120);

        TableColumn<Task, String> colDeadline = createDeadlineColumn();
        TableColumn<Task, String> colAssignee = createAssigneeColumn();

        tableView.getColumns().addAll(colId, colText, colPriority, colStatus, colDeadline, colAssignee);
    }

    private <T> TableColumn<Task, T> createSimpleColumn(String title, String property, double width) {
        TableColumn<Task, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    private TableColumn<Task, String> createDeadlineColumn() {
        TableColumn<Task, String> col = new TableColumn<>("Дедлайн");
        col.setCellValueFactory(cellData -> {
            Instant deadline = cellData.getValue().getDeadlineAt();
            String formatted = (deadline != null)
                    ? DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .format(deadline.atZone(ZoneId.systemDefault()).toLocalDateTime())
                    : "—";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        col.setPrefWidth(120);
        return col;
    }

    private TableColumn<Task, String> createAssigneeColumn() {
        TableColumn<Task, String> col = new TableColumn<>("Исполнитель");
        col.setCellValueFactory(cellData -> {
            String assignee = cellData.getValue().getAssigneeUsername();
            return new javafx.beans.property.SimpleStringProperty(assignee != null ? assignee : "—");
        });
        col.setPrefWidth(150);
        return col;
    }

    private void setupButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setStyle("-fx-background-color: #f4f4f4;");

        Button btnAdd = new Button("➕ Добавить");
        btnAdd.setOnAction(e -> handleAdd());

        Button btnEdit = new Button("✏️ Изменить");
        btnEdit.setOnAction(e -> handleEdit());

        Button btnDelete = new Button("🗑 Удалить");
        btnDelete.setOnAction(e -> handleDelete());

        Button btnDone = new Button("✅ Выполнено");
        btnDone.setOnAction(e -> handleDone());

        Button btnAssign = new Button("👤 Назначить");
        btnAssign.setOnAction(e -> handleAssign());

        Button btnSave = new Button("💾 Сохранить");
        btnSave.setOnAction(e -> handleSave());

        Button btnLoad = new Button("📂 Загрузить");
        btnLoad.setOnAction(e -> handleLoad());

        Button btnChecklist = new Button("📋 Чек-лист");
        btnChecklist.setOnAction(e -> handleChecklist());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setOnAction(e -> refreshTable());

        buttonPanel.getChildren().addAll(btnAdd, btnEdit, btnDelete, btnDone, btnAssign, btnSave, btnLoad, btnChecklist, btnRefresh);
        root.setBottom(buttonPanel);
    }

    private void handleAdd() {
        boolean created = taskFormDialog.showNewTaskDialog();
        if (created) {
            refreshTable();
            UIHelper.showInfo("Задача создана");
        }
    }

    private void handleEdit() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            UIHelper.showWarning("Выберите задачу для редактирования");
            return;
        }

        boolean updated = taskFormDialog.showEditTaskDialog(selectedTask);
        if (updated) {
            refreshTable();
            UIHelper.showInfo("Задача обновлена");
        }
    }

    private void handleDelete() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            UIHelper.showWarning("Выберите задачу для удаления");
            return;
        }

        if (UIHelper.showConfirmation("Удалить задачу #" + selectedTask.getId() + "?")) {
            try {
                taskService.deleteTask(selectedTask.getId());
                refreshTable();
                UIHelper.showInfo("Задача удалена");
            } catch (Exception e) {
                UIHelper.showError("Ошибка удаления: " + e.getMessage());
            }
        }
    }

    private void handleDone() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            UIHelper.showWarning("Выберите задачу");
            return;
        }

        try {
            taskService.markTaskDone(selectedTask.getId());
            refreshTable();
            UIHelper.showInfo("Задача #" + selectedTask.getId() + " выполнена");
        } catch (Exception e) {
            UIHelper.showError("Ошибка: " + e.getMessage());
        }
    }

    private void handleAssign() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            UIHelper.showWarning("Выберите задачу");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Назначить исполнителя");
        dialog.setHeaderText("Задача #" + selectedTask.getId());
        dialog.setContentText("Введите username исполнителя:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            try {
                if (username.trim().isEmpty()) {
                    throw new IllegalArgumentException("Имя исполнителя не может быть пустым");
                }
                taskService.updateTask(selectedTask.getId(), null, null, null, null, username.trim());
                refreshTable();
                UIHelper.showInfo("Задача назначена на " + username);
            } catch (Exception e) {
                UIHelper.showError("Ошибка назначения: " + e.getMessage());
            }
        });
    }

    private void handleChecklist() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            UIHelper.showWarning("Выберите задачу для управления чек-листом");
            return;
        }

        ChecklistDialog dialog = new ChecklistDialog(selectedTask, checklistService);
        dialog.showDialog();
        refreshTable();
    }

    private void handleSave() {
        TextInputDialog dialog = new TextInputDialog("data.bin");
        dialog.setTitle("Сохранение данных");
        dialog.setHeaderText("Введите путь к файлу:");
        dialog.setContentText("Файл:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(path -> {
            try {
                if (path.trim().isEmpty()) {
                    throw new IllegalArgumentException("Путь не может быть пустым");
                }

                storage.save(path, taskService.getTasksMap(), checklistService.getItemsMap());
                UIHelper.showInfo("Данные успешно сохранены в " + path);
            } catch (IOException e) {
                UIHelper.showError("Ошибка сохранения: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UIHelper.showError(e.getMessage());
            }
        });
    }

    private void handleLoad() {
        TextInputDialog dialog = new TextInputDialog("data.bin");
        dialog.setTitle("Загрузка данных");
        dialog.setHeaderText("Введите путь к файлу:");
        dialog.setContentText("Файл:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(path -> {
            try {
                if (path.trim().isEmpty()) {
                    throw new IllegalArgumentException("Путь не может быть пустым");
                }

                SerializationStorage.LabData data = storage.load(path);

                taskService.replaceAllTasks(data.tasks);
                checklistService.replaceAllItems(data.items);

                taskService.syncIdGenerator();
                checklistService.syncIdGenerator();

                refreshTable();

                UIHelper.showInfo("Данные успешно загружены из " + path);
            } catch (IOException e) {
                UIHelper.showError("Ошибка загрузки: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UIHelper.showError(e.getMessage());
            } catch (ClassNotFoundException e) {
                UIHelper.showError("Неверный формат файла (ошибка сериализации)");
            }
        });
    }

    public void refreshTable() {
        try {
            taskList.clear();
            List<Task> freshData = taskService.getAllTasks();
            taskList.addAll(freshData);
            tableView.setItems(taskList);
        } catch (Exception e) {
            UIHelper.showError("Ошибка обновления: " + e.getMessage());
        }
    }

    public BorderPane getRoot() {
        return root;
    }

}