package com.drone.servicios;

import com.drone.model.Drone;


/**
 * ============================================================
 * ADAPTER - Patrón Adapter (Adapter de Servicios)
 * ============================================================
 * Implementa la interfaz Target (ExportadorFormato) que espera el Cliente,
 * pero internamente invoca al Adaptee (GeneradorReporteLegado), captura su
 * salida en XML y realiza la TRADUCCIÓN a JSON.
 */
public class MisionJsonAdapter implements ExportadorFormato {
    
    private final GeneradorReporteLegado sistemaLegado;
    private final Drone dronOpcional;

    /**
     * El Adapter ahora recibe el sistema legado (Adaptee) y los datos necesarios,
     * PERO NO recibe el objeto Misión. La misión nace en el sistema legado.
     */
    public MisionJsonAdapter(GeneradorReporteLegado sistemaLegado, Drone dronOpcional) {
        this.sistemaLegado = sistemaLegado;
        this.dronOpcional = dronOpcional;
    }


	@Override
    public String exportar() {
        // 1. OBTENCIÓN: Llamamos al sistema incompatible (XML)
        String xmlRespuesta = sistemaLegado.obtenerReporteXml(dronOpcional);
        
        // 2. TRADUCCIÓN: Convertimos el XML a JSON
        String jsonFinal = convertirXmlAJson(xmlRespuesta);
        
        // 3. GENERACIÓN DE TRAZA PARA LA INTERFAZ
        return "\n========== PATRON ADAPTER (TRADUCTOR DE SERVICIO) ==========\n"
             + "[1. ADAPTEE] El Sistema Legado generó los datos en XML:\n\n"
             + xmlRespuesta + "\n\n"
             + "[2. ADAPTER] Traduciendo etiquetas XML a sintaxis JSON...\n\n"
             + "[3. TARGET] Resultado devuelto al Controlador (Cliente):\n\n"
             + jsonFinal;
    }

    /**
     * Método interno que parsea el XML recibido y lo reconstruye como JSON.
     */
    private String convertirXmlAJson(String xml) {
        // Extraemos valores usando un parser casero simulado
        String id = extraerValor(xml, "id");
        String nombre = extraerValor(xml, "nombre");
        String ubicacion = extraerValor(xml, "ubicacion");
        String fecha = extraerValor(xml, "fecha");
        String totalDrones = extraerValor(xml, "totalDrones");

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\"         : \"").append(id).append("\",\n");
        json.append("  \"nombre\"     : \"").append(nombre).append("\",\n");
        json.append("  \"ubicacion\"  : \"").append(ubicacion).append("\",\n");
        json.append("  \"fecha\"      : \"").append(fecha).append("\",\n");
        
        if (!"0".equals(totalDrones)) {
            // Extraer datos del dron si el totalDrones > 0
            String subXmlDrones = xml.substring(xml.indexOf("<drones>"));
            String dronId = extraerValor(subXmlDrones, "id");
            String dronSerial = extraerValor(subXmlDrones, "serial");
            
            json.append("  \"drones\"     : [\n");
            json.append("    { \"id\": \"").append(dronId).append("\", \"serial\": \"").append(dronSerial).append("\" }\n");
            json.append("  ]\n");
        } else {
            json.append("  \"drones\"     : []\n");
        }
        json.append("}");
        
        return json.toString();
    }

    /**
     * Utilidad simple para extraer el contenido entre etiquetas XML.
     */
    private String extraerValor(String xml, String tag) {
        String tagAbierto = "<" + tag + ">";
        String tagCerrado = "</" + tag + ">";
        int inicio = xml.indexOf(tagAbierto);
        int fin = xml.indexOf(tagCerrado);
        if (inicio == -1 || fin == -1) return "";
        return xml.substring(inicio + tagAbierto.length(), fin);
    }
}
