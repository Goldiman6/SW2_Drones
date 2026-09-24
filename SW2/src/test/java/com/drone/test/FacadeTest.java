package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.model.Drone;
import com.drone.model.Vigilancia;
import com.drone.servicios.SistemaDronesFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios del Patron Facade.
 *
 * Verifica que SistemaDronesFacade exponga UN UNICO metodo publico
 * (generarDiagnosticoCompleto) que orqueste internamente los 3 subsistemas:
 *   - Bridge      (TipoControl)
 *   - Decorator   (BateriaDecorator)
 *   - Composite   (GestorSensoresDron)
 *
 * El cliente (DroneController) solo interactua con el Facade.
 */
@DisplayName("Pruebas - Patron Facade (SistemaDronesFacade)")
public class FacadeTest {

    // Dron reutilizado en la mayoria de pruebas
    private Drone dronPrueba;

    @BeforeEach
    public void setUp() {
        // Preparamos un Dron de Agricultura como objeto de prueba base
        dronPrueba = new Agricultura("FAC-01", "SER-FAC-001", "AgriTest", "TestCorp", 10.0, 25.0);
    }

    // ================================================================
    // GRUPO 1: Verificar que el Facade consolida la salida
    // ================================================================

    @Test
    @DisplayName("El Facade debe retornar un String no nulo y no vacio en cualquier combinacion")
    public void testFacadeRetornaStringNoNulo() {
        SistemaDronesFacade facade = new SistemaDronesFacade("basico", true, dronPrueba);
        List<String> sensores = Arrays.asList("Sensor Infrarrojo", "RTD");

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, sensores);

