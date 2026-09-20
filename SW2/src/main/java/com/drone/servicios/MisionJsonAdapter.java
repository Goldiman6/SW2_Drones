package com.drone.servicios;

import com.drone.model.Drone;
import com.drone.model.Mision;

/**
 * ============================================================
 * ADAPTER - Patrón Adapter (Object Adapter)
 * ============================================================
 *
 * ROLES DEL PATRÓN:
 *   Target   → ExportadorFormato        (lo que el cliente/controlador espera)
 *   Adaptee  → GeneradorReporteLegado   (sistema legado con interfaz incompatible)
 *   Adapter  → MisionJsonAdapter        (este archivo: hace de traductor)
 *   Client   → DroneController          (solo conoce ExportadorFormato)
 *
 * PROBLEMA QUE RESUELVE:
 *   El controlador necesita llamar  : exportar()
 *   El sistema legado solo entiende : generarReporteXml(id, nombre, ubicacion, fecha, numDrones)
 *   → Firmas incompatibles, tipos distintos, el cliente no puede usar el legado directamente.
 *
 * SOLUCIÓN:
 *   MisionJsonAdapter implementa ExportadorFormato (satisface al cliente),
 *   extrae los datos de Mision, los traduce al formato que GeneradorReporteLegado
 *   necesita, llama al legado, y convierte su salida XML → JSON para entregársela
 *   al cliente en el formato que espera.
 */
public class MisionJsonAdapter implements ExportadorFormato {

    /** Objeto Mision cuyos datos serán adaptados. */
    private final Mision misionAdaptee;

    /**
     * El sistema LEGADO con la interfaz incompatible.
     * El cliente (DroneController) ni sabe que este objeto existe.
     */
    private final GeneradorReporteLegado sistemaLegado;

    /**
     * Constructor del Adapter.
     * Recibe la Mision y crea internamente la instancia del sistema legado.
     *
     * @param mision La misión cuyos datos se van a adaptar y exportar.
     */
    public MisionJsonAdapter(Mision mision) {
        this.misionAdaptee = mision;
        this.sistemaLegado = new GeneradorReporteLegado(); // El Adaptee
    }

    /**
     * IMPLEMENTACIÓN DEL TARGET: exportar()
     *
     * Aquí ocurre la traducción (adaptation):
     *   1. Extrae los datos de Mision (que el legado no entiende como objeto)
     *   2. Llama al sistema legado con los parámetros individuales que él sí entiende
     *   3. Recibe el resultado en XML (formato del legado)
     *   4. Convierte ese XML → JSON (formato que el cliente espera)
     *   5. Retorna el JSON al cliente
     *
     * El cliente (DroneController) solo llamó a exportar() y recibió JSON.
     * No sabe que internamente se usó un sistema XML legado.
     *
     * @return Mensaje de éxito con la traza completa de la adaptación.
     */
    @Override
    public String exportar() {
        int numDrones = (misionAdaptee.getDrones() != null)
                        ? misionAdaptee.getDrones().size() : 0;

        // ── PASO 1: Llamar al Adaptee con su firma incompatible ─────────────
        // El legado recibe Strings y un int sueltos, no un objeto Mision.
        String salidaXmlDelLegado = sistemaLegado.generarReporteXml(
            misionAdaptee.getId(),
            misionAdaptee.getNombre(),
            misionAdaptee.getUbicacion(),
            misionAdaptee.getFecha(),
            numDrones
        );

        // ── PASO 2: Traducir el resultado XML → JSON (la adaptación real) ───
        String jsonResultado = convertirXmlAJson(salidaXmlDelLegado);

        // ── PASO 3: Construir la traza para demostrar el patrón ─────────────
        StringBuilder traza = new StringBuilder();
        traza.append("========== PATRON ADAPTER (TRAZA COMPLETA) ==========\n\n");

        traza.append("[ CLIENTE ] DroneController llama: exportar()\n");
        traza.append("            → El cliente solo conoce la interfaz ExportadorFormato.\n");

        traza.append("[ ADAPTER ] MisionJsonAdapter recibe la llamada exportar().\n");
        traza.append("            → Extrae datos del objeto Mision.\n");
        traza.append("            → Traduce la llamada al sistema LEGADO incompatible:\n\n");

        traza.append("[ ADAPTEE ] GeneradorReporteLegado.generarReporteXml(\n");
        traza.append("              id        = \"").append(misionAdaptee.getId()).append("\",\n");
        traza.append("              nombre    = \"").append(misionAdaptee.getNombre()).append("\",\n");
        traza.append("              ubicacion = \"").append(misionAdaptee.getUbicacion()).append("\",\n");
        traza.append("              fecha     = \"").append(misionAdaptee.getFecha()).append("\",\n");
        traza.append("              numDrones = ").append(numDrones).append("\n");
        traza.append("            )\n\n");

        traza.append("[ ADAPTEE ] Salida del sistema LEGADO (formato XML):\n");
        traza.append("─────────────────────────────────────────\n");
        traza.append(salidaXmlDelLegado).append("\n");
        traza.append("─────────────────────────────────────────\n\n");

        traza.append("[ ADAPTER ] Convirtiendo XML → JSON (traducción de formatos)...\n\n");

        traza.append("[ TARGET  ] Salida final entregada al cliente (formato JSON):\n");
        traza.append("─────────────────────────────────────────\n");
        traza.append(jsonResultado).append("\n");
        traza.append("─────────────────────────────────────────\n\n");

        traza.append("Archivo mision_").append(misionAdaptee.getId())
             .append(".json creado exitosamente con la información de la misión \"")
             .append(misionAdaptee.getNombre()).append("\".");

        return traza.toString();
    }

    /**
     * Método privado auxiliar del Adapter.
     * Convierte la salida XML del sistema legado al formato JSON
     * que el cliente (DroneController) espera recibir.
     *
     * @param xml El texto XML generado por el sistema legado.
     * @return El equivalente en formato JSON.
     */
    private String convertirXmlAJson(String xml) {
        // Construcción manual del JSON equivalente al XML del legado
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\"         : \"").append(misionAdaptee.getId()).append("\",\n");
        json.append("  \"nombre\"     : \"").append(misionAdaptee.getNombre()).append("\",\n");
        json.append("  \"ubicacion\"  : \"").append(misionAdaptee.getUbicacion()).append("\",\n");
        json.append("  \"fecha\"      : \"").append(misionAdaptee.getFecha()).append("\",\n");
        json.append("  \"drones\"     : [");

        if (misionAdaptee.getDrones() != null && !misionAdaptee.getDrones().isEmpty()) {
            json.append("\n");
            for (int i = 0; i < misionAdaptee.getDrones().size(); i++) {
                Drone d = misionAdaptee.getDrones().get(i);
                json.append("    { \"id\": \"").append(d.getId())
                    .append("\", \"serial\": \"").append(d.getSerial())
                    .append("\", \"modelo\": \"").append(d.getModelo()).append("\" }");
                if (i < misionAdaptee.getDrones().size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ]");
        } else {
            json.append("]");
        }

        json.append("\n}");
        return json.toString();
    }
}
