package ru.itmo.soshzab.ui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ru.itmo.soshzab.domain.*;
import ru.itmo.soshzab.service.*;
import ru.itmo.soshzab.validation.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class ChecklistDialog {
    private final Task task;
    private final ChecklistService checklistService;

    private final ObservableList<ChecklistItem> itemList = FXCollections.observableArrayList();
    private final TableView<ChecklistItem> tableView = new TableView<>(itemList);

    public ChecklistDialog(Task task, ChecklistService checklistService) {
        this.task = task;
        this.checklistService = checklistService;
    }

    public void showDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Чек-лист задачи #" + task.getId());
        dialog.setHeaderText(task.getText());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        setupTableColumns();

        HBox buttonPanel = setupButtonPanel();

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(tableView, buttonPanel);

        dialog.getDialogPane().setContent(root);

        refreshList();

        dialog.showAndWait();
    }

    private void setupTableColumns() {
        TableColumn<ChecklistItem, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<ChecklistItem, String> colText = new TableColumn<>("Текст пункта");
        colText.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("text"));
        colText.setPrefWidth(300);

        TableColumn<ChecklistItem, String> colStatus = new TableColumn<>("Статус");
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().isDone() ? "✅ Выполнен" : " Не выполнен";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        colStatus.setPrefWidth(120);

        TableColumn<ChecklistItem, String> colDate = new TableColumn<>("Создан");
        colDate.setCellValueFactory(cellData -> {
            Instant created = cellData.getValue().getCreatedAt();
            String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .format(created.atZone(ZoneId.systemDefault()).toLocalDateTime());
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        colDate.setPrefWidth(150);

        tableView.getColumns().addAll(colId, colText, colStatus, colDate);
    }

    private HBox setupButtonPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(5, 0, 0, 0));

        Button btnAdd = new Button("➕ Добавить пункт");
        btnAdd.setOnAction(e -> handleAdd());

        Button btnToggle = new Button("🔄 Переключить статус");
        btnToggle.setOnAction(e -> handleToggle());

        Button btnRefresh = new Button("🔄 Обновить");
        btnRefresh.setOnAction(e -> refreshList());

        panel.getChildren().addAll(btnAdd, btnToggle, btnRefresh);
        return panel;
    }

    private void refreshList() {
        try {
            itemList.clear();
            List<ChecklistItem> freshData = checklistService.getItemsByTaskId(task.getId());
            itemList.addAll(freshData);
            tableView.setItems(itemList);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка загрузки чек-листа: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void handleAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить пункт");
        dialog.setHeaderText("Новый пункт для задачи #" + task.getId());
        dialog.setContentText("Текст пункта:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            try {
                String trimmed = text.trim();
                ChecklistItemValidator.validateText(trimmed);

                checklistService.addChecklistItem(task.getId(), trimmed);
                refreshList();
                new Alert(Alert.AlertType.INFORMATION, "Пункт добавлен", ButtonType.OK).showAndWait();
            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void handleToggle() {
        ChecklistItem selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите пункт для переключения", ButtonType.OK).showAndWait();
            return;
        }

        try {
            ChecklistItem updated = checklistService.toggleItemDone(selectedItem.getId());
            refreshList();
            String msg = updated.isDone() ? "Пункт отмечен как выполнен" : "Пункт отмечен как не выполнен";
            new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка переключения: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }
}
