package com.drone.dao;

import com.drone.model.Drone;
import java.util.List;

/**
 * Interfaz que define DAO para los Drones.
 * Y CRUD en el sistema
 */
public interface DroneDAO {
    
    /** Guarda un nuevo dron en el sistema */
    void create(Drone drone);
    
    /** Busca y retorna un dron específico usando su ID único */
    Drone read(int id);
    
    /** Retorna la lista completa de todos los drones guardados */
    List<Drone> readAll();
    
    /** Recibe un dron con datos modificados y reemplaza el antiguo en el sistema */
    void update(Drone drone);
    
    /** Elimina un dron del sistema usando su ID único */
    void delete(int id);
}
