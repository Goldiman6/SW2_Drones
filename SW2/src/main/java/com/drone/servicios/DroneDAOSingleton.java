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

    private final String FILE_PATH;
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
        
        // Crear directorio de datos si no existe
        String userHome = System.getProperty("user.home");
        File dataDir = new File(userHome, "drone_data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        this.FILE_PATH = new File(dataDir, "drones_singleton.json").getAbsolutePath();
        
        this.drones = loadFromFile();
        if (!drones.isEmpty()) {
            currentId = drones.stream().mapToInt(d -> Integer.parseInt(d.getId())).max().orElse(0) + 1;
        }
        
        System.out.println("[DAO Singleton] Archivo de persistencia: " + FILE_PATH);
    }

    // ==================== PERSISTENCIA JSON ====================

    /** Guarda la lista completa de drones en el archivo JSON */
    private void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            mapper.writeValue(file, drones);
            System.out.println("[DAO Singleton] Guardados " + drones.size() + " drones en archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[DAO Singleton] Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Lee los drones desde el archivo JSON al iniciar */
    private List<Drone> loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                List<Drone> loadedDrones = mapper.readValue(file, new TypeReference<List<Drone>>() {});
                System.out.println("[DAO Singleton] Cargados " + loadedDrones.size() + " drones desde archivo.");
                return loadedDrones;
            } catch (IOException e) {
                System.err.println("[DAO Singleton] Error al leer: " + e.getMessage());
                e.printStackTrace();
                // Si hay error con archivo corrupto, borrarlo y empezar de cero
                file.delete();
                System.out.println("[DAO Singleton] Archivo corrupto eliminado, iniciando con lista vacía.");
            }
        } else {
            System.out.println("[DAO Singleton] No existe archivo de persistencia, iniciando con lista vacía.");
        }
        return new ArrayList<>();
    }

    // ==================== OPERACIONES CRUD ====================

    /** CREATE: Guarda un nuevo dron asignándole un ID automático */
    public void create(Drone drone) {
        drone.setId(String.valueOf(currentId++));
        drones.add(drone);
        saveToFile();
    }

    /** READ: Busca un dron por su ID */
    public Drone read(String id) {
        return drones.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
    }

    /** READ ALL: Retorna una copia de la lista completa de drones */
    public List<Drone> readAll() {
        return new ArrayList<>(drones);
    }

    /** UPDATE: Reemplaza un dron existente con datos nuevos */
    public void update(Drone drone) {
        for (int i = 0; i < drones.size(); i++) {
            if (drones.get(i).getId().equals(drone.getId())) {
                drones.set(i, drone);
                saveToFile();
                return;
            }
        }
    }

    /** DELETE: Elimina un dron por su ID */
    public void delete(String id) {
        drones.removeIf(d -> d.getId().equals(id));
        saveToFile();
    }
}
