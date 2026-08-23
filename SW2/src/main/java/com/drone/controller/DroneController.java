package com.drone.controller;

import com.drone.servicios.DroneService;
import com.drone.model.Drone;
import java.util.List;

/**
 * EL CONTROLADOR (C en MVC).
 * Actúa como puente entre la Vista y la capa de Servicios.
 * Ahora se conecta al DroneService (en vez del DAO directo),
 * que internamente decide si usa el DAO Estándar o el Singleton.
 */
public class DroneController {
    
    // Conexión con la capa de servicios
    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /** Recibe los datos desde la vista, arma el objeto Drone y le pide al Servicio que lo guarde */
    public void addDrone(String serial, String modelo, String fabricante, float peso, String piloto) {
        Drone drone = new Drone(0, serial, modelo, fabricante, peso, piloto, null);
        droneService.create(drone);
    }

    /** Le pide al Servicio la lista completa para enviarla a la tabla de la vista */
    public List<Drone> getAllDrones() {
        return droneService.readAll();
    }

    /** Arma el objeto Drone con el ID existente y los datos nuevos, y le pide al Servicio que lo actualice */
    public void updateDrone(int id, String serial, String modelo, String fabricante, float peso, String piloto) {
        Drone drone = new Drone(id, serial, modelo, fabricante, peso, piloto, null);
        droneService.update(drone);
    }

    /** Le pide al Servicio que elimine el dron que corresponda a este ID */
    public void deleteDrone(int id) {
        droneService.delete(id);
    }

    /** Retorna el modo activo (Estándar o Singleton) para mostrarlo en la interfaz */
    public DroneService.Modo getModo() {
        return droneService.getModo();
    }

    /** Genera el texto demostrativo de hashCodes para la ventana emergente */
    public String generarDemoHashCode() {
        return droneService.generarDemoHashCode();
    }
}
