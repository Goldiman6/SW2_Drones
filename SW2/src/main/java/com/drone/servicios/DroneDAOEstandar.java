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
 * DAO ESTÁNDAR (instancia normal).
 * Cada vez que se hace "new DroneDAOEstandar()", se crea una instancia NUEVA e INDEPENDIENTE.
 * Si se crearan dos instancias, cada una tendría su propia lista en memoria,
 * lo que podría causar inconsistencias de datos.
 * 
 * Persiste los datos en un archivo JSON usando Jackson.
 */
public class DroneDAOEstandar {
    private final String FILE_PATH = "drones_estandar.json";
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
        this.drones = loadFromFile();
        if (!drones.isEmpty()) {
            currentId = drones.stream().mapToInt(Drone::getId).max().orElse(0) + 1;
        }
        System.out.println("[DAO Estándar] Nueva instancia creada (hashCode: " + this.hashCode() + ")");
    }

    // ==================== PERSISTENCIA JSON ====================

    /** Guarda la lista completa de drones en el archivo JSON */
    private void saveToFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), drones);
        } catch (IOException e) {
            System.err.println("[DAO Estándar] Error al guardar: " + e.getMessage());
        }
    }

    /** Lee los drones desde el archivo JSON al iniciar */
    private List<Drone> loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, new TypeReference<List<Drone>>() {});
            } catch (IOException e) {
                System.err.println("[DAO Estándar] Error al leer: " + e.getMessage());
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
