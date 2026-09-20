package com.drone.test;

import com.drone.model.Drone;
import com.drone.model.Mision;
import com.drone.model.Vigilancia;
import com.drone.servicios.ExportadorFormato;
import com.drone.servicios.MisionJsonAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios del Patrón Adapter.
 *
 * Verifica que el MisionJsonAdapter envuelva correctamente la Mision,
 * llame internamente al sistema legado (Adaptee) y traduzca correctamente
 * el formato XML resultante al formato JSON esperado por el cliente (Target).
 */
public class AdapterTest {

    @Test
    @DisplayName("Verifica la exportación de una Misión sin drones asignados")
    public void testExportarMisionSinDrones() {
        // 1. PREPARACIÓN (Arrange)
        // Creamos una misión básica sin drones
        List<Drone> dronesVacios = new ArrayList<>();
        Mision mision = new Mision(
                "MSN-TEST-1", 
                "Misión Alpha", 
                "Coordenadas X", 
                "2026-09-19", 
                dronesVacios
        );
        
        // Instanciamos el Adapter inyectando la misión
        ExportadorFormato adapter = new MisionJsonAdapter(mision);

        // 2. EJECUCIÓN (Act)
        String trazaResultado = adapter.exportar();

        // 3. VERIFICACIÓN (Assert)
        assertNotNull(trazaResultado, "El resultado de la exportación no debe ser nulo.");
        
        // Verificar que los datos principales estén presentes
        assertTrue(trazaResultado.contains("MSN-TEST-1"), "Debe contener el ID de la misión.");
        assertTrue(trazaResultado.contains("Misión Alpha"), "Debe contener el nombre de la misión.");
        
        // Verificar que el Adapter se comunicó con el sistema LEGADO (Adaptee -> XML)
        assertTrue(trazaResultado.contains("<mision>"), "Debe evidenciarse la llamada al formato XML del sistema legado.");
        assertTrue(trazaResultado.contains("<totalDrones>0</totalDrones>"), "El XML legado debe registrar 0 drones.");
        
        // Verificar que la salida FINAL está en formato JSON
        assertTrue(trazaResultado.contains("\"id\"         : \"MSN-TEST-1\""), "La salida final debe tener formato JSON.");
        assertTrue(trazaResultado.contains("\"drones\"     : []"), "El JSON final debe reflejar un arreglo de drones vacío.");
    }

    @Test
    @DisplayName("Verifica la exportación de una Misión con drones asignados")
    public void testExportarMisionConDrones() {
        // 1. PREPARACIÓN (Arrange)
        // Creamos una misión con un dron de prueba
        List<Drone> drones = new ArrayList<>();
        Drone dronVigilancia = new Vigilancia("100", "SRL-VIG-01", "NightOwl", "FabX", 5.5, true);
        drones.add(dronVigilancia);
        
        Mision mision = new Mision(
                "MSN-TEST-2", 
                "Misión Beta", 
                "Coordenadas Y", 
                "2026-10-01", 
                drones
        );
        
        ExportadorFormato adapter = new MisionJsonAdapter(mision);

        // 2. EJECUCIÓN (Act)
        String trazaResultado = adapter.exportar();

        // 3. VERIFICACIÓN (Assert)
        // Verificar integración con legado
        assertTrue(trazaResultado.contains("<totalDrones>1</totalDrones>"), "El XML legado debe registrar 1 dron.");
        
        // Verificar que la traducción XML -> JSON conservó los datos del Dron
        assertTrue(trazaResultado.contains("\"id\": \"100\""), "El JSON debe incluir el ID del dron de la misión.");
        assertTrue(trazaResultado.contains("\"serial\": \"SRL-VIG-01\""), "El JSON debe incluir el serial del dron.");
        assertTrue(trazaResultado.contains("\"modelo\": \"NightOwl\""), "El JSON debe incluir el modelo del dron.");
    }
}
