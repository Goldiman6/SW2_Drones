package com.drone.test;

import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.ExportadorFormato;
import com.drone.servicios.GeneradorReporteLegado;
import com.drone.servicios.MisionJsonAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdapterTest {

    @Test
    @DisplayName("Verifica la exportación desde XML legado a JSON sin drones adicionales")
    public void testExportarMisionSinDrones() {
        // 1. PREPARACIÓN
        GeneradorReporteLegado legado = new GeneradorReporteLegado();
        ExportadorFormato adapter = new MisionJsonAdapter(legado, null); // Sin dron adicional

        // 2. EJECUCIÓN
        String trazaResultado = adapter.exportar();

        // 3. VERIFICACIÓN
        assertNotNull(trazaResultado);
        assertTrue(trazaResultado.contains("MSN-LEGACY-001"), "Debe contener el ID de la misión extraído del legado.");
        assertTrue(trazaResultado.contains("Operación Backend Antiguo"));
        
        // Verificar integración XML y traducción final JSON
        assertTrue(trazaResultado.contains("<mision>"), "Debe evidenciarse la respuesta XML del legado.");
        assertTrue(trazaResultado.contains("\"id\"         : \"MSN-LEGACY-001\""), "La salida final debe ser JSON válido.");
        assertTrue(trazaResultado.contains("\"drones\"     : []"), "Debe reflejar arreglo vacío en JSON.");
    }

    @Test
    @DisplayName("Verifica la exportación integrando un Dron de la UI hacia el Legado y luego a JSON")
    public void testExportarMisionConDrones() {
        // 1. PREPARACIÓN
        Drone dronVigilancia = new Vigilancia("100", "SRL-VIG-01", "NightOwl", "FabX", 5.5, true);
        GeneradorReporteLegado legado = new GeneradorReporteLegado();
        ExportadorFormato adapter = new MisionJsonAdapter(legado, dronVigilancia);

        // 2. EJECUCIÓN
        String trazaResultado = adapter.exportar();

        // 3. VERIFICACIÓN
        assertTrue(trazaResultado.contains("<totalDrones>1</totalDrones>"), "El XML legado debe recibir e incrustar el Dron.");
        assertTrue(trazaResultado.contains("\"id\": \"100\""), "El JSON traducido debe conservar el ID del dron.");
        assertTrue(trazaResultado.contains("\"serial\": \"SRL-VIG-01\""));
    }
}