        assertNotNull(resultado, "El Facade nunca debe retornar null.");
        assertFalse(resultado.isBlank(), "El Facade nunca debe retornar una cadena vacia.");
    }

    @Test
    @DisplayName("El Facade debe incluir el encabezado general en su salida")
    public void testFacadeIncluyeEncabezado() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("PATRON FACADE"),
                "La salida del Facade debe contener su encabezado identificador.");
    }

    // ================================================================
    // GRUPO 2: Subsistema 1 - BRIDGE
    // ================================================================

    @Test
    @DisplayName("Facade con 'basico' activa el subsistema Bridge con Control Basico")
    public void testFacadeActivaBridgeBasico() {
        SistemaDronesFacade facade = new SistemaDronesFacade("basico", false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("Control Basico (Manual)"),
                "El Facade debe delegar al Bridge con ControlBasico cuando se elige 'basico'.");
    }

    @Test
    @DisplayName("Facade con 'autonomo' activa el subsistema Bridge con Control Autonomo")
    public void testFacadeActivaBridgeAutonomo() {
        SistemaDronesFacade facade = new SistemaDronesFacade("autonomo", false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("Control Autonomo (IA)"),
                "El Facade debe delegar al Bridge con ControlAutonomo cuando se elige 'autonomo'.");
    }

    @Test
    @DisplayName("Facade sin tipo de control reporta Bridge como omitido")
    public void testFacadeOmiteBridgeCuandoTipoEsNull() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("[BRIDGE] Omitido"),
                "Cuando no hay tipo de control, el Facade debe reportar Bridge como Omitido.");
    }

    @Test
    @DisplayName("Facade es insensible a mayusculas en el tipo de control")
    public void testFacadeEsInsensibleAMayusculas() {
        SistemaDronesFacade facadeUpper = new SistemaDronesFacade("BASICO", false, dronPrueba);
        SistemaDronesFacade facadeMixed = new SistemaDronesFacade("Autonomo", false, dronPrueba);

        String resBasico = facadeUpper.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());
        String resAutonomo = facadeMixed.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resBasico.contains("Control Basico (Manual)"),
                "El Facade debe reconocer 'BASICO' en mayusculas.");
        assertTrue(resAutonomo.contains("Control Autonomo (IA)"),
                "El Facade debe reconocer 'Autonomo' en formato mixto.");
    }

    // ================================================================
    // GRUPO 3: Subsistema 2 - DECORATOR
    // ================================================================

    @Test
    @DisplayName("Facade con bateria activa el subsistema Decorator (BateriaDecorator)")
    public void testFacadeActivaDecorator() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, true, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("Bateria de Alta Capacidad"),
                "El Facade debe delegar al BateriaDecorator cuando usaBateria=true.");
        assertFalse(resultado.contains("[DECORATOR] Omitido"),
                "No debe reportar Decorator como omitido cuando se activo.");
    }

    @Test
    @DisplayName("Facade sin bateria reporta Decorator como omitido")
    public void testFacadeOmiteDecoratorSinBateria() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("[DECORATOR] Omitido"),
                "Cuando usaBateria=false, el Facade debe reportar Decorator como Omitido.");
    }

    @Test
    @DisplayName("Facade con bateria pero drone null no debe lanzar excepcion")
    public void testFacadeConBateriaYDroneNullNoLanzaExcepcion() {
        // Si el drone del constructor es null, no se puede crear el decorador
        // El Facade debe manejar esto sin explotar
        assertDoesNotThrow(() -> {
            SistemaDronesFacade facade = new SistemaDronesFacade(null, true, null);
            facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());
        }, "El Facade no debe lanzar excepcion si el drone del constructor fue null.");
    }

    // ================================================================
    // GRUPO 4: Subsistema 3 - COMPOSITE
    // ================================================================

    @Test
    @DisplayName("Facade con sensores activa el subsistema Composite (GestorSensoresDron)")
    public void testFacadeActivaComposite() {
        List<String> sensores = Arrays.asList("Sensor Infrarrojo", "RTD");
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, sensores);

        assertTrue(resultado.contains("[COMPOSITE]"),
                "El Facade debe activar el Composite cuando hay sensores seleccionados.");
        assertTrue(resultado.contains("Sensor Infrarrojo"),
                "La traza del Composite debe incluir los sensores seleccionados.");
    }

    @Test
    @DisplayName("Facade sin sensores reporta Composite como omitido")
    public void testFacadeOmiteCompositeSinSensores() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("[COMPOSITE] Omitido"),
                "Cuando la lista de sensores esta vacia, el Composite debe reportarse como omitido.");
    }

    @Test
    @DisplayName("Facade con lista de sensores null reporta Composite como omitido")
    public void testFacadeOmiteCompositeConSensoresNull() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, null);

        assertTrue(resultado.contains("[COMPOSITE] Omitido"),
                "Con sensores=null, el Composite debe reportarse como omitido sin lanzar excepcion.");
    }

    // ================================================================
    // GRUPO 5: Orquestacion completa (los 3 subsistemas activos)
    // ================================================================

    @Test
    @DisplayName("Facade con todos los subsistemas activos produce un reporte completo")
    public void testFacadeOrquestaLosTresSubsistemas() {
        List<String> sensores = Arrays.asList("Sensor CMOS", "SPI");
        SistemaDronesFacade facade = new SistemaDronesFacade("basico", true, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, sensores);

        // Los 3 subsistemas deben estar presentes
        assertTrue(resultado.contains("Control Basico (Manual)"),   "[Bridge] debe estar activo.");
        assertTrue(resultado.contains("Bateria de Alta Capacidad"),  "[Decorator] debe estar activo.");
        assertTrue(resultado.contains("[COMPOSITE]"),                "[Composite] debe estar activo.");

        // Ninguno debe reportar "Omitido"
        assertFalse(resultado.contains("[BRIDGE] Omitido"),     "Bridge no debe estar omitido.");
        assertFalse(resultado.contains("[DECORATOR] Omitido"),  "Decorator no debe estar omitido.");
        assertFalse(resultado.contains("[COMPOSITE] Omitido"),  "Composite no debe estar omitido.");
    }

    @Test
    @DisplayName("Facade con ningun subsistema activo reporta todo como omitido")
    public void testFacadeTodoOmitido() {
        SistemaDronesFacade facade = new SistemaDronesFacade(null, false, dronPrueba);

        String resultado = facade.generarDiagnosticoCompleto(dronPrueba, Collections.emptyList());

        assertTrue(resultado.contains("[BRIDGE] Omitido"),    "Bridge debe estar omitido.");
        assertTrue(resultado.contains("[DECORATOR] Omitido"), "Decorator debe estar omitido.");
        assertTrue(resultado.contains("[COMPOSITE] Omitido"), "Composite debe estar omitido.");
    }

    @Test
    @DisplayName("Facade funciona correctamente con un Drone de Vigilancia")
    public void testFacadeConDroneVigilancia() {
        Drone dronVig = new Vigilancia("FAC-02", "SER-VIG-002", "SkyPro", "SkyCorp", 5.0, true);
        List<String> sensores = Arrays.asList("UART");
        SistemaDronesFacade facade = new SistemaDronesFacade("autonomo", true, dronVig);

        String resultado = facade.generarDiagnosticoCompleto(dronVig, sensores);

        assertTrue(resultado.contains("Control Autonomo (IA)"), "Debe activar Bridge autonomo.");
        assertTrue(resultado.contains("SkyPro"), "El reporte debe incluir el modelo del Dron.");
        assertTrue(resultado.contains("UART"), "El Composite debe incluir el sensor UART.");
    }
}
