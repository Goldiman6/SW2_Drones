package com.drone.servicios;

/**
 * ============================================================
 * SERVICE INTERFACE - Patrón Proxy
 * ============================================================
 * Define el contrato común que comparten tanto el Servicio Real
 * como el Proxy. El Cliente (DroneController) solo debe conocer
 * esta interfaz, nunca las implementaciones concretas.
 */
public interface IDroneService {

    /**
     * Elimina un Dron identificado por su ID.
     *
     * @param id El identificador único del Dron a eliminar.
     * @throws Exception Si la operación de eliminación falla.
     */
    void eliminarDrone(String id) throws Exception;
}
