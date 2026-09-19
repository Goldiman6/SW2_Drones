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
        
        log.append("[Servicio] Recorriendo y clonando ramas jerárquicas seleccionadas...\n");
        
        // Clonamos recursivamente respetando la estructura jerárquica original
        for (ComponenteSensor hijoMaestro : arbolMaestro.getHijos()) {
            ComponenteSensor ramaClonada = clonarRama(hijoMaestro, seleccionados);
            if (ramaClonada != null) {
                sensoresDelDron.agregar(ramaClonada);
            }
        }
        
        log.append("\n[Servicio] Extracción exitosa. Estructura de árbol generada dinámicamente:\n\n");
        formatearEstructura(sensoresDelDron, 0, log);
        
        return log.toString();
    }
    
    /**
     * Clona el árbol maestro pero solo retiene las hojas seleccionadas y sus grupos padre.
     * Retorna null si la rama no contiene ningún elemento seleccionado.
     */
    private static ComponenteSensor clonarRama(ComponenteSensor original, List<String> seleccionados) {
        // Si es una hoja
        if (original.getHijos().isEmpty()) {
            if (seleccionados.contains(original.getNombre())) {
                return new SensorEstatico(original.getNombre());
            }
            return null;
        }
        
        // Si es un grupo
        GrupoSensor clonGrupo = new GrupoSensor(original.getNombre());
        boolean tieneHijosSeleccionados = false;
        
        for (ComponenteSensor hijoOriginal : original.getHijos()) {
            ComponenteSensor hijoClonado = clonarRama(hijoOriginal, seleccionados);
            if (hijoClonado != null) {
                clonGrupo.agregar(hijoClonado);
                tieneHijosSeleccionados = true;
            }
        }
        
        if (tieneHijosSeleccionados || seleccionados.contains(original.getNombre())) {
            return clonGrupo;
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
        
        // Polimorfismo del Composite
        for (ComponenteSensor hijo : nodo.getHijos()) {
            formatearEstructura(hijo, nivel + 1, sb);
        }
    }
}
