package com.drone.servicios;

import com.drone.dao.DroneDAO;

/**
 * ============================================================
 * PROXY SERVICE  - Patrón Proxy
 * ============================================================
 * Implementa la lógica REAL de eliminación comunicándose
 * directamente con el DAO. El Proxy (DroneProxy) protege el acceso a esta clase.
 *
 * El Cliente (DroneController) NUNCA instancia esta clase directamente;
 * siempre accede a través del DroneProxy.
 */
public class DroneProxyService implements IDroneService {

    private final DroneDAO droneDAO;

    public DroneProxyService(DroneDAO droneDAO) {
        this.droneDAO = droneDAO;
    }

    /**
     * Elimina físicamente el Dron de la base de datos PostgreSQL.
     * Solo se ejecuta si el DroneProxy verificó las credenciales correctamente.
     *
     * @param id El identificador único del Dron a eliminar.
     * @throws Exception Si el DAO no puede completar la operación.
     */
    @Override
    public void eliminarDrone(String id) throws Exception {
        boolean ok = droneDAO.eliminarDrone(id);
        if (!ok) {
            throw new Exception("No se pudo eliminar el Dron con ID: " + id);
        }
    }
}
