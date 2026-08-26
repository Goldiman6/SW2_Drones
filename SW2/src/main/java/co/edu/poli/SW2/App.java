package co.edu.poli.SW2;

import com.drone.controller.DroneController;
import com.drone.servicios.DroneService;
import com.drone.view.DroneView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
* muestra una ventana de selección donde el usuario elige
 * qué arquitectura a usar
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        mostrarSelectorDeArquitectura(stage);
    }

    /**
     * Muestra una ventana inicial con botones para que el usuario
     * elija la arquitectura antes de abrir la aplicación principal.
     */
    private void mostrarSelectorDeArquitectura(Stage stage) {
        // Título
        Label titulo = new Label("Seleccione la Arquitectura principal a evaluar");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Botón 1: Estándar
        Button btnEstandar = new Button("1. Arquitectura DAO Estándar (Creación Directa)");
        btnEstandar.setStyle("-fx-font-size: 14px; -fx-pref-width: 380px; -fx-pref-height: 40px;");
        btnEstandar.setOnAction(e -> iniciarApp(stage, DroneService.Modo.ESTANDAR));

        // Botón 2: Singleton
        Button btnSingleton = new Button("2. Arquitectura Singleton (Creación Directa)");
        btnSingleton.setStyle("-fx-font-size: 14px; -fx-pref-width: 380px; -fx-pref-height: 40px; " +
                             "-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSingleton.setOnAction(e -> iniciarApp(stage, DroneService.Modo.SINGLETON));

        // Botón 3: Factory Method
        Button btnFactory = new Button("3. Arquitectura Factory Method");
        btnFactory.setStyle("-fx-font-size: 14px; -fx-pref-width: 380px; -fx-pref-height: 40px; " +
                             "-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnFactory.setOnAction(e -> iniciarApp(stage, DroneService.Modo.FACTORY));

        // Layout
        VBox layout = new VBox(20, titulo, btnEstandar, btnSingleton, btnFactory);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 450, 300);
        stage.setTitle("Gestión de Drones - Selección de Arquitectura");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Ensambla el patrón MVC con el modo seleccionado y abre la ventana principal.
     * Imprime en consola la demostración del patrón usado.
     */
    private void iniciarApp(Stage stage, DroneService.Modo modo) {
        System.out.println("\n========================================");
        System.out.println("  MODO SELECCIONADO: " + modo.name());
        System.out.println("========================================");

        // Demostración de la diferencia entre Estándar y Singleton
        if (modo == DroneService.Modo.SINGLETON) {
            System.out.println("\n--- Demostración Singleton ---");
            System.out.println("Solicitando instancia 1...");
            DroneService servicio1 = new DroneService(modo);
            System.out.println("Solicitando instancia 2...");
            DroneService servicio2 = new DroneService(modo);
            System.out.println("¿Son la misma instancia del DAO? Ambos servicios usan el MISMO DAO interno.");
            System.out.println("--- Fin demostración ---\n");
        } else if (modo == DroneService.Modo.ESTANDAR) {
            System.out.println("\n--- Demostración Estándar ---");
            System.out.println("Creando instancia 1...");
            DroneService servicio1 = new DroneService(modo);
            System.out.println("Creando instancia 2...");
            DroneService servicio2 = new DroneService(modo);
            System.out.println("Cada servicio creó su PROPIO DAO con hashCode diferente.");
            System.out.println("--- Fin demostración ---\n");
        } else if (modo == DroneService.Modo.FACTORY) {
            System.out.println("\n--- Demostración Factory Method ---");
            System.out.println("La persistencia será Singleton.");
            System.out.println("La creación de los Drones será manejada por DroneFactory.");
            System.out.println("--- Fin demostración ---\n");
        }

        // Ensamblar MVC con el modo elegido
        DroneService droneService = new DroneService(modo);
        DroneController droneController = new DroneController(droneService);

        DroneView root = new DroneView(droneController);
        Scene scene = new Scene(root, 700, 500);
        
        stage.setTitle("Gestión de Drones - SW2 [" + modo.name() + "]");
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}