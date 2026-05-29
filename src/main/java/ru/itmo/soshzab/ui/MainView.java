package ru.itmo.soshzab.ui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import ru.itmo.soshzab.domain.*;
import ru.itmo.soshzab.service.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class MainView {
    private final BorderPane root = new BorderPane();
    private final TaskService taskService;
    private final ChecklistService checklistService;
    private final TaskFormDialog taskFormDialog;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private final TableView<Task> tableView = new TableView<>(taskList);

    public MainView(TaskService taskService, ChecklistService checklistService) {
        this.taskService = taskService;
        this.checklistService = checklistService;
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

        Button btnChecklist = new Button("📋 Чек-лист");
        btnChecklist.setOnAction(e -> handleChecklist());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setOnAction(e -> refreshTable());

        buttonPanel.getChildren().addAll(btnAdd, btnEdit, btnDelete, btnDone, btnAssign, btnChecklist, btnRefresh);
        root.setBottom(buttonPanel);
    }

    private void handleAdd() {
        boolean created = taskFormDialog.showNewTaskDialog();
        if (created) {
            refreshTable();
            new Alert(Alert.AlertType.INFORMATION, "Задача создана", ButtonType.OK).showAndWait();
        }
    }

    private void handleEdit() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите задачу для редактирования", ButtonType.OK).showAndWait();
            return;
        }

        boolean updated = taskFormDialog.showEditTaskDialog(selectedTask);
        if (updated) {
            refreshTable();
            new Alert(Alert.AlertType.INFORMATION, "Задача обновлена", ButtonType.OK).showAndWait();
        }
    }

    private void handleDelete() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите задачу для удаления", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить задачу #" + selectedTask.getId() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                taskService.deleteTask(selectedTask.getId());
                refreshTable();
                new Alert(Alert.AlertType.INFORMATION, "Задача удалена", ButtonType.OK).showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Ошибка удаления: " + e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }

    private void handleDone() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите задачу", ButtonType.OK).showAndWait();
            return;
        }

        try {
            taskService.markTaskDone(selectedTask.getId());
            refreshTable();
            new Alert(Alert.AlertType.INFORMATION, "Задача #" + selectedTask.getId() + " выполнена", ButtonType.OK).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void handleAssign() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите задачу", ButtonType.OK).showAndWait();
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
                new Alert(Alert.AlertType.INFORMATION, "Задача назначена на " + username, ButtonType.OK).showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Ошибка назначения: " + e.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void handleChecklist() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите задачу для управления чек-листом", ButtonType.OK).showAndWait();
            return;
        }

        ChecklistDialog dialog = new ChecklistDialog(selectedTask, checklistService);
        dialog.showDialog();
        refreshTable();
    }

    public void refreshTable() {
        try {
            taskList.clear();
            List<Task> freshData = taskService.getAllTasks();
            taskList.addAll(freshData);
            tableView.setItems(taskList);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка обновления: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    public BorderPane getRoot() {
        return root;
    }

}