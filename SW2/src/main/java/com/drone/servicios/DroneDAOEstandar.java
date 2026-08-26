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
 * DAO ESTÁNDAR 
 * Cada vez que se hace "new DroneDAOEstandar()", se crea una instancia NUEVA e INDEPENDIENTE.
 * Si se crearan dos instancias, cada una tendría su propia lista en memoria,
 * lo que podría causar inconsistencias de datos.
 * 
 * Persiste los datos en un archivo JSON
 */
public class DroneDAOEstandar {
    private final String FILE_PATH;
    private final ObjectMapper mapper;
    private List<Drone> drones;
    private int currentId = 1;

    /**
     * Constructor PÚBLICO: cualquiera puede crear múltiples instancias.
     * Esto es la diferencia clave con el Singleton.
     */
    public DroneDAOEstandar() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Crear directorio de datos si no existe
        String userHome = System.getProperty("user.home");
        File dataDir = new File(userHome, "drone_data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        this.FILE_PATH = new File(dataDir, "drones_estandar.json").getAbsolutePath();
        
        this.drones = loadFromFile();
        if (!drones.isEmpty()) {
            currentId = drones.stream().mapToInt(d -> Integer.parseInt(d.getId())).max().orElse(0) + 1;
        }
        System.out.println("[DAO Estándar] Nueva instancia creada (hashCode: " + this.hashCode() + ")");
        System.out.println("[DAO Estándar] Archivo de persistencia: " + FILE_PATH);
    }

    // ==================== PERSISTENCIA JSON ====================

    /** Guarda la lista completa de drones en el archivo JSON */
    private void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            mapper.writeValue(file, drones);
            System.out.println("[DAO Estándar] Guardados " + drones.size() + " drones en archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[DAO Estándar] Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Lee los drones desde el archivo JSON al iniciar */
    private List<Drone> loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                List<Drone> loadedDrones = mapper.readValue(file, new TypeReference<List<Drone>>() {});
                System.out.println("[DAO Estándar] Cargados " + loadedDrones.size() + " drones desde archivo.");
                return loadedDrones;
            } catch (IOException e) {
                System.err.println("[DAO Estándar] Error al leer: " + e.getMessage());
                e.printStackTrace();
                // Si hay error con archivo corrupto, borrarlo y empezar de cero
                file.delete();
                System.out.println("[DAO Estándar] Archivo corrupto eliminado, iniciando con lista vacía.");
            }
        } else {
            System.out.println("[DAO Estándar] No existe archivo de persistencia, iniciando con lista vacía.");
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
