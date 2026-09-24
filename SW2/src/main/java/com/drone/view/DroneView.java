package com.drone.view;

import com.drone.controller.DroneController;
import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.ComponenteSensor;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
public class DroneView extends HBox {

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
    private Button btnShowComposite;
    private Button btnExportarAdapter;
    // --- PATRON BRIDGE: RadioButtons de Tipo de Control (OPCIONALES) ---
    private ToggleGroup controlGroup;
    private RadioButton rbBasico;
    private RadioButton rbAutonomo;
    private Button btnLimpiarControl;

    // --- PATRON DECORATOR: CheckBox para Extra (OPCIONAL) ---
    private CheckBox chkBateria;

    // --- PATRON COMPOSITE: Selector de Sensores (OPCIONAL) ---
    private MenuButton menuSensores;
    private List<CheckBox> checkBoxesSensores;
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

        btnExportarAdapter = new Button("Exportar Mision a JSON (Adapter)");
        btnExportarAdapter.setOnAction(e -> exportarMisionAction());

        HBox topBox = new HBox(10, btnTestSingleton, btnAddBuilder, btnClonePrototype, btnExportarAdapter);


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
        // SECCION 3.5: Patron Decorator - Extras (OPCIONAL)
        // ----------------------------------------------------------------
        chkBateria = new CheckBox("Agregar bateria adicional?");
        HBox decoratorBox = new HBox(12, chkBateria);
        decoratorBox.setPadding(new Insets(5, 0, 5, 0));

        // ----------------------------------------------------------------
        // SECCION 3.6: Patron Composite - Selector de Sensores (OPCIONAL)
        // ----------------------------------------------------------------
        menuSensores = new MenuButton("Seleccionar Sensores...");
        menuSensores.setPrefWidth(300);
        checkBoxesSensores = new ArrayList<>();
        
        //construimos el menu
        construirMenuSensores(controller.obtenerArbolMaestroSensores(), menuSensores, 0, true);

        Label lblComposite = new Label("Seleccionar Sensores:");
        HBox compositeBox = new HBox(12, lblComposite, menuSensores);
        compositeBox.setPadding(new Insets(5, 0, 5, 0));

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
        
        btnShowComposite = new Button("Sensores (Composite)");
        btnShowComposite.setOnAction(e -> mostrarCompositeAction());

        HBox actionBox = new HBox(10, btnAddFactory, btnUpdate, btnDelete, btnClear, btnShowComposite);

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

        // Construir panel izquierdo (Formulario y Controles)
        VBox leftPane = new VBox(15);
        leftPane.setPadding(new Insets(0, 10, 0, 0)); // Espaciado derecho
        leftPane.getChildren().addAll(topBox, formPane, bridgeBox, decoratorBox, compositeBox, actionBox);
        leftPane.setPrefWidth(550);
        leftPane.setMinWidth(450);

        // Configurar la tabla para que ocupe el resto del espacio en la derecha
        HBox.setHgrow(table, Priority.ALWAYS);

