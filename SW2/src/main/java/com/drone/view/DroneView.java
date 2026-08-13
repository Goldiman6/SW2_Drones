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
 * Interfaz grafica de usuario 
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

        // Form Area
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);

        serialField = new TextField();
        modeloField = new TextField();
        fabricanteField = new TextField();
        pesoField = new TextField();
        pilotoField = new TextField();

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
        getChildren().addAll(formPane, buttonBox, table);

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

    private void refreshTable() {
        droneData.setAll(controller.getAllDrones());
    }

    private void addDrone() {
        try {
            float peso = Float.parseFloat(pesoField.getText());
            controller.addDrone(
                    serialField.getText(),
                    modeloField.getText(),
                    fabricanteField.getText(),
                    peso,
                    pilotoField.getText()
            );
            refreshTable();
            clearForm();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Peso debe ser un número válido.");
        }
    }

    private void updateDrone() {
        if (selectedDrone != null) {
            try {
                float peso = Float.parseFloat(pesoField.getText());
                controller.updateDrone(
                        selectedDrone.getId(),
                        serialField.getText(),
                        modeloField.getText(),
                        fabricanteField.getText(),
                        peso,
                        pilotoField.getText()
                );
                refreshTable();
                clearForm();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Peso debe ser un número válido.");
            }
        }
    }

    private void deleteDrone() {
        if (selectedDrone != null) {
            controller.deleteDrone(selectedDrone.getId());
            refreshTable();
            clearForm();
        }
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
