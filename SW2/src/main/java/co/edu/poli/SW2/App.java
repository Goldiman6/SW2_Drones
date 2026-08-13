package co.edu.poli.SW2;

import com.drone.controller.DroneController;
import com.drone.dao.DroneDAO;
import com.drone.dao.DroneDAOFileImpl;
import com.drone.view.DroneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
Ensamble MVC
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        DroneDAO droneDAO = new DroneDAOFileImpl();
        DroneController droneController = new DroneController(droneDAO);


        DroneView root = new DroneView(droneController);
        Scene scene = new Scene(root, 700, 500);
        
        stage.setTitle("Gestión de Drones - SW2");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}