        // Agregar paneles al HBox principal (Split screen)
        getChildren().addAll(leftPane, table);
    }

    // ----------------------------------------------------------------
    // Metodo utilitario: resuelve el TipoControl segun RadioButton activo
    // Retorna null si ningun radio esta seleccionado (control es opcional)
    // ----------------------------------------------------------------
    /**
     * UNICO METODO QUE INVOCA AL FACADE
     * Recolecta la seleccion visual del usuario y le pide al controlador
     * que ejecute el Facade. El Facade internamente llamara a Bridge, Decorator y Composite.
     */
        private String obtenerTrazaDesdeControlador(Drone dron) {
        // 1. Tipo de control (Bridge)
        String tipoControl = null;
        if (rbBasico != null && rbBasico.isSelected())     tipoControl = "basico";
        if (rbAutonomo != null && rbAutonomo.isSelected()) tipoControl = "autonomo";

        // 2. Extra de bateria (Decorator)
        boolean usaBateria = chkBateria != null && chkBateria.isSelected();

        // 3. Sensores (Composite)
        List<String> sensoresSeleccionados = new ArrayList<>();
        if (checkBoxesSensores != null) {
            for (CheckBox cb : checkBoxesSensores) {
                if (cb.isSelected() && cb.getUserData() != null) {
                    sensoresSeleccionados.add(cb.getUserData().toString());
                }
            }
        }

        // Se delega al Controlador que orquestara el Facade
        return controller.ejecutarFacade(dron, tipoControl, usaBateria, sensoresSeleccionados);
    }

    /**
     * Muestra una ventana (placebo) con la jerarquia del Patron Composite de Sensores.
     * Lee la estructura generada dinamicamente en memoria y la renderiza en un TreeView.
     */
  
    private void exportarMisionAction() {
        String resultado = controller.exportarMisionJson(selectedDrone);
        showScrollableAlert("Patron Adapter - Exportar Mision JSON", resultado);
    }



    private void mostrarCompositeAction() {
        // 1. Obtener el arbol estatico desde la capa de servicios
        ComponenteSensor raizComposite = controller.obtenerArbolMaestroSensores();
        
        // 2. Construir el componente visual TreeItem raiz recursivamente
        TreeItem<String> rootItem = poblarTreeItem(raizComposite);
        rootItem.setExpanded(true); // Expandir la raiz por defecto
        
        // 3. Crear el componente TreeView
        TreeView<String> treeView = new TreeView<>(rootItem);
        
        // 4. Configurar y mostrar el Dialog / Ventana Emergente
        VBox content = new VBox(treeView);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        content.setPrefSize(400, 500);
        content.setPadding(new Insets(10));
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Demostracion Patron Composite - Sensores");
        dialog.setHeaderText("Estructura Jerarquica de Sensores en Memoria");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        dialog.showAndWait();
    }

    /**
     * Metodo recursivo para convertir la jerarquia Comanposite (ComponenteSensor)
     * a la jerarquia visual de JavaFX (TreeItem).
     */
    private TreeItem<String> poblarTreeItem(ComponenteSensor componente) {
        TreeItem<String> item = new TreeItem<>(componente.getNombre());
        item.setExpanded(true); // Expandir todos los niveles
        
        // Si el componente tiene hijos, iterar y agregarlos de forma recursiva
        for (ComponenteSensor hijo : componente.getHijos()) {
            item.getChildren().add(poblarTreeItem(hijo));
        }
        
        return item;
    }

    /**
     * Construye el MenuButton de sensores a partir del arbol Composite.
     * Recorre grupos y hojas por igual y deja el nombre real en userData
     * para que el Facade/Composite pueda reconstruir la rama seleccionada.
     */
    /**
     * Construye el MenuButton de sensores respetando el árbol Composite:
     *  - GRUPOS  -> Label no seleccionable (separador visual en negrita).
     *  - HOJAS   -> CheckBox seleccionable con sangría proporcional al nivel.
     *
     * @param componente Nodo actual del árbol Composite.
     * @param menu       MenuButton donde se agregan los items.
     * @param nivel      Profundidad actual (0 = primer nivel bajo la raíz).
     * @param esRaiz     Si es true, itera los hijos sin pintar el nodo raíz.
     */
    private void construirMenuSensores(ComponenteSensor componente, MenuButton menu,
                                       int nivel, boolean esRaiz) {
        if (componente == null || menu == null) return;

        if (esRaiz) {
            // La raíz ("Sensor General") no se muestra; iteramos sus hijos
            for (ComponenteSensor hijo : componente.getHijos()) {
                construirMenuSensores(hijo, menu, 0, false);
            }
            return;
        }

        String indent = "    ".repeat(nivel); // 4 espacios por nivel

        boolean esGrupo = componente.getHijos() != null && !componente.getHijos().isEmpty();

        if (esGrupo) {
            // --- GRUPO: separador visual no seleccionable ---
            Label lblGrupo = new Label(indent + "▸ " + componente.getNombre());
            lblGrupo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
            lblGrupo.setMouseTransparent(true); // no se puede interactuar
            CustomMenuItem separador = new CustomMenuItem(lblGrupo, false);
            separador.setHideOnClick(false);
            menu.getItems().add(separador);

            // Agregar un SeparatorMenuItem arriba si es el primer nivel
            if (nivel == 0 && !menu.getItems().isEmpty() && menu.getItems().size() > 1) {
                menu.getItems().add(menu.getItems().size() - 1, new SeparatorMenuItem());
            }

            // Recursión en los hijos con nivel + 1
            for (ComponenteSensor hijo : componente.getHijos()) {
                construirMenuSensores(hijo, menu, nivel + 1, false);
            }
        } else {
            // --- HOJA: CheckBox seleccionable ---
            CheckBox cb = new CheckBox(indent + componente.getNombre());
            cb.setUserData(componente.getNombre());
            checkBoxesSensores.add(cb);
            CustomMenuItem item = new CustomMenuItem(cb, false);
            item.setHideOnClick(false);
            menu.getItems().add(item);
        }
    }

    private void testSingleton() {
        String resultado = controller.testSingletonConnection();
        showScrollableAlert("Patron Singleton - Conexion BD", resultado);
    }

    private void addViaFactory() {
        try {
            DatosFormulario datos = leerFormulario();
            Drone creado = controller.addDroneFactory(
                    datos.tipo, datos.serial, datos.modelo, datos.fabricante,
                    datos.peso, datos.capacidad, datos.termica);
            String traza = obtenerTrazaDesdeControlador(creado);
            droneData.add(creado);
            showScrollableAlert("Dron creado (Factory Method)",
                    "Dron guardado con ID " + creado.getId() + "\n\n" + traza);
            clearForm();
        } catch (Exception ex) {
            showError("No se pudo crear el dron", ex.getMessage());
        }
    }

    private void addViaBuilder() {
        try {
            if (!"Agricultura".equals(tipoCombo.getValue())) {
                showError("Builder", "El patron Builder solo crea drones de Agricultura.");
                return;
            }
            DatosFormulario datos = leerFormulario();
            Agricultura creado = controller.addDroneBuilder(
                    datos.serial, datos.modelo, datos.fabricante, datos.peso, datos.capacidad);
            String traza = obtenerTrazaDesdeControlador(creado);
            droneData.add(creado);
            showScrollableAlert("Dron creado (Builder)",
                    "Dron de Agricultura guardado con ID " + creado.getId() + "\n\n" + traza);
            clearForm();
        } catch (Exception ex) {
            showError("No se pudo crear con Builder", ex.getMessage());
        }
    }

    private void cloneViaPrototype() {
        try {
            if (!(selectedDrone instanceof Vigilancia)) {
                showError("Prototype", "El patron Prototype solo clona drones de Vigilancia.");
                return;
            }
            Vigilancia clon = controller.cloneDronePrototype((Vigilancia) selectedDrone);
            String traza = obtenerTrazaDesdeControlador(clon);
            droneData.add(clon);
            showScrollableAlert("Dron clonado (Prototype)",
                    "Clon guardado con ID " + clon.getId() + "\n\n" + traza);
        } catch (Exception ex) {
            showError("No se pudo clonar", ex.getMessage());
        }
    }

    private void updateDrone() {
        try {
            if (selectedDrone == null) {
                showError("Actualizar", "Seleccione un dron de la tabla.");
                return;
            }
            DatosFormulario datos = leerFormulario();
            Drone actualizado = controller.updateDrone(
                    selectedDrone.getId(), datos.tipo, datos.serial, datos.modelo,
                    datos.fabricante, datos.peso, datos.capacidad, datos.termica);
            String traza = obtenerTrazaDesdeControlador(actualizado);
            int idx = droneData.indexOf(selectedDrone);
            if (idx >= 0) {
                droneData.set(idx, actualizado);
            } else {
                droneData.setAll(controller.getAllDrones());
            }
            showScrollableAlert("Dron actualizado",
                    "Dron " + actualizado.getId() + " actualizado.\n\n" + traza);
            table.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            showError("No se pudo actualizar", ex.getMessage());
        }
    }

    private void deleteDrone() {
        try {
            if (selectedDrone == null) {
                showError("Eliminar", "Seleccione un dron de la tabla.");
                return;
            }
            controller.deleteDrone(selectedDrone.getId());
            droneData.remove(selectedDrone);
            showInfo("Eliminado", "El dron fue eliminado mediante el Patron Proxy.");
            table.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("cancelada")) {
                return;
            }
            showError("No se pudo eliminar", ex.getMessage());
        }
    }

    private void clearForm() {
        selectedDrone = null;
        serialField.clear();
        modeloField.clear();
        fabricanteField.clear();
        pesoField.clear();
        capacidadField.clear();
        termicaCheck.setSelected(false);
        tipoCombo.setValue("Agricultura");
        tipoCombo.setDisable(false);
        dinamicoLabel.setText("Capacidad Tanque:");
        capacidadField.setVisible(true);
        capacidadField.setManaged(true);
        termicaCheck.setVisible(false);
        termicaCheck.setManaged(false);
        btnAddFactory.setDisable(false);
        btnAddBuilder.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnClonePrototype.setDisable(true);
        controlGroup.selectToggle(null);
        chkBateria.setSelected(false);
        if (checkBoxesSensores != null) {
            for (CheckBox cb : checkBoxesSensores) {
                cb.setSelected(false);
            }
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            table.getSelectionModel().clearSelection();
        }
    }


    private DatosFormulario leerFormulario() throws Exception {
        String serial = serialField.getText() == null ? "" : serialField.getText().trim();
        String modelo = modeloField.getText() == null ? "" : modeloField.getText().trim();
        String fabricante = fabricanteField.getText() == null ? "" : fabricanteField.getText().trim();
        if (serial.isEmpty() || modelo.isEmpty() || fabricante.isEmpty()) {
            throw new Exception("Serial, modelo y fabricante son obligatorios.");
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoField.getText().trim());
        } catch (Exception e) {
            throw new Exception("El peso debe ser un numero valido.");
        }

        String tipo = tipoCombo.getValue();
        double capacidad = 0;
        boolean termica = termicaCheck.isSelected();
        if ("Agricultura".equals(tipo)) {
            try {
                capacidad = Double.parseDouble(capacidadField.getText().trim());
            } catch (Exception e) {
                throw new Exception("La capacidad del tanque debe ser un numero valido.");
            }
        }
        return new DatosFormulario(tipo, serial, modelo, fabricante, peso, capacidad, termica);
    }

    private static class DatosFormulario {
        public final String tipo;
        public final String serial;
        public final String modelo;
        public final String fabricante;
        public final double peso;
        public final double capacidad;
        public final boolean termica;

        public DatosFormulario(String tipo, String serial, String modelo, String fabricante,
                                double peso, double capacidad, boolean termica) {
            this.tipo = tipo;
            this.serial = serial;
            this.modelo = modelo;
            this.fabricante = fabricante;
            this.peso = peso;
            this.capacidad = capacidad;
            this.termica = termica;
        }
    }


    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showScrollableAlert(String titulo, String contenido) {
        TextArea area = new TextArea(contenido == null ? "" : contenido);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(560, 380);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(titulo);
        dialog.getDialogPane().setContent(area);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void showError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void showInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}





