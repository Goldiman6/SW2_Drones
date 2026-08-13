package com.drone;

import com.drone.controller.DroneController;
import com.drone.dao.DroneDAO;
import com.drone.dao.DroneDAOInMemoryImpl;
import com.drone.view.DroneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize DAO and Controller
        DroneDAO droneDAO = new DroneDAOInMemoryImpl();
        DroneController droneController = new DroneController(droneDAO);
        
        // Setup initial dummy data (optional but good for testing)
        droneController.addDrone("SRL-001", "Mavic 3", "DJI", 0.9f, "Juan Perez");

        // Initialize View
        DroneView root = new DroneView(droneController);

        Scene scene = new Scene(root, 600, 450);
        primaryStage.setTitle("Drone CRUD App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
