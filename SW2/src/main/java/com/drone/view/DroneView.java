package com.drone.view;

import com.drone.controller.DroneController;
import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DroneView extends VBox {

    private final DroneController controller;
    private final TableView<Drone> table;
    private final ObservableList<Drone> droneData;

    private ComboBox<String> tipoCombo;
    private TextField serialField;
    private TextField modeloField;
    private TextField fabricanteField;
    private TextField pesoField;
    
    private Label dinamicoLabel;
    private TextField capacidadField;
    private CheckBox termicaCheck;

    private Button btnTestSingleton;
    private Button btnAddFactory;
    private Button btnAddBuilder;
    private Button btnClonePrototype;
    
    // CRUD Botones
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnClear;

    private Drone selectedDrone;

    public DroneView(DroneController controller) {
        this.controller = controller;
        this.droneData = FXCollections.observableArrayList(controller.getAllDrones());

        setPadding(new Insets(15));
        setSpacing(15);

        // Top Buttons (Singleton + Builder + Prototype)
        btnTestSingleton = new Button("Probar Conexin Singleton");
        btnTestSingleton.setOnAction(e -> testSingleton());

        btnAddBuilder = new Button("Crear con Builder (Solo Agricultura)");
        btnAddBuilder.setOnAction(e -> addViaBuilder());

        btnClonePrototype = new Button("Clonar con Prototype (Solo Vigilancia)");
        btnClonePrototype.setOnAction(e -> cloneViaPrototype());
        btnClonePrototype.setDisable(true); 

        HBox topBox = new HBox(10, btnTestSingleton, btnAddBuilder, btnClonePrototype);

        // Form Area
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);

        tipoCombo = new ComboBox<>(FXCollections.observableArrayList("Agricultura", "Vigilancia"));
        tipoCombo.setValue("Agricultura");
        
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
        termicaCheck = new CheckBox("Tiene deteccin trmica?");
        termicaCheck.setVisible(false);
        termicaCheck.setManaged(false);

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
        formPane.add(dinamicoLabel, 0, 5);
        HBox dynamicBox = new HBox(capacidadField, termicaCheck);
        formPane.add(dynamicBox, 1, 5);

        tipoCombo.setOnAction(e -> {
            if ("Agricultura".equals(tipoCombo.getValue())) {
                dinamicoLabel.setText("Capacidad Tanque:");
                capacidadField.setVisible(true);
                capacidadField.setManaged(true);
                termicaCheck.setVisible(false);
                termicaCheck.setManaged(false);
            } else {
                dinamicoLabel.setText("Deteccin Trmica:");
                capacidadField.setVisible(false);
                capacidadField.setManaged(false);
                termicaCheck.setVisible(true);
                termicaCheck.setManaged(true);
            }
        });

        // Botones principales (CRUD)
        btnAddFactory = new Button("Crear Dron (Factory)");
        btnAddFactory.setOnAction(e -> addViaFactory());
        
        btnUpdate = new Button("Actualizar Dron");
        btnUpdate.setOnAction(e -> updateDrone());
        btnUpdate.setDisable(true);
        
        btnDelete = new Button("Eliminar Dron");
        btnDelete.setOnAction(e -> deleteDrone());
        btnDelete.setDisable(true);
        
        btnClear = new Button("Limpiar Formulario");
        btnClear.setOnAction(e -> clearForm());

        HBox actionBox = new HBox(10, btnAddFactory, btnUpdate, btnDelete, btnClear);

        // Table
        table = new TableView<>();
        
        TableColumn<Drone, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Drone, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if (d instanceof Agricultura) return new SimpleStringProperty("Agricultura");
            if (d instanceof Vigilancia) return new SimpleStringProperty("Vigilancia");
            return new SimpleStringProperty("Desconocido");
        });

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
            if (d instanceof Agricultura) {
                return new SimpleStringProperty(((Agricultura) d).getCapacidadTanque() + " L");
            } else if (d instanceof Vigilancia) {
                return new SimpleStringProperty(((Vigilancia) d).isDeteccionTermica() ? "Trmica: S" : "Trmica: NO");
            }
            return new SimpleStringProperty("-");
        });

        table.getColumns().addAll(idCol, tipoCol, serialCol, modeloCol, pesoCol, extraCol);
        table.setItems(droneData);

        // Evento de seleccin en la tabla para rellenar formulario y habilitar Prototype/CRUD
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDrone = newSelection;
                
                // Rellenar campos comunes
                serialField.setText(selectedDrone.getSerial());
                modeloField.setText(selectedDrone.getModelo());
                fabricanteField.setText(selectedDrone.getFabricante());
                pesoField.setText(String.valueOf(selectedDrone.getPeso()));
                
                // Rellenar campos especficos y tipo
                if (selectedDrone instanceof Agricultura) {
                    tipoCombo.setValue("Agricultura");
                    capacidadField.setText(String.valueOf(((Agricultura) selectedDrone).getCapacidadTanque()));
                    btnClonePrototype.setDisable(true); // Prototype es solo vigilancia por requerimiento
                } else if (selectedDrone instanceof Vigilancia) {
                    tipoCombo.setValue("Vigilancia");
                    termicaCheck.setSelected(((Vigilancia) selectedDrone).isDeteccionTermica());
                    btnClonePrototype.setDisable(false); // Habilitar clonacin
                }
                
                // Modo EDICIN
                tipoCombo.setDisable(true); // No permitir cambiar el tipo al editar
                btnAddFactory.setDisable(true);
                btnAddBuilder.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                clearForm();
            }
        });

        getChildren().addAll(topBox, formPane, actionBox, table);
    }

    private void clearForm() {
        serialField.clear();
        modeloField.clear();
        fabricanteField.clear();
        pesoField.clear();
        capacidadField.clear();
        termicaCheck.setSelected(false);
        
        tipoCombo.setDisable(false);
        table.getSelectionModel().clearSelection();
        selectedDrone = null;
        
        btnAddFactory.setDisable(false);
        btnAddBuilder.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnClonePrototype.setDisable(true);
    }

    private void testSingleton() {
        String res = controller.testSingletonConnection();
        showAlert("Prueba de Singleton", res, Alert.AlertType.INFORMATION);
    }

    private boolean validarCamposFormulario() {
        if (serialField.getText().trim().isEmpty()) {
            showAlert("Validacin de Formulario", "1. Te falta llenar el campo 'Serial'.", Alert.AlertType.WARNING);
            serialField.requestFocus();
            return false;
        }
        if (modeloField.getText().trim().isEmpty()) {
            showAlert("Validacin de Formulario", "2. Te falta llenar el campo 'Modelo'.", Alert.AlertType.WARNING);
            modeloField.requestFocus();
            return false;
        }
        if (fabricanteField.getText().trim().isEmpty()) {
            showAlert("Validacin de Formulario", "3. Te falta llenar el campo 'Fabricante'.", Alert.AlertType.WARNING);
            fabricanteField.requestFocus();
            return false;
        }
        
        String pesoTxt = pesoField.getText().trim();
        if (pesoTxt.isEmpty()) {
            showAlert("Validacin de Formulario", "4. Te falta llenar el campo 'Peso'.", Alert.AlertType.WARNING);
            pesoField.requestFocus();
            return false;
        }
        try {
            double p = Double.parseDouble(pesoTxt);
            if (p <= 0) {
                showAlert("Validacin de Formulario", "4. El dato 'Peso' es invlido. Debe ser un nmero mayor a cero.", Alert.AlertType.WARNING);
                pesoField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validacin de Formulario", "4. El dato 'Peso' es incorrecto. Debe ser un nmero (ej: 2.5) y no contener letras.", Alert.AlertType.WARNING);
            pesoField.requestFocus();
            return false;
        }
        
        if ("Agricultura".equals(tipoCombo.getValue())) {
            String capTxt = capacidadField.getText().trim();
            if (capTxt.isEmpty()) {
                showAlert("Validacin de Formulario", "5. Al elegir Agricultura, la 'Capacidad del Tanque' es obligatoria.", Alert.AlertType.WARNING);
                capacidadField.requestFocus();
                return false;
            }
            try {
                double c = Double.parseDouble(capTxt);
                if (c <= 0) {
                    showAlert("Validacin de Formulario", "5. El dato 'Capacidad' es invlido. Debe ser mayor a cero.", Alert.AlertType.WARNING);
                    capacidadField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validacin de Formulario", "5. El dato 'Capacidad' es incorrecto. Debe ser un nmero numrico (ej: 15.0).", Alert.AlertType.WARNING);
                capacidadField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void addViaFactory() {
        if (!validarCamposFormulario()) {
            return;
        }
        
        try {
            String tipo = tipoCombo.getValue();
            String serial = serialField.getText().trim();
            String modelo = modeloField.getText().trim();
            String fabricante = fabricanteField.getText().trim();
            double peso = Double.parseDouble(pesoField.getText().trim());
            double cap = "Agricultura".equals(tipo) ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
            boolean termica = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

            Drone d = controller.addDroneFactory(tipo, serial, modelo, fabricante, peso, cap, termica);
            refreshTable();
            
            String fabricaConcreta = "Agricultura".equals(tipo) ? "CrearAgricultura" : "CrearVigilancia";
            
            showAlert("Factory Method", 
                      "El dron fue ensamblado delegando la creacin a la fbrica concreta:\n" +
                      "-> Clase [" + fabricaConcreta + ".java]\n\n" +
                      "El sistema reconoci automticamente que necesitabas un dron de " + tipo + 
                      " y utiliz el Factory correspondiente para instanciarlo y guardarlo.\n\n" +
                      "Espacio de memoria del nuevo Dron (hashCode): " + d.hashCode(), 
                      Alert.AlertType.INFORMATION);
            clearForm();
        } catch (Exception e) {
            showAlert("Error", "Error al crear el dron: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addViaBuilder() {
        try {
            // Un Builder real suele usarse para configurar objetos complejos paso a paso, 
            // a menudo sin que el usuario tenga que llenar cada detalle.
            // Para demostrar esto, ignoraremos los campos de texto y el Builder crear un
            // "Dron de Agricultura Preconfigurado de Alta Gama".
            
            String serialBuild = "BUILDER-" + (int)(Math.random() * 1000);
            Agricultura a = controller.addDroneBuilder(
                serialBuild, 
                "AgriPro Max", 
                "BuilderTech Corp", 
                25.5, 
                150.0
            );
            
            refreshTable();
            showAlert("Builder Pattern", 
                      "El Builder acaba de ensamblar y guardar un Dron Preconfigurado automticamente:\n\n" +
                      "- Modelo: AgriPro Max\n" +
                      "- Capacidad: 150 Litros\n\n" +
                      "As se demuestra que el Builder arma el objeto por partes complejas " +
                      "en segundo plano.\n" +
                      "Espacio de memoria (hashCode): " + a.hashCode(), 
                      Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Ocurri un problema en el Builder: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cloneViaPrototype() {
        if (selectedDrone instanceof Vigilancia) {
            try {
                Vigilancia original = (Vigilancia) selectedDrone;
                Vigilancia clon = controller.cloneDronePrototype(original);
                refreshTable();
                showAlert("Prototype Pattern", 
                          "Dron Vigilancia CLONADO exitosamente.\n" +
                          "Espacio memoria ORIGINAL: " + original.hashCode() + "\n" +
                          "Espacio memoria CLON: " + clon.hashCode() + "\n\n" +
                          "Al agregar sufijos aleatorios, puedes clonar este dron infinidad de veces sin errores de BD.", 
                          Alert.AlertType.INFORMATION);
                clearForm();
            } catch (Exception e) { 
                showAlert("Error", "Ocurri un problema al intentar clonar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void updateDrone() {
        if (selectedDrone != null) {
            if (!validarCamposFormulario()) {
                return;
            }
            try {
                // Guardamos el ID ANTES de refrescar la tabla, ya que el refresco 
                // limpia la seleccin y volvera a selectedDrone = null (causando el error)
                String idActual = selectedDrone.getId();
                
                String tipo = tipoCombo.getValue();
                String serial = serialField.getText().trim();
                String modelo = modeloField.getText().trim();
                String fabricante = fabricanteField.getText().trim();
                double peso = Double.parseDouble(pesoField.getText().trim());
                double cap = "Agricultura".equals(tipo) ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
                boolean termica = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

                controller.updateDrone(idActual, tipo, serial, modelo, fabricante, peso, cap, termica);
                refreshTable();
                showAlert("Dron Actualizado", "El dron con ID " + idActual + " fue actualizado exitosamente.", Alert.AlertType.INFORMATION);
                clearForm();
            } catch (Exception e) {
                showAlert("Error", "Error al actualizar dron: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void deleteDrone() {
        if (selectedDrone != null) {
            try {
                controller.deleteDrone(selectedDrone.getId());
                refreshTable();
                showAlert("Dron Eliminado", "El dron ha sido eliminado de la BD.", Alert.AlertType.INFORMATION);
                clearForm();
            } catch (Exception e) {
                showAlert("Error", "Error al eliminar dron: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void refreshTable() {
        droneData.setAll(controller.getAllDrones());
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
