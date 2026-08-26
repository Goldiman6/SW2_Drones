package com.drone.servicios;

import com.drone.model.Drone;

/**
 * PATRÓN FACTORY METHOD .
 * Se encarga de instanciar drones con el modelo .
 * 
 */
public class DroneFactory {

    public static Drone crearDrone(String tipo, String serial, String modelo, String fabricante, double peso, double capacidadTanque, boolean deteccionTermica) {
        Drone drone = new Drone();
        drone.setTipo(tipo);
        drone.setSerial(serial);
        drone.setModelo(modelo);
        drone.setFabricante(fabricante);
        drone.setPeso(peso);
        
        if ("Agricultura".equalsIgnoreCase(tipo)) {
            drone.setCapacidadTanque(capacidadTanque);
            drone.setDeteccionTermica(null);
        } else if ("Vigilancia".equalsIgnoreCase(tipo)) {
            drone.setDeteccionTermica(deteccionTermica);
            drone.setCapacidadTanque(null);
        } else {
            throw new IllegalArgumentException("Tipo de dron no soportado: " + tipo);
        }
        
        return drone;
    }
}
