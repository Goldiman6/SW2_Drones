package com.drone.dao;

import com.drone.model.Drone;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta del DroneDAO.
 * Su única responsabilidad es manejar la persistencia de datos (guardar y leer)
 * en un archivo JSON local usando la librería Jackson.
 */
public class DroneDAOFileImpl implements DroneDAO {
    private final String FILE_PATH = "drones_data.json";
    private final ObjectMapper mapper;
    private List<Drone> drones;
    private int currentId = 1;

    /**
     * Constructor: Se ejecuta al instanciar la clase.
     * Configura el lector JSON (Mapper) e intenta cargar los datos previos del archivo.
     */
    public DroneDAOFileImpl() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.drones = loadFromFile();
        if (!drones.isEmpty()) {
            currentId = drones.stream().mapToInt(Drone::getId).max().orElse(0) + 1;
        }
    }

    /**
     * Convierte la lista actual de drones en memoria a formato JSON y la guarda en el disco duro.
     */
    private void saveToFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), drones);
        } catch (IOException e) {
            System.err.println("Error saving drones to JSON file: " + e.getMessage());
        }
    }

    /**
     * Lee el archivo JSON del disco duro y lo convierte nuevamente en una Lista de objetos Drone en memoria.
     */
    private List<Drone> loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, new TypeReference<List<Drone>>() {});
            } catch (IOException e) {
                System.err.println("Error loading drones from JSON file: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void create(Drone drone) {
        drone.setId(currentId++); // Asigna un ID automático
        drones.add(drone);        // Lo agrega a la memoria RAM
        saveToFile();             // Lo guarda en el archivo JSON permanentemente
    }

    @Override
    public Drone read(int id) {
        return drones.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Drone> readAll() {
        return new ArrayList<>(drones);
    }

    @Override
    public void update(Drone drone) {
        for (int i = 0; i < drones.size(); i++) {
            if (drones.get(i).getId() == drone.getId()) {
                drones.set(i, drone);
                saveToFile();
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        drones.removeIf(d -> d.getId() == id);
        saveToFile();
    }
}
