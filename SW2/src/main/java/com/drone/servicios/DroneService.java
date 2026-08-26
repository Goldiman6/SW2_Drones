package com.drone.servicios;

import com.drone.model.Drone;
import java.util.List;

/**
 * SERVICIO DE DRONES (Capa de Servicio).
 * Actúa como intermediario entre el Controlador y los DAOs.
 * 
 * Su propósito es abstraer qué tipo de DAO se está usando (Estándar o Singleton),
 * para que el Controlador y la Vista no necesiten saber los detalles de implementación.
 * 
 * Recibe un "modo" al construirse y enruta todas las operaciones CRUD
 * al DAO correspondiente.
 */
public class DroneService {

    /** Modos disponibles para elegir la arquitectura principal a evaluar */
    public enum Modo {
        ESTANDAR,   // Crea instancia nueva del DAO cada vez, creación directa
        SINGLETON,  // Usa única instancia del DAO, creación directa
        FACTORY     // Usa Singleton para persistencia, pero obliga a crear por Factory
    }

    private final Modo modo;
    private DroneDAOEstandar daoEstandar;
    private DroneDAOSingleton daoSingleton;

    /**
     * Constructor que recibe el modo de operación.
     */
    public DroneService(Modo modo) {
        this.modo = modo;
        if (modo == Modo.ESTANDAR) {
            this.daoEstandar = new DroneDAOEstandar();
            System.out.println("[Servicio] Modo ESTÁNDAR activado.");
        } else if (modo == Modo.SINGLETON) {
            this.daoSingleton = DroneDAOSingleton.getInstance();
            System.out.println("[Servicio] Modo SINGLETON activado.");
        } else if (modo == Modo.FACTORY) {
            this.daoSingleton = DroneDAOSingleton.getInstance();
            System.out.println("[Servicio] Modo FACTORY activado.");
        }
    }

    // ==================== CRUD delegado al DAO activo ====================

    /** Delega la creación al DAO activo según el modo */
    public void create(Drone drone) {
        if (modo == Modo.ESTANDAR) {
            daoEstandar.create(drone);
        } else {
            daoSingleton.create(drone);
        }
    }

    /** Delega la lectura por ID al DAO activo */
    public Drone read(String id) {
        if (modo == Modo.ESTANDAR) {
            return daoEstandar.read(id);
        } else {
            return daoSingleton.read(id);
        }
    }

    /** Delega la lectura completa al DAO activo */
    public List<Drone> readAll() {
        if (modo == Modo.ESTANDAR) {
            return daoEstandar.readAll();
        } else {
            return daoSingleton.readAll();
        }
    }

    /** Delega la actualización al DAO activo */
    public void update(Drone drone) {
        if (modo == Modo.ESTANDAR) {
            daoEstandar.update(drone);
        } else {
            daoSingleton.update(drone);
        }
    }

    /** Delega la eliminación al DAO activo */
    public void delete(String id) {
        if (modo == Modo.ESTANDAR) {
            daoEstandar.delete(id);
        } else {
            daoSingleton.delete(id);
        }
    }

    /** Retorna el modo activo para mostrarlo en la interfaz */
    public Modo getModo() {
        return modo;
    }

    /**
     * Genera un texto demostrativo que muestra qué arquitectura se usó.
     */
    public String generarDemoHashCode(String tipoDron) {
        StringBuilder sb = new StringBuilder();

        if (modo == Modo.SINGLETON) {
            DroneDAOSingleton ref1 = DroneDAOSingleton.getInstance();
            DroneDAOSingleton ref2 = DroneDAOSingleton.getInstance();
            sb.append("══════ PATRÓN SINGLETON ══════\n\n");
            sb.append("Referencia 1 → hashCode: ").append(ref1.hashCode()).append("\n");
            sb.append("Referencia 2 → hashCode: ").append(ref2.hashCode()).append("\n");
        } else if (modo == Modo.ESTANDAR) {
            DroneDAOEstandar ref1 = new DroneDAOEstandar();
            DroneDAOEstandar ref2 = new DroneDAOEstandar();
            sb.append("══════ DAO ESTÁNDAR ══════\n\n");
            sb.append("Instancia 1 → hashCode: ").append(ref1.hashCode()).append("\n");
            sb.append("Instancia 2 → hashCode: ").append(ref2.hashCode()).append("\n");
        } else if (modo == Modo.FACTORY) {
            sb.append("══════ FACTORY METHOD ══════\n\n");
            sb.append("El Dron de tipo [").append(tipoDron).append("]\n");
            sb.append("fue creado delegando la responsabilidad a la\n");
            sb.append("fábrica estática (DroneFactory.crearDrone).\n");
            sb.append("El controlador NO usó 'new Drone...()' directamente.");
        }

        return sb.toString();
    }
}
