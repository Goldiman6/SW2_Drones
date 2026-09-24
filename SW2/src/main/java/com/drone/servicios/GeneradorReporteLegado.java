package com.drone.servicios;

import com.drone.model.Drone;

/**
 * ============================================================
 * ADAPTEE - Patrón Adapter
 * ============================================================
 * Representa un sistema antiguo o externo que procesa datos de
 * misiones y DEVUELVE ÚNICAMENTE FORMATO XML.
 *
 * El Cliente (Controlador) no sabe interactuar con esto porque
 * el Cliente espera recibir un formato JSON.
 */
public class GeneradorReporteLegado {

    /**
     * Simula una consulta a una base de datos antigua que escupe un XML.
     * 
     * @param dronAdicional Dron opcional seleccionado en la UI para incluir en la misión.
     * @return String con estructura XML estricta.
     */
    public String obtenerReporteXml(Drone dronAdicional) {
        StringBuilder xml = new StringBuilder();
        xml.append("<mision>\n");
        xml.append("  <id>MSN-LEGACY-001</id>\n");
        xml.append("  <nombre>Operación Backend Antiguo</nombre>\n");
        xml.append("  <ubicacion>Base de Datos Oculta</ubicacion>\n");
        xml.append("  <fecha>2026-10-31</fecha>\n");
        
        if (dronAdicional != null) {
            xml.append("  <totalDrones>1</totalDrones>\n");
            xml.append("  <drones>\n");
            xml.append("    <dron>\n");
            xml.append("      <id>").append(dronAdicional.getId()).append("</id>\n");
            xml.append("      <serial>").append(dronAdicional.getSerial()).append("</serial>\n");
            xml.append("    </dron>\n");
            xml.append("  </drones>\n");
        } else {
            xml.append("  <totalDrones>0</totalDrones>\n");
            xml.append("  <drones></drones>\n");
        }
        xml.append("</mision>");
        
        return xml.toString();
    }
}
