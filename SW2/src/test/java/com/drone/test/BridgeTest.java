package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.ControlAutonomo;
import com.drone.servicios.ControlBasico;
import com.drone.servicios.TipoControl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios del Patron Bridge.
 *
 * Verifica que la abstraccion de control (TipoControl) y sus
 * implementaciones concretas (ControlBasico, ControlAutonomo)
 * funcionen independientemente del tipo de Dron (Agricultura/Vigilancia)
 * sin modificar su estructura interna.
 */
@DisplayName("Pruebas - Patron Bridge (Tipos de Control)")
public class BridgeTest {

    @Test
    @DisplayName("Control Basico debe generar el reporte correcto para un Dron de Agricultura")
    public void testControlBasicoConAgricultura() {
        // 1. Preparacion (Arrange)
        Drone dronAgr = new Agricultura("BR-01", "SER-AGR-001", "AgriMax", "AgriCorp", 15.5, 20.0);
        TipoControl control = new ControlBasico();

        // 2. Accion (Act)
        String resultado = control.configurar(dronAgr);

        // 3. Verificacion (Assert)
        assertNotNull(resultado, "El resultado de configurar no debe ser null.");
        assertTrue(resultado.contains("Control Basico (Manual)"), "Debe indicar que es Control Basico.");
        assertTrue(resultado.contains("AgriMax"), "El reporte debe contener el modelo del dron.");
        assertTrue(resultado.contains("BR-01"), "El reporte debe contener el ID del dron.");
        assertTrue(resultado.contains("Piloto:  Requerido"), "El control basico requiere piloto.");
    }

    @Test
    @DisplayName("Control Autonomo debe generar el reporte correcto para un Dron de Vigilancia")
    public void testControlAutonomoConVigilancia() {
        // 1. Preparacion (Arrange)
        Drone dronVig = new Vigilancia("BR-02", "SER-VIG-002", "SkyWatcher", "SkyCorp", 5.0, true);
        TipoControl control = new ControlAutonomo();

        // 2. Accion (Act)
        String resultado = control.configurar(dronVig);

        // 3. Verificacion (Assert)
        assertNotNull(resultado, "El resultado de configurar no debe ser null.");
        assertTrue(resultado.contains("Control Autonomo (IA)"), "Debe indicar que es Control Autonomo.");
        assertTrue(resultado.contains("SkyWatcher"), "El reporte debe contener el modelo del dron.");
        assertTrue(resultado.contains("BR-02"), "El reporte debe contener el ID del dron.");
        assertTrue(resultado.contains("Piloto:  No requerido"), "El control autonomo no requiere piloto.");
    }

    @Test
    @DisplayName("Polimorfismo Bridge: Intercambiar controles en tiempo de ejecucion")
    public void testPolimorfismoBridge() {
        // Demuestra la esencia del Bridge: el dron y el control pueden variar independientemente
        Drone dronBase = new Agricultura("BR-03", "SER-TEST", "DroneTest", "TestCorp", 1.0, 1.0);
        
        // Usando el mismo Dron, le asignamos Control Basico
        TipoControl control = new ControlBasico();
        String reporteBasico = control.configurar(dronBase);
        assertTrue(reporteBasico.contains("Radiocontrol por frecuencia"));

        // Usando el mismo Dron, ahora le asignamos Control Autonomo dinamicamente
        control = new ControlAutonomo();
        String reporteAutonomo = control.configurar(dronBase);
        assertTrue(reporteAutonomo.contains("Planificacion de rutas con GPS"));
        
        // Verificamos que el dron sigue siendo el mismo y no sufrio cambios estructurales
        assertEquals("DroneTest", dronBase.getModelo(), "El modelo del dron original no debe alterarse.");
    }
}
