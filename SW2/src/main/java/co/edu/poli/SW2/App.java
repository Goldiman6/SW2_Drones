package co.edu.poli.SW2;

import com.drone.controller.DroneController;
import com.drone.dao.DroneDAO;
import com.drone.view.DroneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * PUNTO DE ENTRADA DE LA APLICACIÓN.
 * Aquí es donde se ensambla el patrón MVC (Modelo-Vista-Controlador).
 * Se instancian las piezas por separado y se conectan entre sí para 
 * que la aplicación pueda arrancar.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        DroneDAO droneDAO = new DroneDAO();
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