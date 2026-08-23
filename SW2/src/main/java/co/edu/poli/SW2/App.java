package co.edu.poli.SW2;

import com.drone.controller.DroneController;
import com.drone.servicios.DroneService;
import com.drone.view.DroneView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

/**
 * PUNTO DE ENTRADA DE LA APLICACIÓN.
 * Al iniciar, muestra una ventana de selección donde el usuario elige
 * qué arquitectura de DAO usar: Estándar o Singleton.
 * Luego ensambla el patrón MVC con el modo elegido.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        mostrarSelectorDeArquitectura(stage);
    }

    /**
     * Muestra una ventana inicial con dos botones para que el usuario
     * elija la arquitectura antes de abrir la aplicación principal.
     */
    private void mostrarSelectorDeArquitectura(Stage stage) {
        // Título
        Label titulo = new Label("Seleccione la Arquitectura");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Descripción Estándar
        Label descEstandar = new Label(
            "DAO Estándar:\n" +
            "Crea una instancia NUEVA cada vez.\n" +
            "Cada instancia es independiente."
        );
        descEstandar.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // Botón Estándar
        Button btnEstandar = new Button("Iniciar con DAO Estándar");
        btnEstandar.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px; -fx-pref-height: 40px;");
        btnEstandar.setOnAction(e -> iniciarApp(stage, DroneService.Modo.ESTANDAR));

        // Separador
        Separator separador = new Separator();

        // Descripción Singleton
        Label descSingleton = new Label(
            "DAO Singleton:\n" +
            "Usa una ÚNICA instancia compartida.\n" +
            "Garantiza consistencia de datos."
        );
        descSingleton.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // Botón Singleton
        Button btnSingleton = new Button("Iniciar con DAO Singleton");
        btnSingleton.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px; -fx-pref-height: 40px; " +
                             "-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnSingleton.setOnAction(e -> iniciarApp(stage, DroneService.Modo.SINGLETON));

        // Layout
        VBox layout = new VBox(15, titulo, descEstandar, btnEstandar, separador, descSingleton, btnSingleton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 380);
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
        } else {
            System.out.println("\n--- Demostración Estándar ---");
            System.out.println("Creando instancia 1...");
            DroneService servicio1 = new DroneService(modo);
            System.out.println("Creando instancia 2...");
            DroneService servicio2 = new DroneService(modo);
            System.out.println("Cada servicio creó su PROPIO DAO con hashCode diferente.");
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