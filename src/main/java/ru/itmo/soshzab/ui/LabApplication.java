package ru.itmo.soshzab.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.itmo.soshzab.service.ChecklistService;
import ru.itmo.soshzab.service.TaskService;

public class LabApplication extends Application {

    private final TaskService taskService = new TaskService();
    private final ChecklistService checklistService = new ChecklistService();

    @Override
    public void start(Stage primaryStage) {
       MainView mainView = new MainView(taskService, checklistService);

        Scene scene = new Scene(mainView.getRoot(), 900, 600);
        primaryStage.setTitle("Лабораторные задачи (Task & Checklist)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
