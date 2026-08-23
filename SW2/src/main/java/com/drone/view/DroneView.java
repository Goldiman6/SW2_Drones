package com.drone.view;

import com.drone.controller.DroneController;
import com.drone.model.Drone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * LA VISTA (V en MVC).
 * Dibuja los botones, la tabla y los campos de texto en pantalla.
 * Incluye validaciones de campos y ventanas emergentes demostrativas.
 */
public class DroneView extends VBox {

    private final DroneController controller;
    private final TableView<Drone> table;
    private final ObservableList<Drone> droneData;

    private TextField serialField;
    private TextField modeloField;
    private TextField fabricanteField;
    private TextField pesoField;
    private TextField pilotoField;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button clearButton;

    private Drone selectedDrone;

    public DroneView(DroneController controller) {
        this.controller = controller;
        this.droneData = FXCollections.observableArrayList(controller.getAllDrones());

        setPadding(new Insets(15));
        setSpacing(15);

        // Indicador del modo activo
        String modoTexto = controller.getModo().name();
        Label modoLabel = new Label("Arquitectura activa: " + modoTexto);
        modoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        // Form Area
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);

        serialField = new TextField();
        serialField.setPromptText("Ej: SRL-001");
        modeloField = new TextField();
        modeloField.setPromptText("Ej: Mavic 3");
        fabricanteField = new TextField();
        fabricanteField.setPromptText("Ej: DJI");
        pesoField = new TextField();
        pesoField.setPromptText("Ej: 0.9");
        pilotoField = new TextField();
        pilotoField.setPromptText("Ej: Juan Perez");

        formPane.add(new Label("Serial:"), 0, 0);
        formPane.add(serialField, 1, 0);
        formPane.add(new Label("Modelo:"), 0, 1);
        formPane.add(modeloField, 1, 1);
        formPane.add(new Label("Fabricante:"), 0, 2);
        formPane.add(fabricanteField, 1, 2);
        formPane.add(new Label("Peso:"), 0, 3);
        formPane.add(pesoField, 1, 3);
        formPane.add(new Label("Piloto:"), 0, 4);
        formPane.add(pilotoField, 1, 4);

