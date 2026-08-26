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

    /** Recibe los datos desde la vista y crea el objeto según el modo arquitectónico activo */
    public void addDrone(String tipo, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) {
        Drone drone = null;

        if (droneService.getModo() == DroneService.Modo.FACTORY) {
            // USANDO PATRÓN FACTORY METHOD
            drone = com.drone.servicios.DroneFactory.crearDrone(tipo, serial, modelo, fabricante, peso, capacidad, termica);
        } else {
            // USANDO CREACIÓN DIRECTA (Normal) con modelo simplificado
            drone = new Drone();
            drone.setTipo(tipo);
            drone.setSerial(serial);
            drone.setModelo(modelo);
            drone.setFabricante(fabricante);
            drone.setPeso(peso);
            
            if ("Agricultura".equalsIgnoreCase(tipo)) {
                drone.setCapacidadTanque(capacidad);
                drone.setDeteccionTermica(null);
            } else if ("Vigilancia".equalsIgnoreCase(tipo)) {
                drone.setDeteccionTermica(termica);
                drone.setCapacidadTanque(null);
            }
        }

        if (drone != null) {
            droneService.create(drone);
        }
    }

    /** Le pide al Servicio la lista completa para enviarla a la tabla de la vista */
    public List<Drone> getAllDrones() {
        return droneService.readAll();
    }

    /** Crea un dron, le inyecta el ID existente, y al Servicio que lo actualice */
    public void updateDrone(String id, String tipo, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) {
        Drone drone = null;

        if (droneService.getModo() == DroneService.Modo.FACTORY) {
            drone = com.drone.servicios.DroneFactory.crearDrone(tipo, serial, modelo, fabricante, peso, capacidad, termica);
        } else {
            // USANDO CREACIÓN DIRECTA (Normal) con modelo simplificado
            drone = new Drone();
            drone.setId(id);
            drone.setTipo(tipo);
            drone.setSerial(serial);
            drone.setModelo(modelo);
            drone.setFabricante(fabricante);
            drone.setPeso(peso);
            
            if ("Agricultura".equalsIgnoreCase(tipo)) {
                drone.setCapacidadTanque(capacidad);
                drone.setDeteccionTermica(null);
            } else if ("Vigilancia".equalsIgnoreCase(tipo)) {
                drone.setDeteccionTermica(termica);
                drone.setCapacidadTanque(null);
            }
        }
        
        if (drone != null) {
            drone.setId(id); // Asegurar que mantiene su ID
            droneService.update(drone);
        }
    }

    /** Le pide al Servicio que elimine el dron que corresponda a este ID */
    public void deleteDrone(String id) {
        droneService.delete(id);
    }

    /** Retorna el modo activo (Estándar o Singleton) para mostrarlo en la interfaz */
    public DroneService.Modo getModo() {
        return droneService.getModo();
    }

    /** Genera el texto demostrativo de la arquitectura para la ventana emergente */
    public String generarDemoHashCode(String tipoDron) {
        return droneService.generarDemoHashCode(tipoDron);
    }
}
