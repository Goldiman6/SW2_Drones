package com.drone.servicios;

import com.drone.model.Drone;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SINGLETON.
 * Garantiza que SOLO EXISTA UNA ÚNICA INSTANCIA en toda la aplicación.
 * 
 * - Evita que se creen múltiples conexiones al mismo archivo de datos.
 * - Asegura que todas las partes del programa trabajen con la misma lista de drones.
 * - Ahorra memoria al no duplicar objetos innecesariamente.
 * 
 * ¿Cómo funciona?
 * - El constructor es PRIVADO: nadie puede hacer "new DroneDAOSingleton()".
 * - Para obtener la instancia se usa: DroneDAOSingleton.getInstance()
 * - La primera vez que se llama, crea la instancia. Las siguientes veces, 
 *   devuelve la MISMA instancia ya creada.
 */
public class DroneDAOSingleton {

    // ==================== PATRÓN SINGLETON ====================

    /** La ÚNICA instancia que existirá de esta clase (inicialmente null) */
    private static DroneDAOSingleton instancia;

    /**
     * Punto de acceso GLOBAL a la instancia única.
     * Si aún no existe, la crea. Si ya existe, retorna la misma.
     * 'synchronized' evita problemas si dos hilos intentan crearla al mismo tiempo.
     */
    public static synchronized DroneDAOSingleton getInstance() {
        if (instancia == null) {
            instancia = new DroneDAOSingleton();
            System.out.println("[DAO Singleton] Instancia CREADA por primera vez (hashCode: " + instancia.hashCode() + ")");
        } else {
            System.out.println("[DAO Singleton] Retornando instancia EXISTENTE (hashCode: " + instancia.hashCode() + ")");
        }
        return instancia;
    }

    // ==================== ATRIBUTOS ====================

    private final String FILE_PATH = "drones_singleton.json";
    private final ObjectMapper mapper;
    private List<Drone> drones;
    private int currentId = 1;

    /**
     * Constructor PRIVADO: solo esta clase puede llamarlo (desde getInstance()).
     * Esto IMPIDE que alguien externo haga "new DroneDAOSingleton()".
     */
    private DroneDAOSingleton() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.drones = loadFromFile();
        if (!drones.isEmpty()) {
            currentId = drones.stream().mapToInt(Drone::getId).max().orElse(0) + 1;
        }
    }

    // ==================== PERSISTENCIA JSON ====================

    /** Guarda la lista completa de drones en el archivo JSON */
    private void saveToFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), drones);
        } catch (IOException e) {
            System.err.println("[DAO Singleton] Error al guardar: " + e.getMessage());
        }
    }

    /** Lee los drones desde el archivo JSON al iniciar */
    private List<Drone> loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, new TypeReference<List<Drone>>() {});
            } catch (IOException e) {
                System.err.println("[DAO Singleton] Error al leer: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    // ==================== OPERACIONES CRUD ====================

    /** CREATE: Guarda un nuevo dron asignándole un ID automático */
    public void create(Drone drone) {
        drone.setId(currentId++);
        drones.add(drone);
        saveToFile();
    }

    /** READ: Busca un dron por su ID */
    public Drone read(int id) {
        return drones.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    /** READ ALL: Retorna una copia de la lista completa de drones */
    public List<Drone> readAll() {
        return new ArrayList<>(drones);
    }

    /** UPDATE: Reemplaza un dron existente con datos nuevos */
    public void update(Drone drone) {
        for (int i = 0; i < drones.size(); i++) {
            if (drones.get(i).getId() == drone.getId()) {
                drones.set(i, drone);
                saveToFile();
                return;
            }
        }
    }

    /** DELETE: Elimina un dron por su ID */
    public void delete(int id) {
        drones.removeIf(d -> d.getId() == id);
        saveToFile();
    }
}
