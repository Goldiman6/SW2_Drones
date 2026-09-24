package com.drone.servicios;

import com.drone.model.Drone;
import java.util.List;

/**
 * ============================================================
 * FACADE - Patrón Facade (GoF)
 * ============================================================
 * Proporciona una interfaz ÚNICA y SIMPLIFICADA que orquesta
 * internamente TRES subsistemas de patrones de diseño.
 * 
 * La Facade NO genera la lógica ni los detalles de cada patrón;
 * simplemente llama a las clases correspondientes (que sí contienen
 * su propia lógica) y consolida sus respuestas.
 *
 * Estructura GoF:
 *   Client -> SistemaDronesFacade -> [Bridge | Decorator | Composite]
 */
public class SistemaDronesFacade {

    private final TipoControl    subsistemaControl;    // Bridge
    private final DroneComponent subsistemaDecorator;  // Decorator

    public SistemaDronesFacade(String tipoControl, boolean usaBateria, Drone drone) {
        // [Subsistema 1] Bridge
        if ("basico".equalsIgnoreCase(tipoControl)) {
            this.subsistemaControl = new ControlBasico();
        } else if ("autonomo".equalsIgnoreCase(tipoControl)) {
            this.subsistemaControl = new ControlAutonomo();
        } else {
            this.subsistemaControl = null;
        }

        // [Subsistema 2] Decorator
        if (usaBateria && drone != null) {
            DroneComponent base = new DroneBasico(drone);
            this.subsistemaDecorator = new BateriaDecorator(base);
        } else {
            this.subsistemaDecorator = null;
        }
    }

    /**
     * ============================================================
     * MÉTODO ÚNICO DE LA FACADE
     * ============================================================
     * Invoca directamente a las clases de los patrones y recolecta
     * el texto que ELLAS generan, unificándolo para la Vista.
     */
    public String generarDiagnosticoCompleto(Drone drone, List<String> sensoresSeleccionados) {
        StringBuilder traza = new StringBuilder();

        traza.append("PATRON FACADE - Reporte Unificado de Subsistemas\n");
        traza.append("==================================================\n");

        // 1. LLAMADO AL SUBSISTEMA BRIDGE (Las clases concretas generan su propio texto)
        if (subsistemaControl != null) {
            traza.append(subsistemaControl.configurar(drone)).append("\n");
        } else {
            traza.append("\n[BRIDGE] Omitido - No se selecciono tipo de control.\n");
        }

        // 2. LLAMADO AL SUBSISTEMA DECORATOR (El Decorador genera su propio texto)
        if (subsistemaDecorator != null) {
            traza.append("\n[DECORATOR] ").append(subsistemaDecorator.getDescription()).append("\n");
        } else {
            traza.append("\n[DECORATOR] Omitido - Bateria extra no seleccionada.\n");
        }

        // 3. LLAMADO AL SUBSISTEMA COMPOSITE (El Gestor genera su propio arbol)
        if (sensoresSeleccionados != null && !sensoresSeleccionados.isEmpty()) {
            traza.append("\n[COMPOSITE] \n");
            traza.append(GestorSensoresDron.acoplarYGenerarTexto(sensoresSeleccionados, drone.getId()));
        } else {
            traza.append("\n[COMPOSITE] Omitido - Ningun sensor seleccionado.\n");
        }

        return traza.toString();
    }
}
