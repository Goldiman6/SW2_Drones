package com.drone.controller;

import com.drone.dao.DroneDAO;
import com.drone.model.Drone;
import java.util.List;

/**
 * Controlador con manejo de peticiones de la interfaz grafica conectado al DAO, 
 * siempre usa la abstracción 'DroneDAO'.
 */
public class DroneController {
    
    // Dependencia del DAO inyectada. Cumple con el principio de Inversión de Dependencias (SOLID).
    private final DroneDAO droneDAO;

    public DroneController(DroneDAO droneDAO) {
        this.droneDAO = droneDAO;
    }

    /** Recibe los datos sueltos desde la vista, arma el objeto Drone y le pide al DAO que lo guarde */
    public void addDrone(String serial, String modelo, String fabricante, float peso, String piloto) {
        Drone drone = new Drone(0, serial, modelo, fabricante, peso, piloto, null);
        droneDAO.create(drone);
    }

    /** Retorna el DAO de la lista completa para enviarla a la tabla de la vista */
    public List<Drone> getAllDrones() {
        return droneDAO.readAll();
    }

    /** Arma el objeto Drone con el ID existente y los datos nuevos, y le pide al DAO que lo actualice */
    public void updateDrone(int id, String serial, String modelo, String fabricante, float peso, String piloto) {
        Drone drone = new Drone(id, serial, modelo, fabricante, peso, piloto, null);
        droneDAO.update(drone);
    }

    /** Le pide al DAO que elimine el dron que corresponda a este ID */
    public void deleteDrone(int id) {
        droneDAO.delete(id);
    }
}
