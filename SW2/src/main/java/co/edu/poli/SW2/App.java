package co.edu.poli.SW2;

import com.drone.controller.DroneController;
import com.drone.view.DroneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        DroneController droneController = new DroneController();
        DroneView root = new DroneView(droneController);
        Scene scene = new Scene(root, 800, 600);
        
        stage.setTitle("Gestin de Drones - SW2");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}