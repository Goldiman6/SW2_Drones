package com.drone.servicios;

import java.util.List;

/**
 * Servicio encargado de demostrar el ensamblaje dinámico de sensores 
 * en un Dron utilizando el Patrón Composite.
 */
public class GestorSensoresDron {

    /**
     * Recibe una lista de nombres de sensores seleccionados en la UI,
     * busca cada uno en el árbol maestro estático, y los ensambla 
     * dinámicamente en un nuevo GrupoSensor (Composite).
     * Retorna una cadena de texto (Traza) explicando el proceso paso a paso 
     * para evidenciar el funcionamiento real del patrón.
     */
    public static String acoplarYGenerarTexto(List<String> seleccionados, String droneId) {
        if (seleccionados == null || seleccionados.isEmpty() || 
           (seleccionados.size() == 1 && seleccionados.get(0).startsWith("Ninguno"))) {
            return "";
        }

        StringBuilder log = new StringBuilder();
        log.append("\n\n========== PATRON COMPOSITE (TRAZA DE SERVICIO) ==========\n");
        log.append("[Servicio] Inicializando árbol maestro de sensores...\n");
        
        ComponenteSensor arbolMaestro = GeneradorCompositeSensores.crearArbolSensores();
        
        log.append("[Servicio] Creando nuevo GrupoSensor (Composite) para el Dron [").append(droneId).append("]\n");
        ComponenteSensor sensoresDelDron = new GrupoSensor("Sensores Instalados en Dron " + droneId);
        
        for (String nombre : seleccionados) {
            if (nombre.startsWith("Ninguno")) continue; // Evitar procesar el item nulo
            
            log.append("[Servicio] Buscando '").append(nombre).append("' en el árbol maestro... ");
            ComponenteSensor encontrado = buscarEnArbol(arbolMaestro, nombre);
            
            if (encontrado != null) {
                log.append("¡Encontrado!\n");
                log.append("[Servicio] Ejecutando: sensoresDelDron.agregar(encontrado)\n");
                // Aquí demostramos la adición dinámica al Composite
                sensoresDelDron.agregar(encontrado);
            } else {
                log.append("No encontrado.\n");
            }
        }
        
        log.append("\n[Servicio] Extracción exitosa. Recorriendo la estructura dinámica ensamblada mediante polimorfismo (getHijos):\n\n");
        formatearEstructura(sensoresDelDron, 0, log);
        
        return log.toString();
    }
    
    /**
     * Busca recursivamente un componente por nombre en el árbol.
     */
    private static ComponenteSensor buscarEnArbol(ComponenteSensor raiz, String nombre) {
        if (raiz.getNombre().equals(nombre)) return raiz;
        for (ComponenteSensor hijo : raiz.getHijos()) {
            ComponenteSensor encontrado = buscarEnArbol(hijo, nombre);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    /**
     * Recorre la jerarquía demostrando que trata Hojas y Grupos por igual.
     */
    private static void formatearEstructura(ComponenteSensor nodo, int nivel, StringBuilder sb) {
        String indent = "  ".repeat(nivel);
        String tipo = nodo.getHijos().isEmpty() ? "(Hoja)" : "(Grupo)";
        sb.append(indent).append("- ").append(nodo.getNombre()).append(" ").append(tipo).append("\n");
        
        // Polimorfismo del Composite: Si es hoja, no entra al loop. Si es grupo, procesa recursivamente.
        for (ComponenteSensor hijo : nodo.getHijos()) {
            formatearEstructura(hijo, nivel + 1, sb);
        }
    }
}
