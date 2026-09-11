package com.drone.view;

import com.drone.controller.DroneController;
import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.ControlAutonomo;
import com.drone.servicios.ControlBasico;
import com.drone.servicios.TipoControl;
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
 * Vista principal de la aplicacion de Drones (Patron MVC - Vista).
 *
 * Contiene la interfaz grafica JavaFX con:
 *  - Formulario para crear, actualizar y eliminar drones (CRUD).
 *  - Botones para demostrar los patrones Factory, Builder, Prototype y Singleton.
 *  - RadioButtons opcionales para seleccionar el Tipo de Control (Patron Bridge):
 *    Control Basico o Control Autonomo.
 */
public class DroneView extends VBox {

    private final DroneController controller;
    private final TableView<Drone> table;
    private final ObservableList<Drone> droneData;

    // Campos del formulario
    private ComboBox<String> tipoCombo;
    private TextField serialField;
    private TextField modeloField;
    private TextField fabricanteField;
    private TextField pesoField;
    private Label dinamicoLabel;
    private TextField capacidadField;
    private CheckBox termicaCheck;

    // Botones de patrones creacionales y utilidades
    private Button btnTestSingleton;
    private Button btnAddFactory;
    private Button btnAddBuilder;
    private Button btnClonePrototype;

    // Botones CRUD
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnClear;

    // --- PATRON BRIDGE: RadioButtons de Tipo de Control (OPCIONALES) ---
    private ToggleGroup controlGroup;
    private RadioButton rbBasico;
    private RadioButton rbAutonomo;
    private Button btnLimpiarControl;

    // Dron actualmente seleccionado en la tabla (modo edicion)
    private Drone selectedDrone;

    public DroneView(DroneController controller) {
        this.controller = controller;
        this.droneData = FXCollections.observableArrayList(controller.getAllDrones());

        setPadding(new Insets(15));
        setSpacing(15);

        // ----------------------------------------------------------------
        // SECCION 1: Botones superiores (Singleton, Builder, Prototype)
        // ----------------------------------------------------------------
        btnTestSingleton = new Button("Probar Conexion Singleton");
        btnTestSingleton.setOnAction(e -> testSingleton());

        btnAddBuilder = new Button("Crear con Builder (Solo Agricultura)");
        btnAddBuilder.setOnAction(e -> addViaBuilder());

        btnClonePrototype = new Button("Clonar con Prototype (Solo Vigilancia)");
        btnClonePrototype.setOnAction(e -> cloneViaPrototype());
        btnClonePrototype.setDisable(true);

        HBox topBox = new HBox(10, btnTestSingleton, btnAddBuilder, btnClonePrototype);

        // ----------------------------------------------------------------
        // SECCION 2: Formulario de datos del dron
        // ----------------------------------------------------------------
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);

        tipoCombo = new ComboBox<>(FXCollections.observableArrayList("Agricultura", "Vigilancia"));
        tipoCombo.setValue("Agricultura");

        serialField     = new TextField(); serialField.setPromptText("Ej: SRL-001");
        modeloField     = new TextField(); modeloField.setPromptText("Ej: Mavic 3");
        fabricanteField = new TextField(); fabricanteField.setPromptText("Ej: DJI");
        pesoField       = new TextField(); pesoField.setPromptText("Ej: 0.9");

        dinamicoLabel  = new Label("Capacidad Tanque:");
        capacidadField = new TextField(); capacidadField.setPromptText("Ej: 15.5 (Litros)");
        termicaCheck   = new CheckBox("Tiene deteccion termica?");
        termicaCheck.setVisible(false);
        termicaCheck.setManaged(false);

        formPane.add(new Label("Tipo de Dron:"),  0, 0); formPane.add(tipoCombo, 1, 0);
        formPane.add(new Label("Serial:"),        0, 1); formPane.add(serialField, 1, 1);
        formPane.add(new Label("Modelo:"),        0, 2); formPane.add(modeloField, 1, 2);
        formPane.add(new Label("Fabricante:"),    0, 3); formPane.add(fabricanteField, 1, 3);
        formPane.add(new Label("Peso (kg):"),     0, 4); formPane.add(pesoField, 1, 4);
        formPane.add(dinamicoLabel,               0, 5);
        HBox dynamicBox = new HBox(capacidadField, termicaCheck);
        formPane.add(dynamicBox, 1, 5);

