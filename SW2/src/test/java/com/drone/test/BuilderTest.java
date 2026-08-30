package com.drone.test;

import com.drone.model.Agricultura;
import com.drone.servicios.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba del Patron Builder.
 * Verifica que el Builder ensambla el objeto correctamente paso a paso.
 */
@DisplayName("Pruebas - Patron Builder")
public class BuilderTest {

    private Builder builder;

    @BeforeEach
    public void setUp() {
        builder = new Builder();
        builder.Reset();
    }

    @Test
    @DisplayName("El Builder debe ensamblar un Dron con todos sus atributos correctos")
    public void testBuildCorrecto() throws Exception {
        String randomId = "BLD-" + (int)(Math.random() * 10000);
        builder.SetId(randomId);
        builder.SetSerial("SER-" + randomId);
        builder.SetModelo("Builder Max");
        builder.SetFabricante("TechCorp");
        builder.SetPeso(25.5);
        builder.SetcapacidadTanque(150.0);

        Agricultura a = builder.buildAndSave();

        assertNotNull(a, "ERROR: El Builder devolvio null.");
        assertEquals(randomId, a.getId(), "El ID ensamblado es incorrecto.");
        assertEquals("SER-" + randomId, a.getSerial(), "El serial ensamblado es incorrecto.");
        assertEquals(25.5, a.getPeso(), "El peso ensamblado es incorrecto.");
        assertEquals(150.0, a.getCapacidadTanque(), "La capacidad ensamblada es incorrecta.");
    }

    @Test
    @DisplayName("Despues de Reset, el Builder debe estar limpio para construir uno nuevo")
    public void testResetLimpiaElBuilder() {
        builder.SetId("VIEJO-ID");
        builder.SetSerial("VIEJO-SERIAL");
        builder.Reset(); // Se reinicia el objeto interno

        String randomId2 = "BLD-" + (int)(Math.random() * 10000);
        
        // Configura uno nuevo limpio
        builder.SetId(randomId2);
        builder.SetSerial("SER-" + randomId2);
        builder.SetModelo("Nuevo Modelo");
        builder.SetFabricante("NuevoFab");
        builder.SetPeso(10.0);
        builder.SetcapacidadTanque(50.0);

        // Si el Reset funciono bien, el ID sera el nuevo y no el viejo
        try {
            Agricultura a = builder.buildAndSave();
            assertEquals(randomId2, a.getId(), "ERROR: El Reset no limpio correctamente el Builder.");
        } catch (Exception e) {
            fail("El buildAndSave lanzo una excepcion inesperada: " + e.getMessage());
        }
    }
}
