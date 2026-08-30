package com.drone.servicios;

import com.drone.model.Drone;

public interface FactoryCreator {
    /**
     * Crea un dron, lo configura y lo guarda directamente en la base de datos a travs del DAO.
     */
    Drone crearYGuardar(String id, String serial, String modelo, String fabricante, double peso, double capacidad, boolean termica) throws Exception;
}
