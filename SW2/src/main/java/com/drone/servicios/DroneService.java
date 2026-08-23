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

    /** Modos disponibles para elegir la arquitectura del DAO */
    public enum Modo {
        ESTANDAR,   // Crea una instancia nueva del DAO cada vez
        SINGLETON   // Usa una única instancia compartida
    }

    private final Modo modo;
    private DroneDAOEstandar daoEstandar;
    private DroneDAOSingleton daoSingleton;

    /**
     * Constructor que recibe el modo de operación.
     * Dependiendo del modo, inicializa el DAO correspondiente.
     */
    public DroneService(Modo modo) {
        this.modo = modo;
        if (modo == Modo.ESTANDAR) {
            this.daoEstandar = new DroneDAOEstandar();
            System.out.println("[Servicio] Modo ESTÁNDAR activado.");
        } else {
            this.daoSingleton = DroneDAOSingleton.getInstance();
            System.out.println("[Servicio] Modo SINGLETON activado.");
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
    public Drone read(int id) {
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
    public void delete(int id) {
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
     * Genera un texto demostrativo que compara hashCodes para evidenciar
     * la diferencia entre Estándar y Singleton.
     * Se muestra al usuario en una ventana emergente al crear un dron.
     */
    public String generarDemoHashCode() {
        StringBuilder sb = new StringBuilder();

        if (modo == Modo.SINGLETON) {
            // Solicitar la instancia dos veces: debe ser LA MISMA
            DroneDAOSingleton ref1 = DroneDAOSingleton.getInstance();
            DroneDAOSingleton ref2 = DroneDAOSingleton.getInstance();

            sb.append("══════ PATRÓN SINGLETON ══════\n\n");
            sb.append("Referencia 1 → hashCode: ").append(ref1.hashCode()).append("\n");
            sb.append("Referencia 2 → hashCode: ").append(ref2.hashCode()).append("\n");
        } else {
            // Crear dos instancias nuevas: deben ser DIFERENTES
            DroneDAOEstandar ref1 = new DroneDAOEstandar();
            DroneDAOEstandar ref2 = new DroneDAOEstandar();

            sb.append("══════ DAO ESTÁNDAR ══════\n\n");
            sb.append("Instancia 1 → hashCode: ").append(ref1.hashCode()).append("\n");
            sb.append("Instancia 2 → hashCode: ").append(ref2.hashCode()).append("\n");
        }

        return sb.toString();
    }
}