        tipoCombo.setOnAction(e -> {
            boolean esAgr = "Agricultura".equals(tipoCombo.getValue());
            dinamicoLabel.setText(esAgr ? "Capacidad Tanque:" : "Deteccion Termica:");
            capacidadField.setVisible(esAgr);
            capacidadField.setManaged(esAgr);
            termicaCheck.setVisible(!esAgr);
            termicaCheck.setManaged(!esAgr);
        });

        // ----------------------------------------------------------------
        // SECCION 3: Patron Bridge - Selector de Tipo de Control (OPCIONAL)
        // ----------------------------------------------------------------
        controlGroup = new ToggleGroup();
        rbBasico    = new RadioButton("Control Basico");
        rbAutonomo  = new RadioButton("Control Autonomo");
        rbBasico.setToggleGroup(controlGroup);
        rbAutonomo.setToggleGroup(controlGroup);

        btnLimpiarControl = new Button("Sin control");
        btnLimpiarControl.setOnAction(e -> controlGroup.selectToggle(null));

        Label lblControl = new Label("Tipo de Control (opcional - Bridge):");
        HBox bridgeBox = new HBox(12, lblControl, rbBasico, rbAutonomo, btnLimpiarControl);
        bridgeBox.setPadding(new Insets(5, 0, 5, 0));

        // ----------------------------------------------------------------
        // SECCION 4: Botones de accion CRUD
        // ----------------------------------------------------------------
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

        // ----------------------------------------------------------------
        // SECCION 5: Tabla de drones
        // ----------------------------------------------------------------
        table = new TableView<>();

