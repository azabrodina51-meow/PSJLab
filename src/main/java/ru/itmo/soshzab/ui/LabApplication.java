package ru.itmo.soshzab.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;
import ru.itmo.soshzab.storage.*;

public class LabApplication extends Application {

    private final TaskService taskService = new TaskService();
    private final ChecklistService checklistService = new ChecklistService();
    private final SerializationStorage storage = new SerializationStorage();

    @Override
    public void start(Stage primaryStage) {
       MainView mainView = new MainView(taskService, checklistService, storage);

        Scene scene = new Scene(mainView.getRoot(), 1100, 600);
        primaryStage.setTitle("Лабораторные задачи (Task & Checklist)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
