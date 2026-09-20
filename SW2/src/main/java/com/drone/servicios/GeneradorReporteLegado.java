package com.drone.servicios;

/**
 * ============================================================
 * ADAPTEE - Patrón Adapter
 * ============================================================
 * Simula un sistema LEGADO / INCOMPATIBLE que ya existe en la
 * empresa y no puede modificarse (p.ej.: un generador de reportes
 * de una librería externa o un sistema antiguo).
 *
 * Su interfaz es INCOMPATIBLE con lo que el cliente espera:
 *   - El cliente espera:   exportar()
 *   - Esta clase tiene:    generarReporteXml(String, String, String, String, int)
 *
 * Por eso se necesita un Adapter que traduzca entre ambas.
 */
public class GeneradorReporteLegado {

    /**
     * Método de la clase legada: firma TOTALMENTE DIFERENTE al Target.
     * Genera un reporte en formato XML-like usando parámetros individuales
     * (no recibe un objeto Mision, no devuelve en formato JSON).
     *
     * @param id         ID de la misión
     * @param nombre     Nombre de la misión
     * @param ubicacion  Ubicación de la misión
     * @param fecha      Fecha de la misión
     * @param numDrones  Cantidad de drones asignados
     * @return           Reporte en formato XML-like (incompatible con el cliente)
     */
    public String generarReporteXml(String id, String nombre,
                                     String ubicacion, String fecha,
                                     int numDrones) {
        return "<mision>\n" +
               "  <id>" + id + "</id>\n" +
               "  <nombre>" + nombre + "</nombre>\n" +
               "  <ubicacion>" + ubicacion + "</ubicacion>\n" +
               "  <fecha>" + fecha + "</fecha>\n" +
               "  <totalDrones>" + numDrones + "</totalDrones>\n" +
               "</mision>";
    }
}
