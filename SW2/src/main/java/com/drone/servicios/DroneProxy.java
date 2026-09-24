package com.drone.servicios;

/**
 * ============================================================
 * PROXY - Patrón Proxy (Protection Proxy)
 * ============================================================
 * Implementa la misma interfaz que el Servicio Real (IDroneService).
 * 
 * La responsabilidad exclusiva de esta clase es proteger el acceso.
 * Recibe el intento de contraseña y comprueba si tiene permisos
 * antes de llamar a DroneProxyService.
 */
public class DroneProxy implements IDroneService {

    /** Regla de negocio de seguridad encapsulada en el Proxy */
    private static final String CONTRASENA_CORRECTA = "admin123";

    /** El Servicio Real al que el Proxy delega si la verificación es exitosa. */
    private final IDroneService realService;
    
    /** Contraseña ingresada por el cliente desde la UI */
    private final String passwordIntentada;

    public DroneProxy(IDroneService realService, String passwordIntentada) {
        this.realService = realService;
        this.passwordIntentada = passwordIntentada;
    }

    /**
     * Punto de entrada interceptado por el Proxy.
     * Verifica la contraseña internamente y decide si permite o rechaza la eliminación.
     *
     * @param id El ID del Dron que se desea eliminar.
     * @throws Exception Si el acceso es denegado o si el servicio real falla.
     */
    @Override
    public void eliminarDrone(String id) throws Exception {
        // EL PROXY HACE LA COMPROBACIÓN LÓGICA
        if (CONTRASENA_CORRECTA.equals(passwordIntentada)) {
            // Acceso autorizado: delegar al DroneProxyService (Servicio Real)
            realService.eliminarDrone(id);
        } else {
            // Acceso denegado: lanzar error para que la UI lo atrape
            throw new Exception("Contrasena incorrecta. Intente de nuevo.");
        }
    }
}