        TableColumn<Drone, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Drone, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if (d instanceof Agricultura) return new SimpleStringProperty("Agricultura");
            if (d instanceof Vigilancia)  return new SimpleStringProperty("Vigilancia");
            return new SimpleStringProperty("Desconocido");
        });

        TableColumn<Drone, String> serialCol = new TableColumn<>("Serial");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serial"));

        TableColumn<Drone, String> modeloCol = new TableColumn<>("Modelo");
        modeloCol.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        TableColumn<Drone, String> fabricanteCol = new TableColumn<>("Fabricante");
        fabricanteCol.setCellValueFactory(new PropertyValueFactory<>("fabricante"));

        TableColumn<Drone, Double> pesoCol = new TableColumn<>("Peso (kg)");
        pesoCol.setCellValueFactory(new PropertyValueFactory<>("peso"));

        TableColumn<Drone, String> extraCol = new TableColumn<>("Atributo Especial");
        extraCol.setPrefWidth(160);
        extraCol.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if (d instanceof Agricultura)
                return new SimpleStringProperty(((Agricultura) d).getCapacidadTanque() + " L");
            if (d instanceof Vigilancia)
                return new SimpleStringProperty(
                        ((Vigilancia) d).isDeteccionTermica() ? "Termica: SI" : "Termica: NO");
            return new SimpleStringProperty("-");
        });

        table.getColumns().addAll(idCol, tipoCol, serialCol, modeloCol, fabricanteCol, pesoCol, extraCol);
        table.setItems(droneData);

        // Escuchador de seleccion: rellena el formulario y activa botones CRUD/Prototype
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                selectedDrone = newVal;
                serialField.setText(selectedDrone.getSerial());
                modeloField.setText(selectedDrone.getModelo());
                fabricanteField.setText(selectedDrone.getFabricante());
                pesoField.setText(String.valueOf(selectedDrone.getPeso()));

                if (selectedDrone instanceof Agricultura) {
                    tipoCombo.setValue("Agricultura");
                    capacidadField.setText(
                            String.valueOf(((Agricultura) selectedDrone).getCapacidadTanque()));
                    btnClonePrototype.setDisable(true);
                } else if (selectedDrone instanceof Vigilancia) {
                    tipoCombo.setValue("Vigilancia");
                    termicaCheck.setSelected(((Vigilancia) selectedDrone).isDeteccionTermica());
                    btnClonePrototype.setDisable(false);
                }

                tipoCombo.setDisable(true);
                btnAddFactory.setDisable(true);
                btnAddBuilder.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                clearForm();
            }
        });

        // Agregar todos los nodos al VBox principal
        getChildren().addAll(topBox, formPane, bridgeBox, actionBox, table);
    }

    // ----------------------------------------------------------------
    // Metodo utilitario: resuelve el TipoControl segun RadioButton activo
    // Retorna null si ningun radio esta seleccionado (control es opcional)
    // ----------------------------------------------------------------
    private TipoControl resolverControl() {
        if (rbBasico.isSelected())   return new ControlBasico();
        if (rbAutonomo.isSelected()) return new ControlAutonomo();
        return null;
    }

    /**
     * Construye el bloque de texto del Patron Bridge con la RUTA DE CREACION completa.
     * Muestra: patron usado -> clase concreta -> interfaz Bridge -> implementacion -> dron destino.
     * Si no hay control seleccionado, retorna cadena vacia (el campo es opcional).
     *
     * @param dron         El dron recien creado o clonado.
     * @param patronOrigen Nombre del patron que creo el dron (ej: "Factory Method").
     * @param claseOrigen  Nombre de la clase concreta usada (ej: "CrearAgricultura").
     * @return Texto enriquecido con la ruta de creacion y la descripcion del control.
     */
    private String textoControl(Drone dron, String patronOrigen, String claseOrigen) {
        TipoControl control = resolverControl();
        if (control == null) return "";

        String tipoControlNombre = (control instanceof ControlBasico) ? "ControlBasico" : "ControlAutonomo";
        String descripcion = control.configurar(dron);

        return "\n\n========== PATRON BRIDGE ==========\n" +
               "Ruta de creacion:\n" +
               "  [" + patronOrigen + "] -> " + claseOrigen + "\n" +
               "        |\n" +
               "        v\n" +
               "  <<interface>> TipoControl\n" +
               "        |\n" +
               "        v\n" +
               "  " + tipoControlNombre + ".configurar(Drone)\n" +
               "        |\n" +
               "        v\n" +
               "  Dron: " + dron.getModelo() + " [ID: " + dron.getId() + "]\n" +
               descripcion;
    }

    // ----------------------------------------------------------------
    // Limpia el formulario y resetea el estado de la UI
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // Validacion de campos obligatorios del formulario
    // ----------------------------------------------------------------
    private boolean validarCamposFormulario() {
        if (serialField.getText().trim().isEmpty()) {
            showAlert("Validacion", "Falta llenar el campo 'Serial'.", Alert.AlertType.WARNING);
            serialField.requestFocus(); return false;
        }
        if (modeloField.getText().trim().isEmpty()) {
            showAlert("Validacion", "Falta llenar el campo 'Modelo'.", Alert.AlertType.WARNING);
            modeloField.requestFocus(); return false;
        }
        if (fabricanteField.getText().trim().isEmpty()) {
            showAlert("Validacion", "Falta llenar el campo 'Fabricante'.", Alert.AlertType.WARNING);
            fabricanteField.requestFocus(); return false;
        }
        String pesoTxt = pesoField.getText().trim();
        if (pesoTxt.isEmpty()) {
            showAlert("Validacion", "Falta llenar el campo 'Peso'.", Alert.AlertType.WARNING);
            pesoField.requestFocus(); return false;
        }
        try {
            if (Double.parseDouble(pesoTxt) <= 0) {
                showAlert("Validacion", "El Peso debe ser mayor a cero.", Alert.AlertType.WARNING);
                pesoField.requestFocus(); return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validacion", "El Peso debe ser un numero (ej: 2.5).", Alert.AlertType.WARNING);
            pesoField.requestFocus(); return false;
        }
        if ("Agricultura".equals(tipoCombo.getValue())) {
            String capTxt = capacidadField.getText().trim();
            if (capTxt.isEmpty()) {
                showAlert("Validacion", "La Capacidad del Tanque es obligatoria para Agricultura.",
                          Alert.AlertType.WARNING);
                capacidadField.requestFocus(); return false;
            }
            try {
                if (Double.parseDouble(capTxt) <= 0) {
                    showAlert("Validacion", "La Capacidad debe ser mayor a cero.",
                              Alert.AlertType.WARNING);
                    capacidadField.requestFocus(); return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validacion", "La Capacidad debe ser un numero (ej: 15.0).",
                          Alert.AlertType.WARNING);
                capacidadField.requestFocus(); return false;
            }
        }
        return true;
    }

    // ----------------------------------------------------------------
    // Singleton
    // ----------------------------------------------------------------
    private void testSingleton() {
        showAlert("Prueba de Singleton", controller.testSingletonConnection(),
                  Alert.AlertType.INFORMATION);
    }

    // ----------------------------------------------------------------
    // Factory Method
    // ----------------------------------------------------------------
    private void addViaFactory() {
        if (!validarCamposFormulario()) return;
        try {
            String tipo      = tipoCombo.getValue();
            String serial    = serialField.getText().trim();
            String modelo    = modeloField.getText().trim();
            String fabricante = fabricanteField.getText().trim();
            double peso      = Double.parseDouble(pesoField.getText().trim());
            double cap       = "Agricultura".equals(tipo)
                               ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
            boolean termica  = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

            Drone d = controller.addDroneFactory(tipo, serial, modelo, fabricante, peso, cap, termica);
            refreshTable();

            String fabricaConcreta = "Agricultura".equals(tipo) ? "CrearAgricultura" : "CrearVigilancia";
            String msg = "Dron creado por Factory Method.\n" +
                         "Fabrica concreta usada: [" + fabricaConcreta + "]\n" +
                         "Espacio de memoria (hashCode): " + d.hashCode() +
                         textoControl(d, "Factory Method", fabricaConcreta);   // <-- Bridge

            showAlert("Factory Method", msg, Alert.AlertType.INFORMATION);
            clearForm();
        } catch (Exception e) {
            showAlert("Error", "Error al crear el dron: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ----------------------------------------------------------------
    // Builder Pattern
    // ----------------------------------------------------------------
    private void addViaBuilder() {
        try {
            String serialBuild = "BUILDER-" + (int)(Math.random() * 1000);
            Agricultura a = controller.addDroneBuilder(
                    serialBuild, "AgriPro Max", "BuilderTech Corp", 25.5, 150.0);

            refreshTable();
            String msg = "El Builder ensamblo y guardo un Dron preconfigurado:\n\n" +
                         "Modelo: AgriPro Max\n" +
                         "Capacidad: 150 Litros\n" +
                         "Espacio de memoria (hashCode): " + a.hashCode() +
                         textoControl(a, "Builder Pattern", "Builder -> buildAndSave()");   // <-- Bridge

            showAlert("Builder Pattern", msg, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Problema en el Builder: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ----------------------------------------------------------------
    // Prototype Pattern
    // ----------------------------------------------------------------
    private void cloneViaPrototype() {
        if (!(selectedDrone instanceof Vigilancia)) return;
        try {
            Vigilancia original = (Vigilancia) selectedDrone;
            Vigilancia clon     = controller.cloneDronePrototype(original);
            refreshTable();

            String msg = "Dron Vigilancia CLONADO exitosamente.\n" +
                         "Memoria ORIGINAL: " + original.hashCode() + "\n" +
                         "Memoria CLON:     " + clon.hashCode() +
                         textoControl(clon, "Prototype Pattern", "Prototype -> cloneAndSave()");   // <-- Bridge

            showAlert("Prototype Pattern", msg, Alert.AlertType.INFORMATION);
            clearForm();
        } catch (Exception e) {
            showAlert("Error", "Problema al clonar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ----------------------------------------------------------------
    // Actualizar Dron (CRUD)
    // ----------------------------------------------------------------
    private void updateDrone() {
        if (selectedDrone == null) return;
        if (!validarCamposFormulario()) return;
        try {
            String idActual   = selectedDrone.getId();
            String tipo       = tipoCombo.getValue();
            String serial     = serialField.getText().trim();
            String modelo     = modeloField.getText().trim();
            String fabricante = fabricanteField.getText().trim();
            double peso       = Double.parseDouble(pesoField.getText().trim());
            double cap        = "Agricultura".equals(tipo)
                                ? Double.parseDouble(capacidadField.getText().trim()) : 0.0;
            boolean termica   = "Vigilancia".equals(tipo) && termicaCheck.isSelected();

            controller.updateDrone(idActual, tipo, serial, modelo, fabricante, peso, cap, termica);
            refreshTable();
            showAlert("Dron Actualizado",
                      "El dron con ID " + idActual + " fue actualizado exitosamente.",
                      Alert.AlertType.INFORMATION);
            clearForm();
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar dron: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ----------------------------------------------------------------
    // Eliminar Dron (CRUD)
    // ----------------------------------------------------------------
    private void deleteDrone() {
        if (selectedDrone == null) return;
        try {
            controller.deleteDrone(selectedDrone.getId());
            refreshTable();
            showAlert("Dron Eliminado", "El dron ha sido eliminado de la BD.",
                      Alert.AlertType.INFORMATION);
            clearForm();
        } catch (Exception e) {
            showAlert("Error", "Error al eliminar dron: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ----------------------------------------------------------------
    // Utilidades de la tabla y alertas
    // ----------------------------------------------------------------
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
