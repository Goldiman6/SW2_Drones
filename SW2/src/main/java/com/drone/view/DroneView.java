package com.drone.view;

import com.drone.controller.DroneController;
import com.drone.model.Drone;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Incorpora un selector de Tipo de Dron (Agricultura/Vigilancia)
 * y muestra/oculta campos dinámicamente según el tipo elegido.
 */
public class DroneView extends VBox {

    private final DroneController controller;
    private final TableView<Drone> table;
    private final ObservableList<Drone> droneData;

    private ComboBox<String> tipoCombo;
    private TextField serialField;
    private TextField modeloField;
    private TextField fabricanteField;
    private TextField pesoField;
    
    // Campos dinámicos según el tipo de dron
    private Label dinamicoLabel;
    private TextField capacidadField; // Para Agricultura
    private CheckBox termicaCheck;    // Para Vigilancia

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
        Label modoLabel = new Label("Arquitectura de Persistencia activa: " + modoTexto + " | Creación: Factory Method");
        modoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        // Form Area
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);

        tipoCombo = new ComboBox<>(FXCollections.observableArrayList("Agricultura", "Vigilancia"));
        tipoCombo.setValue("Agricultura"); // Valor por defecto
        
        serialField = new TextField();
        serialField.setPromptText("Ej: SRL-001");
        modeloField = new TextField();
        modeloField.setPromptText("Ej: Mavic 3");
        fabricanteField = new TextField();
        fabricanteField.setPromptText("Ej: DJI");
        pesoField = new TextField();
        pesoField.setPromptText("Ej: 0.9");
        
        dinamicoLabel = new Label("Capacidad Tanque:");
        capacidadField = new TextField();
        capacidadField.setPromptText("Ej: 15.5 (Litros)");
        termicaCheck = new CheckBox("¿Tiene detección térmica?");
        termicaCheck.setVisible(false); // Oculto por defecto

        formPane.add(new Label("Tipo de Dron:"), 0, 0);
        formPane.add(tipoCombo, 1, 0);
        formPane.add(new Label("Serial:"), 0, 1);
        formPane.add(serialField, 1, 1);
        formPane.add(new Label("Modelo:"), 0, 2);
        formPane.add(modeloField, 1, 2);
        formPane.add(new Label("Fabricante:"), 0, 3);
        formPane.add(fabricanteField, 1, 3);
        formPane.add(new Label("Peso (kg):"), 0, 4);
        formPane.add(pesoField, 1, 4);
        
        // Fila 5 es dinámica
        formPane.add(dinamicoLabel, 0, 5);
        // Ponemos ambos controles en la misma celda, pero solo uno será visible a la vez
        HBox dynamicBox = new HBox(capacidadField, termicaCheck);
        formPane.add(dynamicBox, 1, 5);

        // Lógica para alternar los campos dinámicos cuando cambia el tipo
        tipoCombo.setOnAction(e -> {
            if ("Agricultura".equals(tipoCombo.getValue())) {
                dinamicoLabel.setText("Capacidad Tanque:");
                capacidadField.setVisible(true);
                capacidadField.setManaged(true);
                termicaCheck.setVisible(false);
                termicaCheck.setManaged(false);
            } else {
                dinamicoLabel.setText("Cámara Térmica:");
                capacidadField.setVisible(false);
                capacidadField.setManaged(false);
                termicaCheck.setVisible(true);
                termicaCheck.setManaged(true);
            }
        });

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
        
        TableColumn<Drone, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Drone, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Drone, String> serialCol = new TableColumn<>("Serial");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serial"));

        TableColumn<Drone, String> modeloCol = new TableColumn<>("Modelo");
        modeloCol.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        TableColumn<Drone, Double> pesoCol = new TableColumn<>("Peso");
        pesoCol.setCellValueFactory(new PropertyValueFactory<>("peso"));

        TableColumn<Drone, String> extraCol = new TableColumn<>("Atributo Especial");
        extraCol.setPrefWidth(150);
        extraCol.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if ("Agricultura".equals(d.getTipo()) && d.getCapacidadTanque() != null) {
                return new SimpleStringProperty(d.getCapacidadTanque() + " L");
            }
            if ("Vigilancia".equals(d.getTipo()) && d.getDeteccionTermica() != null) {
                return new SimpleStringProperty(d.getDeteccionTermica() ? "Térmica: SÍ" : "Térmica: NO");
            }
            return new SimpleStringProperty("-");
        });

        table.getColumns().addAll(idCol, tipoCol, serialCol, modeloCol, pesoCol, extraCol);
        table.setItems(droneData);

        // Add Components
        getChildren().addAll(modoLabel, formPane, buttonBox, table);

        // Event
        addButton.setOnAction(e -> addDrone());
        updateButton.setOnAction(e -> updateDrone());
        deleteButton.setOnAction(e -> deleteDrone());
        clearButton.setOnAction(e -> clearForm());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDrone = newSelection;
                
                if ("Agricultura".equals(selectedDrone.getTipo())) {
                    tipoCombo.setValue("Agricultura");
                    capacidadField.setText(String.valueOf(selectedDrone.getCapacidadTanque()));
                } else if ("Vigilancia".equals(selectedDrone.getTipo())) {
                    tipoCombo.setValue("Vigilancia");
                    termicaCheck.setSelected(selectedDrone.getDeteccionTermica());
                }

                serialField.setText(selectedDrone.getSerial());
                modeloField.setText(selectedDrone.getModelo());
                fabricanteField.setText(selectedDrone.getFabricante());
                pesoField.setText(String.valueOf(selectedDrone.getPeso()));

                // Deshabilitar tipoCombo durante actualización para evitar incongruencias
                tipoCombo.setDisable(true);
                addButton.setDisable(true);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private boolean validarCampos() {
        if (serialField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Serial' es obligatorio.");
            return false;
        }
        if (modeloField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Modelo' es obligatorio.");
            return false;
        }
        if (fabricanteField.getText().trim().isEmpty()) {
            showAlert("Campo Requerido", "El campo 'Fabricante' es obligatorio.");
            return false;
        }
        try {
            double peso = Double.parseDouble(pesoField.getText().trim());
            if (peso <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert("Valor Inválido", "El campo 'Peso' debe ser un número mayor a 0.");
            return false;
        }
        
        if ("Agricultura".equals(tipoCombo.getValue())) {
            try {
                double cap = Double.parseDouble(capacidadField.getText().trim());
                if (cap <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Valor Inválido", "El campo 'Capacidad Tanque' debe ser un número mayor a 0.");
                return false;
            }
        }
        return true;
    }

    private void addDrone() {
        if (!validarCampos()) return;

        double peso = Double.parseDouble(pesoField.getText().trim());
        String tipo = tipoCombo.getValue();
        double capacidad = "Agricultura".equals(tipo) ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
        boolean termica = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

        controller.addDrone(
                tipo,
                serialField.getText().trim(),
                modeloField.getText().trim(),
                fabricanteField.getText().trim(),
                peso,
                capacidad,
                termica
        );
        refreshTable();
        showSuccessWithDemo(tipo);
        clearForm();
    }

    private void updateDrone() {
        if (selectedDrone != null) {
            if (!validarCampos()) return;

            double peso = Double.parseDouble(pesoField.getText().trim());
            String tipo = tipoCombo.getValue();
            double capacidad = "Agricultura".equals(tipo) ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
            boolean termica = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

            controller.updateDrone(
                    selectedDrone.getId(),
                    tipo,
                    serialField.getText().trim(),
                    modeloField.getText().trim(),
                    fabricanteField.getText().trim(),
                    peso,
                    capacidad,
                    termica
            );
            refreshTable();
            showInfo("Dron Actualizado", "El dron con ID " + selectedDrone.getId() + " fue actualizado exitosamente.");
            clearForm();
        }
    }

    private void deleteDrone() {
        if (selectedDrone != null) {
            controller.deleteDrone(selectedDrone.getId());
            refreshTable();
            showInfo("Dron Eliminado", "El dron fue eliminado exitosamente.");
            clearForm();
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
        capacidadField.clear();
        termicaCheck.setSelected(false);
        
        tipoCombo.setDisable(false); // Rehabilitar selección
        table.getSelectionModel().clearSelection();
        selectedDrone = null;
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSuccessWithDemo(String tipoDron) {
        String demoHashCode = controller.generarDemoHashCode(tipoDron);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dron Creado Exitosamente");
        alert.setHeaderText("El dron fue registrado y guardado.");

        TextArea textArea = new TextArea(demoHashCode);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(150);
        textArea.setPrefWidth(400);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