        // Buttons
        addButton = new Button("Añadir");
        updateButton = new Button("Actualizar");
        deleteButton = new Button("Eliminar");
        clearButton = new Button("Limpiar");

        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, clearButton);

        // Table
        table = new TableView<>();
        
        TableColumn<Drone, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Drone, String> serialCol = new TableColumn<>("Serial");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serial"));

        TableColumn<Drone, String> modeloCol = new TableColumn<>("Modelo");
        modeloCol.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        TableColumn<Drone, String> fabricanteCol = new TableColumn<>("Fabricante");
        fabricanteCol.setCellValueFactory(new PropertyValueFactory<>("fabricante"));

        TableColumn<Drone, Float> pesoCol = new TableColumn<>("Peso");
        pesoCol.setCellValueFactory(new PropertyValueFactory<>("peso"));

        TableColumn<Drone, String> pilotoCol = new TableColumn<>("Piloto");
        pilotoCol.setCellValueFactory(new PropertyValueFactory<>("piloto"));

        table.getColumns().addAll(idCol, serialCol, modeloCol, fabricanteCol, pesoCol, pilotoCol);
        table.setItems(droneData);

        // Add Components to VBox
        getChildren().addAll(modoLabel, formPane, buttonBox, table);

        // Event Handlers
        addButton.setOnAction(e -> addDrone());
        updateButton.setOnAction(e -> updateDrone());
        deleteButton.setOnAction(e -> deleteDrone());
        clearButton.setOnAction(e -> clearForm());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDrone = newSelection;
                serialField.setText(selectedDrone.getSerial());
                modeloField.setText(selectedDrone.getModelo());
                fabricanteField.setText(selectedDrone.getFabricante());
                pesoField.setText(String.valueOf(selectedDrone.getPeso()));
                pilotoField.setText(selectedDrone.getPiloto());

                addButton.setDisable(true);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    // ==================== VALIDACIÓN DE CAMPOS ====================

    /**
     * Valida que todos los campos del formulario estén completos.
     * Si alguno está vacío, muestra un cuadro de error específico
     * indicando cuál es el campo faltante.
     * @return true si todos los campos son válidos, false si hay algún error.
     */
    private boolean validarCampos() {
        if (serialField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Serial' es obligatorio.\nPor favor ingrese el número de serie del dron.");
            serialField.requestFocus();
            return false;
        }
        if (modeloField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Modelo' es obligatorio.\nPor favor ingrese el modelo del dron.");
            modeloField.requestFocus();
            return false;
        }
        if (fabricanteField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Fabricante' es obligatorio.\nPor favor ingrese el fabricante del dron.");
            fabricanteField.requestFocus();
            return false;
        }
        if (pesoField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Peso' es obligatorio.\nPor favor ingrese el peso del dron en kg.");
            pesoField.requestFocus();
            return false;
        }
        // Validar que el peso sea un número válido
        try {
            float peso = Float.parseFloat(pesoField.getText().trim());
            if (peso <= 0) {
                showAlert("Valor Inválido", "El campo 'Peso' debe ser un número mayor a 0.");
                pesoField.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            showAlert("Valor Inválido", "El campo 'Peso' debe ser un número válido.\nEjemplo: 0.9 o 2.5");
            pesoField.requestFocus();
            return false;
        }
        if (pilotoField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Piloto' es obligatorio.\nPor favor ingrese el nombre del piloto asignado.");
            pilotoField.requestFocus();
            return false;
        }
        return true;
    }

    // ==================== OPERACIONES CRUD ====================

    private void addDrone() {
        // Primero validar que todos los campos estén completos
        if (!validarCampos()) {
            return;
        }

        float peso = Float.parseFloat(pesoField.getText().trim());
        controller.addDrone(
                serialField.getText().trim(),
                modeloField.getText().trim(),
                fabricanteField.getText().trim(),
                peso,
                pilotoField.getText().trim()
        );
        refreshTable();

        // Mostrar ventana de éxito con demostración de arquitectura
        showSuccessWithDemo(
                serialField.getText().trim(),
                modeloField.getText().trim(),
                fabricanteField.getText().trim(),
                peso,
                pilotoField.getText().trim()
        );

        clearForm();
    }

    private void updateDrone() {
        if (selectedDrone != null) {
            // Validar campos antes de actualizar
            if (!validarCampos()) {
                return;
            }

            float peso = Float.parseFloat(pesoField.getText().trim());
            controller.updateDrone(
                    selectedDrone.getId(),
                    serialField.getText().trim(),
                    modeloField.getText().trim(),
                    fabricanteField.getText().trim(),
                    peso,
                    pilotoField.getText().trim()
            );
            refreshTable();
            showInfo("Dron Actualizado", "El dron con ID " + selectedDrone.getId() + " fue actualizado exitosamente.");
            clearForm();
        }
    }

    private void deleteDrone() {
        if (selectedDrone != null) {
            // Confirmación antes de eliminar
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar este dron?");
            confirmacion.setContentText("ID: " + selectedDrone.getId() + 
                    "\nSerial: " + selectedDrone.getSerial() +
                    "\nModelo: " + selectedDrone.getModelo());

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    controller.deleteDrone(selectedDrone.getId());
                    refreshTable();
                    showInfo("Dron Eliminado", "El dron fue eliminado exitosamente.");
                    clearForm();
                }
            });
        }
    }

    private void refreshTable() {
        droneData.setAll(controller.getAllDrones());
    }

    private void clearForm() {
        serialField.clear();
        modeloField.clear();
        fabricanteField.clear();
        pesoField.clear();
        pilotoField.clear();
        table.getSelectionModel().clearSelection();
        selectedDrone = null;
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // ==================== VENTANAS EMERGENTES ====================

    /**
     * Muestra una ventana emergente de ÉXITO al crear un dron.
     * Incluye los datos del dron creado y la demostración de hashCodes
     * para evidenciar la diferencia entre Estándar y Singleton.
     */
    private void showSuccessWithDemo(String serial, String modelo, String fabricante, float peso, String piloto) {
        String demoHashCode = controller.generarDemoHashCode();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dron Creado Exitosamente");
        alert.setHeaderText("El dron fue registrado correctamente");

        String contenido = "═══ Datos del Dron ═══\n" +
                "Serial: " + serial + "\n" +
                "Modelo: " + modelo + "\n" +
                "Fabricante: " + fabricante + "\n" +
                "Peso: " + peso + " kg\n" +
                "Piloto: " + piloto + "\n\n" +
                "═══ Demostración de Arquitectura ═══\n\n" +
                demoHashCode;

        // Usar TextArea para mostrar contenido extenso
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(350);
        textArea.setPrefWidth(420);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(480);
        alert.showAndWait();
    }

    /** Muestra un cuadro de error con el mensaje dado */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Muestra un cuadro informativo */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
